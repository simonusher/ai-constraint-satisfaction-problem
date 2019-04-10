public class HistoryState {
    public Variable variable;
    public Integer historySize;

    public HistoryState(Variable variable, Integer historySize) {
        this.variable = variable;
        this.historySize = historySize;
    }

    public void reset(){
        variable.resetToHistoryState(historySize);
    }
}
