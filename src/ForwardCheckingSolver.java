import java.util.*;
import java.util.stream.Collectors;

public class ForwardCheckingSolver {
    private Problem problem;
    private List<Constraint> constraints;
    private List<Variable> variables;

    private Stack<Variable> variablesToCheck;
    private int numberOfCalls;
    private MrvComparator mrvComparator = new MrvComparator();

    public ForwardCheckingSolver(Problem problem) {
        this.problem = problem;
        this.constraints = problem.getConstraints();
        this.variables = problem.getUnfixedVariables();
    }

    public void solve() {
        sortVariables();
        resetVariablesToCheck();
        this.numberOfCalls = 0;
        checkNextVariable();
    }

    private void sortVariables() {
//        this.variables.sort(Comparator.comparingInt(Variable::getNumberOfConstraints));
//        this.variables.forEach(Variable::recalculateAvailableDomain);
        this.variables.sort(mrvComparator);
    }

    private void resetVariablesToCheck() {
        this.variablesToCheck = new Stack<>();
        this.variablesToCheck.addAll(variables);
    }

    private void checkNextVariable(){
        numberOfCalls++;
        if(variablesToCheck.empty()){
            System.out.println("Found solution");
            problem.saveCurrentSolution();
        } else {
            Variable currentVariable = variablesToCheck.pop();

            HistoryPair currentVariableHistory = currentVariable.getHistorySize();
            currentVariable.pushHistoryStack();

            List<Variable> constrainedVariables = currentVariable.getVariablesToChange();

            List<HistoryPair> historySizes = constrainedVariables.stream().map(Variable::getHistorySize).collect(Collectors.toList());

            while(currentVariable.nextValue()){
                boolean correctlyAssigned = currentVariable.correctlyAssigned();
                if(correctlyAssigned) {
                    boolean domainsNotEmpty = constrainedVariables.stream().allMatch(Variable::recalculateAvailableDomain);
                    if(domainsNotEmpty){
                        selectNextVariable();
                        checkNextVariable();
                    }
                    historySizes.forEach(HistoryPair::reset);
                }
            }
            currentVariableHistory.reset();
            currentVariable.resetValue();

            historySizes.forEach(HistoryPair::reset);
            variablesToCheck.push(currentVariable);
        }
    }

    public int getNumberOfCalls() {
        return numberOfCalls;
    }

    private void selectNextVariable(){
        variablesToCheck.stream().max(mrvComparator).ifPresent(variable -> {
            variablesToCheck.remove(variable);
            variablesToCheck.push(variable);
        });
    }
}
