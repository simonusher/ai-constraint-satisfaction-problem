import java.util.Comparator;
import java.util.List;
import java.util.Stack;

public class BacktrackingSolver {
    private Problem problem;
    private List<Constraint> constraints;
    private List<Variable> variables;

    private Stack<Variable> variablesToCheck;
    private int numberOfCalls;

    public BacktrackingSolver(Problem problem) {
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
        this.variables.sort(Comparator.comparingInt(Variable::getNumberOfConstraints));
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

            while(currentVariable.nextValue()){
                boolean correctlyAssigned = currentVariable.correctlyAssigned();
                if(correctlyAssigned) {
                    checkNextVariable();
                }
            }
            currentVariable.reset();
            variablesToCheck.push(currentVariable);
        }
    }

    public int getNumberOfCalls() {
        return numberOfCalls;
    }
}
