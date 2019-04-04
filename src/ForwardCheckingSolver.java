import java.util.Comparator;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

public class ForwardCheckingSolver {
    private Problem problem;
    private List<Constraint> constraints;
    private List<Variable> variables;

    private Stack<Variable> variablesToCheck;
    private int numberOfReturns;

    public ForwardCheckingSolver(Problem problem) {
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
            int currentVariableHistorySize = currentVariable.getDomainHistorySize();
            currentVariable.pushDomainHistoryState();

            boolean solutionExists = false;
            List<Variable> constrainedVariables = currentVariable.getVariablesToChange();
            List<Integer> outerHistorySizes = constrainedVariables.stream()
                    .map(Variable::getDomainHistorySize).collect(Collectors.toList());

            while(currentVariable.nextValue()){
                boolean correctlyAssigned = currentVariable.correctlyAssigned();
                if(correctlyAssigned) {
                    List<Integer> historySizes = currentVariable.getVariablesToChange().stream()
                            .map(Variable::getDomainHistorySize).collect(Collectors.toList());
                    if(currentVariable.clearConstrainedVariablesDomains()){
                        solutionExists = true;
                        checkNextVariable();
                    } else {
                        for(int i = 0; i < constrainedVariables.size(); i++){
                            constrainedVariables.get(i).restoreDomainToState(historySizes.get(i));
                        }
                    }
                }
            }
            if(!solutionExists){
                numberOfReturns++;
            }
            for(int i = 0; i < constrainedVariables.size(); i++){
                constrainedVariables.get(i).restoreDomainToState(outerHistorySizes.get(i));
            }
            currentVariable.resetAndRestoreDomain(currentVariableHistorySize);
            variablesToCheck.push(currentVariable);
        }
    }

    public int getNumberOfReturns() {
        return numberOfReturns;
    }
}
