import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SudokuProblem extends TabularProblem {
    private static final String SUDOKU_OPENING_ERROR = "Couldn't read Sudoku file from %s";
    private static final String SUDOKU_FORMAT_ERROR = "Incorrectly formatted Sudoku file %s in line number: %d";
    private static final int DEFAULT_SUDOKU_SIZE = 9;

    private static final int DEFAULT_SUDOKU_SQUARE_SIZE = 3;

    private int squareSize;

    public SudokuProblem(String resultFolderName) {
        super(resultFolderName);
    }

    @Override
    public void load(String filename) {
        boardSize = DEFAULT_SUDOKU_SIZE;
        squareSize = DEFAULT_SUDOKU_SQUARE_SIZE;
        String pathName = TEST_FILES_LOCATION + filename;
        System.out.println(pathName);
        File problemFile = new File(pathName);
        if(problemFile.exists() && !problemFile.isDirectory() && problemFile.canRead()){
            int lineNumber = 0;
            try {
                initDomain();
                initVariableContainers();
                List<String> lines = Files.readAllLines(Paths.get(pathName));

                String[] tokens;
                for(int i = 0; i < boardSize; i++, lineNumber++){
                    tokens = lines.get(lineNumber).split(DELIMITER);
                    for(int j = 0; j < boardSize; j++) {
                        int value = Integer.valueOf(tokens[j]);
                        if(value == 0){
                            variablesBoard.get(i).add(new Variable(wholeDomain));
                        } else {
                            variablesBoard.get(i).add(new Variable(wholeDomain, value));
                        }
                    }
                }

                this.variablesFlat = variablesBoard.stream().flatMap(List::stream).collect(Collectors.toList());


                constraints = new ArrayList<>();
                initRowAndColumnConstraints();
                initSudokuConstraints();


                this.filename = filename;
                isLoaded = true;

            } catch (IOException e) {
                System.err.println(String.format(SUDOKU_OPENING_ERROR, pathName));
                e.printStackTrace();
            } catch (NumberFormatException | IndexOutOfBoundsException e){
                System.err.println(String.format(SUDOKU_FORMAT_ERROR, pathName, lineNumber));
                e.printStackTrace();
            }
        } else {
            System.err.println(String.format(SUDOKU_OPENING_ERROR, pathName));
        }
    }

    private void initVariableContainers() {
        variablesBoard = new ArrayList<>(boardSize);
        for(int i = 0; i < boardSize; i++){
            variablesBoard.add(new ArrayList<>());
        }
    }

    private void initSudokuConstraints(){
        for(int i = 0; i < boardSize; i += squareSize){
            List<ArrayList<Variable>> rows = variablesBoard.subList(i, i + squareSize);
            for(int j = 0; j < boardSize; j += squareSize){
                int finalJ = j;
                List<Variable> constrainedVariables = rows.stream()
                        .flatMap(list -> list.subList(finalJ, finalJ + squareSize).stream())
                        .collect(Collectors.toList());
                Constraint squareConstraint = new UnequalityConstraint(constrainedVariables);
                constrainedVariables.forEach(variable -> variable.addConstraint(squareConstraint));
            }
        }
    }
}
