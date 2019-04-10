import java.util.*;
import java.util.stream.Collectors;

public class ForwardCheckingSolver {
    private Problem problem;
    private List<Variable> variables;
    private int numberOfCalls;
    private MrvComparator mrvComparator = new MrvComparator();
    private int currentVariableIndex;

    private Long solvingStartTime;

    private boolean shouldUseVariableHeuristic;
    private boolean shouldUseValueHeuristic;

    public ForwardCheckingSolver(Problem problem,
                                 boolean shouldUseVariableHeuristic,
                                 boolean shouldUseValueHeuristic
    ) {
        this.shouldUseValueHeuristic = shouldUseValueHeuristic;
        this.shouldUseVariableHeuristic = shouldUseVariableHeuristic;
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
            List<HistoryState> historyStates = constrainedVariables.stream()
                    .map(Variable::getHistoryState)
                    .collect(Collectors.toList());

            while(currentVariable.hasNextValue()){
                if(shouldUseValueHeuristic){
                    currentVariable.pickBestValue();
                } else {
                    currentVariable.nextValue();
                }
                boolean correctlyAssigned = currentVariable.correctlyAssigned();
                if(correctlyAssigned) {
                    boolean domainsNotEmpty = constrainedVariables.stream().allMatch(Variable::recalculateAvailableDomain);
                    if(domainsNotEmpty){
                        sortVariables();
                        checkNextVariable();
                    }
                    historyStates.forEach(HistoryState::reset);
                }
            }
            currentVariableHistory.reset();
            currentVariable.resetValue();

            historyStates.forEach(HistoryState::reset);
            currentVariableIndex--;
        }
    }

    public int getNumberOfCalls() {
        return numberOfCalls;
    }

    private void sortVariables(){
        if(shouldUseVariableHeuristic) {
            variables.subList(currentVariableIndex, variables.size()).sort(mrvComparator);
        }
    }
}
