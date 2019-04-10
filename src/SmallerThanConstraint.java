import java.util.ArrayList;
import java.util.List;
public class SmallerThanConstraint implements Constraint {
    private ArrayList<Variable> myVariables;
    private Variable greaterVariable;
    private Variable smallerVariable;
    private List<Integer> wholeDomain;

    private Integer maxValue;
    private Integer minValue;

    public SmallerThanConstraint(List<Integer> wholeDomain) {
        myVariables = new ArrayList<>();
        this.wholeDomain = wholeDomain;
    }

    public SmallerThanConstraint(Variable greaterVariable, Variable smallerVariable, List<Integer> wholeDomain) {
        this(wholeDomain);
        setGreaterVariable(greaterVariable);
        setSmallerVariable(smallerVariable);
        this.wholeDomain.stream().min(Integer::compareTo).ifPresent(value -> minValue = value);
        this.wholeDomain.stream().max(Integer::compareTo).ifPresent(value -> maxValue = value);
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
        if((smallerVariable.isSet() && smallerVariable.getValue().equals(maxValue)) ||
                greaterVariable.isSet() && greaterVariable.getValue().equals(minValue))
        {
            return false;
        }

        return greaterVariable.isGreaterThan(smallerVariable);
    }
}
