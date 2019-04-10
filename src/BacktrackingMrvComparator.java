import java.util.Comparator;
public class BacktrackingMrvComparator implements Comparator<Variable> {
    @Override
    public int compare(Variable o1, Variable o2) {
        int domainSizeDifference = o1.getNumberOfCurrentlyPossibleValues() - o2.getNumberOfCurrentlyPossibleValues();
        if(domainSizeDifference == 0) {
            return o2.getNumberOfConstraints() - o1.getNumberOfConstraints();
        } else {
            return domainSizeDifference;
        }
    }
}
