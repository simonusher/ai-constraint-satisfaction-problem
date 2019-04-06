import java.util.Comparator;

public class MrvComparator implements Comparator<Variable> {
    @Override
    public int compare(Variable o1, Variable o2) {
        int domainSizeDifference = o2.getAvailableDomainSize() - o1.getAvailableDomainSize();
        if(domainSizeDifference == 0) {
            return o1.getNumberOfConstraints() - o2.getNumberOfConstraints();
        } else {
            return domainSizeDifference;
        }
    }
}
