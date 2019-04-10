import java.util.List;

public class BacktrackingSolver {
    private Problem problem;
    private List<Variable> variables;
    private int currentVariableIndex;
    private int numberOfCalls;
    private BacktrackingMrvComparator mrvComparator = new BacktrackingMrvComparator();

    public BacktrackingSolver(Problem problem) {
        this.problem = problem;
        this.variables = problem.getUnfixedVariables();
        this.currentVariableIndex = 0;
    }

    public void solve() {
        this.numberOfCalls = 0;
        sortVariables();
        checkNextVariable();
    }

    private void checkNextVariable(){
        numberOfCalls++;
        if(currentVariableIndex >= variables.size()){
            System.out.println("Found solution, number of calls: " + numberOfCalls);
            problem.saveCurrentSolution();
        } else {
            Variable currentVariable = variables.get(currentVariableIndex);
            currentVariableIndex++;

            while(currentVariable.nextValue()){
//            while(currentVariable.pickBestValue()){
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
        variables.subList(currentVariableIndex, variables.size()).sort(mrvComparator);
    }
}
