import java.util.HashSet;
import java.util.List;

public class UnequalityConstraint implements Constraint {
    private List<Variable> myVariables;

    public UnequalityConstraint(List<Variable> myVariables) {
        this.myVariables = myVariables;
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
}
