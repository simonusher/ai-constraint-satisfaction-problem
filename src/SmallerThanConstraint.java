import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SmallerThanConstraint implements Constraint {
    private ArrayList<Variable> myVariables;
    private Variable greaterVariable;
    private Variable smallerVariable;
    private List<Integer> wholeDomain;

    public SmallerThanConstraint(List<Integer> wholeDomain) {
        myVariables = new ArrayList<>();
        this.wholeDomain = wholeDomain;
    }

    public SmallerThanConstraint(Variable greaterVariable, Variable smallerVariable, List<Integer> wholeDomain) {
        this(wholeDomain);
        setGreaterVariable(greaterVariable);
        setSmallerVariable(smallerVariable);
    }

    public void setGreaterVariable(Variable greaterVariable) {
        this.greaterVariable = greaterVariable;
        this.myVariables.add(greaterVariable);
    }

    public void setSmallerVariable(Variable smallerVariable) {
        this.smallerVariable = smallerVariable;
        this.myVariables.add(smallerVariable);
    }

    @Override
    public List<Variable> getVariables() {
        return myVariables;
    }

    @Override
    public boolean isSatisfied() {
        return greaterVariable.isGreaterThan(smallerVariable);
    }

    @Override
    public boolean removeIncorrectVariableValues(Variable changedVariable) {
        List<Integer> incorrectValues;
        if (changedVariable == smallerVariable) {
            if(greaterVariable.isSet()){
                return true;
            } else {
                incorrectValues = this.wholeDomain.stream().filter(val -> val <= changedVariable.getValue()).collect(Collectors.toList());
                return greaterVariable.removeValuesFromDomain(incorrectValues);
            }
        } else {
            if(smallerVariable.isSet()){
                return true;
            } else {
                incorrectValues = this.wholeDomain.stream().filter(val -> val >= changedVariable.getValue()).collect(Collectors.toList());
                return smallerVariable.removeValuesFromDomain(incorrectValues);
            }
        }
    }
}
