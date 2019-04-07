public class HistoryPair {
    public Variable variable;
    public Integer historySize;

    public HistoryPair(Variable variable, Integer historySize) {
        this.variable = variable;
        this.historySize = historySize;
    }

    public void reset(){
        variable.resetToHistoryState(historySize);
    }
}
