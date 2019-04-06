import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class UnequalityConstraint implements Constraint {
    private List<Variable> myVariables;


    public UnequalityConstraint() {
        myVariables = new ArrayList<>();
    }

    public UnequalityConstraint(List<Variable> myVariables) {
        this.myVariables = myVariables;
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
        boolean satisfied = true;
        for(int i = 0; i < myVariables.size() - 1 && satisfied; i++) {
            for(int j = i + 1; j < myVariables.size() && satisfied; j++) {
                satisfied = !myVariables.get(i).isEqual(myVariables.get(j));
            }
        }
        return satisfied;
    }

//    @Override
//    public boolean removeIncorrectVariableValues(Variable changedVariable) {
//        return myVariables.stream()
//                .filter(variable -> variable != changedVariable && !variable.isSet())
//                .allMatch(variable -> variable.removeValueFromDomain(changedVariable.getValue()));
//    }
}
