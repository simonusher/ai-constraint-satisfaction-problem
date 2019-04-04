import java.util.Collection;
import java.util.List;

public interface Problem {
    void load(String filename);
    boolean isLoaded();
    List<Constraint> getConstraints();
    List<Variable> getVariables();
    List<Variable> getUnfixedVariables();
    Collection<Integer> getWholeDomain();

    void saveCurrentSolution();
    void saveSolutionsToFile();
}
