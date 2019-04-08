import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SkyscrapperProblem implements Problem {
    private static final String SKYSCRAPPER_OPENING_ERROR = "Couldn't read Skyscrapper file from %s";
    private static final String SKYSCRAPPER_FORMAT_ERROR = "Incorrectly formatted Skyscrapper file %s in line number: %d";
    private static final String DELIMITER = ";";
    private static final String RESULT_LOCATION = "results/";
    private static final String TEST_FILES_LOCATION = "test_files/";

    private int boardSize;
    private ArrayList<ArrayList<Variable>> variablesBoard;
    private List<Variable> variablesFlat;
    private List<Constraint> constraints;
    private List<Integer> wholeDomain;

    private List<String> savedSolutions;
    private String filename;
    private boolean isLoaded;

    public SkyscrapperProblem() {
        savedSolutions = new ArrayList<>();
        isLoaded = false;
    }

    @Override
    public void load(String filename) {
        String pathName = TEST_FILES_LOCATION + filename;
        System.out.println(pathName);
        File problemFile = new File(pathName);
        if(problemFile.exists() && !problemFile.isDirectory() && problemFile.canRead()){
            int lineNumber = 0;
            try {
                List<String> lines = Files.readAllLines(Paths.get(pathName));
                boardSize = Integer.valueOf(lines.get(lineNumber));
                lineNumber++;
                initDomain();
                initBoard();

                this.variablesFlat = variablesBoard.stream().flatMap(List::stream).collect(Collectors.toList());


                constraints = new ArrayList<>();
                initRowAndColumnConstraints();

                List<List<Integer>> skyscrapperConstraints = new ArrayList<>(4);
                for(int i = 0; i < 4; i++){
                    String[] sideConstraints = lines.get(lineNumber++).split(DELIMITER);
                    skyscrapperConstraints.add(Arrays.stream(sideConstraints).skip(1).map(Integer::valueOf).collect(Collectors.toList()));
                }

                initSkyscrapperConstraints(skyscrapperConstraints);


                this.filename = filename;
                isLoaded = true;

            } catch (IOException e) {
                System.err.println(String.format(SKYSCRAPPER_OPENING_ERROR, pathName));
                e.printStackTrace();
            } catch (NumberFormatException | IndexOutOfBoundsException e){
                System.err.println(String.format(SKYSCRAPPER_FORMAT_ERROR, pathName, lineNumber));
                e.printStackTrace();
            }
        } else {
            System.err.println(String.format(SKYSCRAPPER_OPENING_ERROR, pathName));
        }
    }

    @Override
    public boolean isLoaded() {
        return isLoaded;
    }

    private void initSkyscrapperConstraints(List<List<Integer>> sideConstraints){
        List<Integer> topConstraints = sideConstraints.get(0);
        List<Integer> bottomConstraints = sideConstraints.get(1);
        List<Integer> leftConstraints = sideConstraints.get(2);
        List<Integer> rightConstraints = sideConstraints.get(3);

        for(int i = 0; i < boardSize; i++){
            ArrayList<Variable> rowVariables = variablesBoard.get(i);
            int columnNumber = i;
            ArrayList<Variable> columnVariables = variablesBoard.stream()
                    .map(row -> row.get(columnNumber))
                    .collect(Collectors.toCollection(ArrayList::new));

            Constraint columnConstraint = new SkyscrapperConstraint(columnVariables, topConstraints.get(i), bottomConstraints.get(i), wholeDomain);
            Constraint rowConstraint = new SkyscrapperConstraint(rowVariables, leftConstraints.get(i), rightConstraints.get(i), wholeDomain);

            rowVariables.forEach(variable -> variable.addConstraint(rowConstraint));
            columnVariables.forEach(variable -> variable.addConstraint(columnConstraint));
            constraints.add(rowConstraint);
            constraints.add(columnConstraint);
        }
    }

    private void initRowAndColumnConstraints() {
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

    private void initBoard() {
        variablesFlat = new ArrayList<>(boardSize * boardSize);
        variablesBoard = new ArrayList<>(boardSize);
        for(int i = 0; i < boardSize; i++){
            variablesBoard.add(new ArrayList<>(boardSize));
            for(int j = 0; j < boardSize; j++){
                variablesBoard.get(i).add(new Variable(wholeDomain));
            }
        }

    }

    private void initDomain() {
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
    public void saveCurrentSolution() {
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
    }

    @Override
    public void saveSolutionsToFile() {
        String fileLocation = RESULT_LOCATION + filename.substring(0, filename.indexOf('.')) + ".html";
        try (PrintWriter writer = new PrintWriter(fileLocation)) {
            writer.println("<html><body><head><link rel=\"stylesheet\" type=\"text/css\" href=\"styles.css\"></head>");
            for (String savedSolution : savedSolutions) {
                writer.println(savedSolution);
            }
            writer.println("</html></body>");
        } catch (IOException e) {
            System.err.println("Couldn't write to a file");
        }
    }
}
