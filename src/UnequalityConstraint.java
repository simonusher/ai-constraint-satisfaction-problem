import java.util.HashSet;
import java.util.List;

public class UnequalityConstraint implements Constraint {
    private List<Variable> myVariables;
    private int numberOfVariables;
    private int variablesMinusOne;


    public UnequalityConstraint(List<Variable> myVariables) {
        this.myVariables = myVariables;
        this.numberOfVariables = myVariables.size();
        this.variablesMinusOne = numberOfVariables - 1;
    }

    public void addVariable(Variable variable){
        myVariables.add(variable);
    }

    @Override
    public List<Variable> getVariables() {
        return myVariables;
    }

    @Override
    public boolean isSatisfied() {
        HashSet<Integer> unique = new HashSet<>(myVariables.size());
        for (Variable myVariable : myVariables) {
            if (myVariable.isSet()) {
                if(!unique.add(myVariable.getValue())){
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean removeIncorrectVariableValues(Variable changedVariable) {
        Integer value = changedVariable.getValue();
        return myVariables.stream()
                .filter(variable -> variable != changedVariable && !variable.isSet())
                .allMatch(variable -> variable.removeValueFromDomain(value));
    }
}
