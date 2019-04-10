import java.util.List;

public class BacktrackingSolver {
    private Problem problem;
    private List<Variable> variables;
    private int currentVariableIndex;
    private int numberOfCalls;
    private BacktrackingMrvComparator mrvComparator = new BacktrackingMrvComparator();
    private Long solvingStartTime;


    boolean shouldUseVariableHeuristic;
    boolean shouldUseValueHeuristic;

    public BacktrackingSolver(Problem problem,
                              boolean shouldUseVariableHeuristic,
                              boolean shouldUseValueHeuristic
    ) {
        this.shouldUseValueHeuristic = shouldUseValueHeuristic;
        this.shouldUseVariableHeuristic = shouldUseVariableHeuristic;
        this.problem = problem;
        this.variables = problem.getUnfixedVariables();
        this.currentVariableIndex = 0;
    }

    public void solve() {
        solvingStartTime = System.nanoTime();
        this.numberOfCalls = 0;
        sortVariables();
        checkNextVariable();
        Long runningTime = System.nanoTime() - solvingStartTime;
        problem.saveSolutionsToFile(numberOfCalls, runningTime);
    }

    private void checkNextVariable(){
        numberOfCalls++;
        if(currentVariableIndex >= variables.size()){
            System.out.println("Found solution, number of calls: " + numberOfCalls);
            Long elapsedTime = System.nanoTime() - solvingStartTime;
            problem.saveCurrentSolution(numberOfCalls, elapsedTime);
        } else {
            Variable currentVariable = variables.get(currentVariableIndex);
            currentVariableIndex++;

            while(currentVariable.hasNextValue()){
                if(shouldUseValueHeuristic){
                    currentVariable.pickBestValue();
                } else {
                    currentVariable.nextValue();
                }
                boolean correctlyAssigned = currentVariable.correctlyAssigned();
                if(correctlyAssigned) {
                    sortVariables();
                    checkNextVariable();
                }
            }
            currentVariable.reset();
            currentVariableIndex--;
        }
    }

    public int getNumberOfCalls() {
        return numberOfCalls;
    }

    private void sortVariables(){
        if(shouldUseVariableHeuristic){
            variables.subList(currentVariableIndex, variables.size()).sort(mrvComparator);
        }
    }
}
