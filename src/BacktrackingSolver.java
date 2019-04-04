import java.util.Comparator;
import java.util.List;
import java.util.Stack;

public class BacktrackingSolver {
    private Problem problem;
    private List<Constraint> constraints;
    private List<Variable> variables;

    private Stack<Variable> variablesToCheck;
    private int numberOfReturns;

    public BacktrackingSolver(Problem problem) {
        this.problem = problem;
        this.constraints = problem.getConstraints();
        this.variables = problem.getUnfixedVariables();
    }

    public void solve() {
        sortVariables();
        resetVariablesToCheck();
        this.numberOfReturns = 0;
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
        if(variablesToCheck.empty()){
            System.out.println("Found solution");
            problem.saveCurrentSolution();
        } else {
            Variable currentVariable = variablesToCheck.pop();

            boolean solutionExists = false;
            while(currentVariable.nextValue()){
                boolean correctlyAssigned = currentVariable.correctlyAssigned();
                if(correctlyAssigned) {
                    solutionExists = true;
                    checkNextVariable();
                }
            }
            if(!solutionExists){
                numberOfReturns++;
            }
            currentVariable.reset();
            variablesToCheck.push(currentVariable);
        }
    }

    public int getNumberOfReturns() {
        return numberOfReturns;
    }
}
