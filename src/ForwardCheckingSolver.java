import java.util.*;
import java.util.stream.Collectors;

public class ForwardCheckingSolver {
    private Problem problem;
    private List<Variable> variables;
    private int numberOfCalls;
    private MrvComparator mrvComparator = new MrvComparator();

    private int currentVariableIndex;

    public ForwardCheckingSolver(Problem problem) {
        this.problem = problem;
        this.variables = problem.getUnfixedVariables();
        currentVariableIndex = 0;
    }

    public void solve() {
        init();
        checkNextVariable();
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
            problem.saveCurrentSolution();
        } else {
            Variable currentVariable = variables.get(currentVariableIndex);
            currentVariableIndex++;

            HistoryPair currentVariableHistory = currentVariable.getHistorySize();
            currentVariable.pushHistoryStack();

            List<Variable> constrainedVariables = currentVariable.getVariablesToChange();

            List<HistoryPair> historySizes = constrainedVariables.stream().map(Variable::getHistorySize).collect(Collectors.toList());

            while(currentVariable.nextValue()){
//            while(currentVariable.pickBestValue()){
                boolean correctlyAssigned = currentVariable.correctlyAssigned();
                if(correctlyAssigned) {
                    boolean domainsNotEmpty = constrainedVariables.stream().allMatch(Variable::recalculateAvailableDomain);
                    if(domainsNotEmpty){
                        sortVariables();
                        checkNextVariable();
                    }
                    historySizes.forEach(HistoryPair::reset);
                }
            }
            currentVariableHistory.reset();
            currentVariable.resetValue();

            historySizes.forEach(HistoryPair::reset);
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
