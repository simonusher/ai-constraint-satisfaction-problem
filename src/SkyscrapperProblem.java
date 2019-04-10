import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SkyscrapperProblem extends TabularProblem {
    private static final String SKYSCRAPPER_OPENING_ERROR = "Couldn't read Skyscrapper file from %s";
    private static final String SKYSCRAPPER_FORMAT_ERROR = "Incorrectly formatted Skyscrapper file %s in line number: %d";

    public SkyscrapperProblem(String resultFolderName) {
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
}
