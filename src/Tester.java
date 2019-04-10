import java.util.List;

public class Tester {
    private List<String> problemFileNames;
    private boolean usingVariableHeuristic;
    private boolean usingValueHeuristic;
    private static final float TIME_UNIT = 1000000000f;

    public Tester(List<String> problemFileNames, boolean usingVariableHeuristic, boolean usingValueHeuristic) {
        this.problemFileNames = problemFileNames;
        this.usingVariableHeuristic = usingVariableHeuristic;
        this.usingValueHeuristic = usingValueHeuristic;
    }

    public void runTests(){
        for (String problemFileName : problemFileNames) {
            solveWithForwardChecking(problemFileName);
            solveWithBacktracking(problemFileName);
        }
    }

    private void solveWithForwardChecking(String problemFileName) {
        String resultFolderName = getResultFolderName() + "fc/";
        Problem problem = getProblem(problemFileName, resultFolderName);
        if(problem.isLoaded()){
            ForwardCheckingSolver solver = new ForwardCheckingSolver(problem, usingVariableHeuristic, usingValueHeuristic);
            System.out.println("Solving " + problemFileName + " with forward checking");
            long startTime = System.nanoTime();
            solver.solve();
            long endTime = System.nanoTime();
            long elapsedTime = endTime - startTime;
            System.out.println("Execution time in seconds: " + elapsedTime / TIME_UNIT);
            System.out.println(solver.getNumberOfCalls());
        }
    }

    private void solveWithBacktracking(String problemFileName) {
        String resultFolderName = getResultFolderName() + "bt/";
        Problem problem = getProblem(problemFileName, resultFolderName);
        if(problem.isLoaded()){
            BacktrackingSolver solver = new BacktrackingSolver(problem, usingVariableHeuristic, usingValueHeuristic);
            System.out.println("Solving " + problemFileName + " with backtracking");
            long startTime = System.nanoTime();
            solver.solve();
            long endTime = System.nanoTime();
            long elapsedTime = endTime - startTime;
            System.out.println("Execution time in seconds: " + elapsedTime / TIME_UNIT);
            System.out.println(solver.getNumberOfCalls());
        }
    }

    private Problem getProblem(String problemFileName, String resultFolderName){
        Problem problem = null;
        if(problemFileName.startsWith("test_futo")){
            problem = new FutoshikiProblem(resultFolderName);
        } else if(problemFileName.startsWith("test_sky")){
            problem = new SkyscrapperProblem(resultFolderName);
        }
        if(problem != null){
            problem.load(problemFileName);
        }
        return problem;
    }

    private String getResultFolderName(){
        StringBuilder resultFolderNameBuilder = new StringBuilder("results/");
        if(usingVariableHeuristic){
            resultFolderNameBuilder.append("variable/");
        }
        if(usingValueHeuristic){
            resultFolderNameBuilder.append("value/");
        }
        return resultFolderNameBuilder.toString();
    }
}
