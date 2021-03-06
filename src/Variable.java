import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Variable {
    public String id;
    private List<Integer> wholeDomain;
    private List<Integer> availableDomain;
    private boolean isSet;
    private boolean isFixed;
    private List<Constraint> myConstraints;
    private Integer value;

    private Stack<List<Integer>> domainHistory;


    public Variable(List<Integer> wholeDomain) {
        this.wholeDomain = wholeDomain;
        this.myConstraints = new LinkedList<>();
        this.isFixed = false;
        this.isSet = false;
        this.resetDomain();
        domainHistory = new Stack<>();
    }

    public Variable(List<Integer> wholeDomain, Integer initialValue) {
        this.wholeDomain = wholeDomain;
        this.myConstraints = new LinkedList<>();
        this.isFixed = true;
        this.resetDomain();
        setValue(initialValue);
        domainHistory = new Stack<>();
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

    public boolean hasNextValue(){
        return !availableDomain.isEmpty();
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

    public void sortDomain(){
        List<Variable> constrainedVariables = this.getVariablesToChange();
        Map<Integer, Integer> possibleValuesMap = new HashMap<>(availableDomain.size());
        for (Integer possibleValue : availableDomain) {
            setValue(possibleValue);
            int numberOfPossibleValues = constrainedVariables.stream()
                    .mapToInt(Variable::getNumberOfCurrentlyPossibleValues).sum();
            possibleValuesMap.put(possibleValue, numberOfPossibleValues);
        }
        availableDomain.sort((firstPossibleValue, secondPossibleValue) -> possibleValuesMap.get(secondPossibleValue) - possibleValuesMap.get(firstPossibleValue));
    }

    public boolean pickBestValue(){
        if(availableDomain.isEmpty()){
            return false;
        }
        else {
            int maxPossibleValues = -1;
            Integer bestValue = null;
            List<Variable> constrainedVariables = this.getVariablesToChange();
            for (Integer possibleValue : availableDomain) {
                setValue(possibleValue);
                int numberOfPossibleValues = constrainedVariables.stream()
                        .mapToInt(Variable::getNumberOfCurrentlyPossibleValues).sum();
                if(numberOfPossibleValues > maxPossibleValues){
                    maxPossibleValues = numberOfPossibleValues;
                    bestValue = possibleValue;
                }
            }
            availableDomain.remove(bestValue);
            setValue(bestValue);
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

    public boolean isGreaterThan(Variable other){
        return !this.isSet || !other.isSet || this.value > other.value;
    }

    public boolean correctlyAssigned(){
        return this.value != null && myConstraints.stream().allMatch(Constraint::isSatisfied);
    }

    public List<Variable> getVariablesToChange(){
        return getVariablesToChangeStream().collect(Collectors.toList());
    }

    public Stream<Variable> getVariablesToChangeStream(){
        return myConstraints.stream()
                .flatMap(constraint -> constraint.getVariables().stream())
                .distinct()
                .filter(variable -> variable != this && !variable.isSet());
    }

    public int getNumberOfConstraints() {
        return myConstraints.size();
    }

    public boolean recalculateAvailableDomain(){
        pushHistoryState();
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

    public int getNumberOfCurrentlyPossibleValues(){
        HistoryState historyState = getHistoryState();
        recalculateAvailableDomain();
        int possibleNumber = getAvailableDomainSize();
        historyState.reset();
        return possibleNumber;
    }


    public int getAvailableDomainSize() {
        return this.availableDomain.size();
    }

    public void pushHistoryState(){
        domainHistory.push(availableDomain);
        availableDomain = new LinkedList<>(availableDomain);
    }

    public void popHistoryState(){
        this.availableDomain = domainHistory.pop();
    }

    public void resetToHistoryState(Integer stateNumber){
        while(domainHistory.size() != stateNumber){
            popHistoryState();
        }
    }

    public HistoryState getHistoryState(){
        return new HistoryState(this, domainHistory.size());
    }
}
