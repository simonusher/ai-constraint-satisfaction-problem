import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class TabularProblem  implements Problem {
    protected static final String RESULT_LOCATION = "results/";
    protected static final String DELIMITER = ";";
    protected static final String TEST_FILES_LOCATION = "test_files/";
    protected int boardSize;
    protected ArrayList<ArrayList<Variable>> variablesBoard;
    protected List<Variable> variablesFlat;
    protected List<Constraint> constraints;
    protected List<Integer> wholeDomain;

    protected List<String> savedSolutions;
    protected List<Long> elapsedTimesToFind;
    protected List<Integer> numbersOfCallsToFind;

    protected String filename;
    protected boolean isLoaded;

    private static final float TIME_UNIT = 1000000000f;

    public TabularProblem() {
        savedSolutions = new ArrayList<>();
        numbersOfCallsToFind = new ArrayList<>();
        elapsedTimesToFind = new ArrayList<>();
        isLoaded = false;
    }

    @Override
    public boolean isLoaded() {
        return isLoaded;
    }

    protected void initRowAndColumnConstraints() {
        for(int i = 0; i < boardSize; i++){
            List<Variable> rowVariables = variablesBoard.get(i);
            int columnNumber = i;
            List<Variable> columnVariables = variablesBoard.stream().map(row -> row.get(columnNumber)).collect(Collectors.toList());

            Constraint rowConstraint = new UnequalityConstraint(rowVariables);
            Constraint columnConstraint = new UnequalityConstraint(columnVariables);

            rowVariables.forEach(variable -> variable.addConstraint(rowConstraint));
            columnVariables.forEach(variable -> variable.addConstraint(columnConstraint));
            constraints.add(rowConstraint);
            constraints.add(columnConstraint);
        }
    }

    protected void initDomain() {
        this.wholeDomain = IntStream.range(1, boardSize + 1).boxed().collect(Collectors.toList());
    }


    @Override
    public List<Constraint> getConstraints() {
        return constraints;
    }

    @Override
    public List<Variable> getVariables() {
        return variablesFlat;
    }

    @Override
    public List<Variable> getUnfixedVariables() {
        return getVariables().stream().filter(variable -> !variable.isFixed()).collect(Collectors.toList());
    }

    @Override
    public Collection<Integer> getWholeDomain() {
        return wholeDomain;
    }

    @Override
    public void saveCurrentSolution(int numberOfCalls, Long elapsedTime) {
        StringBuilder builder = new StringBuilder();
        builder.append("<table>");
        for(int i = 0; i < boardSize; i++){
            builder.append("<tr>");
            for(int j = 0; j < boardSize; j++) {
                builder.append("<td>");
                builder.append(variablesBoard.get(i).get(j).getValue());
                builder.append("</td>");
            }
            builder.append("</tr>");
        }
        builder.append("</table>");
        savedSolutions.add(builder.toString());
        this.numbersOfCallsToFind.add(numberOfCalls);
        this.elapsedTimesToFind.add(elapsedTime);
    }

    @Override
    public void saveSolutionsToFile(int numberOfCalls, Long elapsedTime) {
        String problemName = filename.substring(0, filename.indexOf('.'));
        String fileLocation = RESULT_LOCATION + problemName + ".html";
        try (PrintWriter writer = new PrintWriter(fileLocation)) {
            writer.println("<!DOCTYPE html><html><head><title>Results</title><link rel=\"stylesheet\" type=\"text/css\" href=\"styles.css\"></head><body>");
            writer.println("<h1>Problem: " + problemName + "</h1>");
            writer.println("<div>Time: " + elapsedTime / TIME_UNIT + "s</div>");
            writer.println("<div>Number of calls: " + numberOfCalls + "</div>");
            for(int i = 0; i < savedSolutions.size(); i++){
                writer.println("<h3>Solution number: " + i + "</h3>");
                writer.println("<div>Calls to find:" + numbersOfCallsToFind.get(i) + "</div>");
                writer.println("<div>Time to find:" + elapsedTimesToFind.get(i) / TIME_UNIT + "s</div>");
                writer.println(savedSolutions.get(i));
            }
            writer.println("</body></html>");
        } catch (IOException e) {
            System.err.println("Couldn't write to a file");
        }
    }
}
