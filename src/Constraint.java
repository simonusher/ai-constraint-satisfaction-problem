import java.util.List;

public interface Constraint {
    List<Variable> getVariables();
    boolean isSatisfied();
}
