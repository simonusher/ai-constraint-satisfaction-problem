import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class UnequalityConstraint implements Constraint {
    private List<Variable> myVariables;
    private int numberOfVariables;
    private int variablesMinusOne;
    private HashSet<Integer> unique;



    public UnequalityConstraint(List<Variable> myVariables) {
        this.myVariables = myVariables;
        this.numberOfVariables = myVariables.size();
        this.variablesMinusOne = numberOfVariables - 1;
        unique = new HashSet<>();
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
        for(int i = 0; i < variablesMinusOne && satisfied; i++) {
            for(int j = i + 1; j < numberOfVariables && satisfied; j++) {
                satisfied = !myVariables.get(i).isEqual(myVariables.get(j));
            }
        }
        return satisfied;
//        return myVariables.stream().filter(Variable::isSet).map(Variable::getValue).allMatch(new HashSet<Integer>()::add);
    }

    @Override
    public boolean removeIncorrectVariableValues(Variable changedVariable) {
        Integer value = changedVariable.getValue();
        return myVariables.stream()
                .filter(variable -> variable != changedVariable && !variable.isSet())
                .allMatch(variable -> variable.removeValueFromDomain(value));
    }
}
