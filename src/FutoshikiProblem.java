import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FutoshikiProblem extends TabularProblem {
    private static final String FUTOSHIKI_OPENING_ERROR = "Couldn't read Futoshiki file from %s";
    private static final String FUTOSHIKI_FORMAT_ERROR = "Incorrectly formatted Futoshiki file %s in line number: %d";
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public FutoshikiProblem(String resultFolderName) {
        super(resultFolderName);
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


    private void initVariableContainers() {
        variablesFlat = new ArrayList<>(boardSize * boardSize);
        variablesBoard = new ArrayList<>(boardSize);
        for(int i = 0; i < boardSize; i++){
            variablesBoard.add(new ArrayList<>(boardSize));
        }
    }

    private static int indexInAlphabet(char character){
        return ALPHABET.indexOf(character);
    }
}
