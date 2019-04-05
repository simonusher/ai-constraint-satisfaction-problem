import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Variable {
    public String id;
    private List<Integer> wholeDomain;
    private List<Integer> availableDomain;
    private boolean isSet;
    private boolean isFixed;
    private Set<Constraint> myConstraints;
    private Integer value;


    public Variable(List<Integer> wholeDomain) {
        this.wholeDomain = wholeDomain;
        this.myConstraints = new HashSet<>();
        this.isFixed = false;
        this.isSet = false;
        this.resetDomain();
    }

    public Variable(List<Integer> wholeDomain, Integer initialValue) {
        this.wholeDomain = wholeDomain;
        this.myConstraints = new HashSet<>();
        this.isFixed = true;
        this.resetDomain();
        setValue(initialValue);
    }

    public boolean isSet() {
        return isSet;
    }

    public boolean isFixed() {
        return isFixed;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer newValue){
        this.value = newValue;
        this.isSet = true;
    }

    public void addConstraint(Constraint constraint){
        myConstraints.add(constraint);
    }

    public boolean nextValue(){
        if(availableDomain.isEmpty()){
            return false;
        }
        else {
            setValue(availableDomain.remove(0));
            return true;
        }
    }

    public void resetDomain() {
        this.availableDomain = new LinkedList<>();
        this.availableDomain.addAll(this.wholeDomain);
    }

    public void resetValue(){
        this.value = null;
        this.isSet = false;
    }

    public void reset() {
        resetDomain();
        this.value = null;
        this.isSet = false;
    }

    public boolean isEqual(Variable other){
        return this.isSet && other.isSet && this.value.equals(other.value);
    }

    public boolean isGreaterThan(Variable other){
        return !this.isSet || !other.isSet || this.value > other.value;
    }

    public boolean isSmallerThan(Variable other){
        return !this.isSet || !other.isSet || this.value < other.value;
    }

    public boolean correctlyAssigned(){
        return this.value != null && myConstraints.stream().allMatch(Constraint::isSatisfied);
    }

    public List<Variable> getVariablesToChange(){
        return new ArrayList<>(getVariablesToChangeStream().collect(Collectors.toSet()));
    }

    public Stream<Variable> getVariablesToChangeStream(){
        return myConstraints.stream()
                .flatMap(constraint -> constraint.getVariables().stream())
                .filter(variable -> variable != this && !variable.isSet());
    }

    public int getNumberOfConstraints() {
        return myConstraints.size();
    }

    public boolean recalculateAvailableDomain(){
        resetDomain();
        List<Integer> available = new LinkedList<>();
        for(int i = 0; i < availableDomain.size(); i++){
            setValue(availableDomain.get(i));
            if(correctlyAssigned()){
                available.add(this.value);
            }
        }
        this.resetValue();
        this.availableDomain = available;
        return !this.availableDomain.isEmpty();
    }
}
