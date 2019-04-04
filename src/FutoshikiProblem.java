import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FutoshikiProblem implements Problem {
    private static final String FUTOSHIKI_OPENING_ERROR = "Couldn't read Futoshiki file from %s";
    private static final String FUTOSHIKI_FORMAT_ERROR = "Incorrectly formatted Futoshiki file %s in line number: %d";
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DELIMITER = ";";
//    private static final String RESULT_LOCATION = "\\results\\";
    private static final String RESULT_LOCATION = "results/";
    private static final String TEST_FILES_LOCATION = "test_files/";

    private int boardSize;
    private List<List<Variable>> variablesBoard;
    private List<Variable> variablesFlat;
    private List<Constraint> constraints;
    private List<Integer> wholeDomain;

    private List<String> savedSolutions;
    private String filename;
    private boolean isLoaded;

    public FutoshikiProblem() {
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
                initVariableContainers();
                initDomain();
                lineNumber += 2; //SKIP "START:" line


                String[] tokens;
                for(int i = 0; i < boardSize; i++, lineNumber++) {
                    tokens = lines.get(lineNumber).split(DELIMITER);
                    for(int j = 0; j < boardSize; j++) {
                       int value = Integer.valueOf(tokens[j]);
                       if(value == 0){
                           variablesBoard.get(i).add(new Variable(wholeDomain));
                           variablesBoard.get(i).get(j).id = String.valueOf(ALPHABET.charAt(i)) + (j+1);
                       } else {
                           variablesBoard.get(i).add(new Variable(wholeDomain, value));
                           variablesBoard.get(i).get(j).id = String.valueOf(ALPHABET.charAt(i)) + (j+1);
                       }
                    }
                }
                this.variablesFlat = variablesBoard.stream().flatMap(List::stream).collect(Collectors.toList());

                lineNumber += 1; //SKIP "REL:" line

                constraints = new ArrayList<>();
                initRowAndColumnConstraints();
                for(int i = lineNumber; i < lines.size(); i++, lineNumber++) {
                    tokens = lines.get(i).split(DELIMITER);
                    int firstRowIndex = indexInAlphabet(tokens[0].charAt(0));
                    int firstColumnIndex = Character.getNumericValue(tokens[0].charAt(1)) - 1;
                    int secondRowIndex = indexInAlphabet(tokens[1].charAt(0));
                    int secondColumnIndex = Character.getNumericValue(tokens[1].charAt(1)) - 1;

                    Variable smallerVariable = variablesBoard.get(firstRowIndex).get(firstColumnIndex);
                    Variable greaterVariable = variablesBoard.get(secondRowIndex).get(secondColumnIndex);
                    SmallerThanConstraint constraint = new SmallerThanConstraint(greaterVariable, smallerVariable, wholeDomain);
                    constraints.add(constraint);
                    smallerVariable.addConstraint(constraint);
                    greaterVariable.addConstraint(constraint);
                }

                this.filename = filename;
                isLoaded = true;

            } catch (IOException e) {
                System.err.println(String.format(FUTOSHIKI_OPENING_ERROR, pathName));
                e.printStackTrace();
            } catch (NumberFormatException | IndexOutOfBoundsException e){
                System.err.println(String.format(FUTOSHIKI_FORMAT_ERROR, pathName, lineNumber));
                e.printStackTrace();
            }
        } else {
            System.err.println(String.format(FUTOSHIKI_OPENING_ERROR, pathName));
        }
    }

    @Override
    public boolean isLoaded() {
        return isLoaded;
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

    private void initVariableContainers() {
        variablesFlat = new ArrayList<>(boardSize * boardSize);
        variablesBoard = new ArrayList<>(boardSize);
        for(int i = 0; i < boardSize; i++){
            variablesBoard.add(new ArrayList<>(boardSize));
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

    private static int indexInAlphabet(char character){
        return ALPHABET.indexOf(character);
    }
}
