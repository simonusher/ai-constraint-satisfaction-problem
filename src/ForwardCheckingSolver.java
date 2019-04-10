import java.util.*;
import java.util.stream.Collectors;

public class ForwardCheckingSolver {
    private Problem problem;
    private List<Variable> variables;
    private int numberOfCalls;
    private MrvComparator mrvComparator = new MrvComparator();

    private Long solvingStartTime;

    private int currentVariableIndex;

    public ForwardCheckingSolver(Problem problem) {
        this.problem = problem;
        this.variables = problem.getUnfixedVariables();
        currentVariableIndex = 0;
    }

    public void solve() {
        solvingStartTime = System.nanoTime();
        init();
        checkNextVariable();
        Long runningTime = System.nanoTime() - solvingStartTime;
        problem.saveSolutionsToFile(numberOfCalls, runningTime);
    }

    private void init() {
        this.numberOfCalls = 0;
        this.variables.forEach(Variable::recalculateAvailableDomain);
        this.variables.sort(mrvComparator);
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

            HistoryState currentVariableHistory = currentVariable.getHistoryState();
            currentVariable.pushHistoryState();

            List<Variable> constrainedVariables = currentVariable.getVariablesToChange();

            List<HistoryState> historySizes = constrainedVariables.stream().map(Variable::getHistoryState).collect(Collectors.toList());

            while(currentVariable.nextValue()){
//            while(currentVariable.pickBestValue()){
                boolean correctlyAssigned = currentVariable.correctlyAssigned();
                if(correctlyAssigned) {
                    boolean domainsNotEmpty = constrainedVariables.stream().allMatch(Variable::recalculateAvailableDomain);
                    if(domainsNotEmpty){
                        sortVariables();
                        checkNextVariable();
                    }
                    historySizes.forEach(HistoryState::reset);
                }
            }
            currentVariableHistory.reset();
            currentVariable.resetValue();

            historySizes.forEach(HistoryState::reset);
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
