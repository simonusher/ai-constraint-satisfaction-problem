import java.util.ArrayList;
import java.util.List;

public class SkyscrapperConstraint implements Constraint {
    private ArrayList<Variable> myVariables;
    private int skyscrapersFromLeft;
    private int skyscrapersFromRight;
    private List<Integer> wholeDomain;
    private Integer minHeight;
    private Integer maxHeight;

    public SkyscrapperConstraint(ArrayList<Variable> myVariables,
                                 int skyscrapersFromLeft, int skyscrapersFromRight, List<Integer> wholeDomain) {
        this.myVariables = myVariables;
        this.skyscrapersFromLeft = skyscrapersFromLeft;
        this.skyscrapersFromRight = skyscrapersFromRight;
        this.wholeDomain = wholeDomain;
        this.wholeDomain.stream().min(Integer::compareTo).ifPresent(value -> minHeight = value);
        this.wholeDomain.stream().max(Integer::compareTo).ifPresent(value -> maxHeight = value);
    }

    @Override
    public List<Variable> getVariables() {
        return myVariables;
    }

    @Override
    public boolean isSatisfied() {
        int nFromLeft = 0;
        int nFromRight = 0;

        Integer currentMaxFromLeft = myVariables.get(0).getValue();
        Integer currentMaxFromRight = myVariables.get(myVariables.size() - 1).getValue();

        if(currentMaxFromLeft != null){
            if((currentMaxFromLeft.equals(maxHeight) && skyscrapersFromLeft > 1) || (currentMaxFromLeft.equals(minHeight) && skyscrapersFromLeft == 1)){
                return false;
            }
        }

        if(currentMaxFromRight != null){
            if((currentMaxFromRight.equals(maxHeight) && skyscrapersFromRight > 1) || (currentMaxFromRight.equals(minHeight) && skyscrapersFromRight == 1)){
                return false;
            }
        }

        for (int i = 0, j = myVariables.size() - 1; i < myVariables.size(); i++, j--) {
            Variable leftVariable = myVariables.get(i);
            Variable rightVariable = myVariables.get(j);
            if(!leftVariable.isSet() || !rightVariable.isSet()){
                return true;
            }

            Integer leftValue = leftVariable.getValue();
            Integer rightValue = rightVariable.getValue();

            if(currentMaxFromLeft == null || leftValue >= currentMaxFromLeft){
                nFromLeft++;
                currentMaxFromLeft = leftValue;
            }

            if(currentMaxFromRight == null || rightValue >= currentMaxFromRight){
                nFromRight++;
                currentMaxFromRight = rightValue;
            }
        }

        return (skyscrapersFromLeft == 0 || nFromLeft == skyscrapersFromLeft)
                && (skyscrapersFromRight == 0 || nFromRight == skyscrapersFromRight);
    }
}
