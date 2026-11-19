package tools.vitruv.dsls.reactions.runtime.structure;

import java.util.ArrayList;
import java.util.List;

public interface CallHistoryHaving<T extends CallHistoryHaving<T>> {
    public void addCalledAction(T calledAction);
    public void clearCalledActions();
    public List<T> getCalledActions();
    public default List<T> getAllCalledActions() {
        var allCalledActions = new ArrayList<T>();
        getCalledActions().forEach(ca -> {
            allCalledActions.add(ca);
            allCalledActions.addAll(ca.getAllCalledActions());
        });
        return allCalledActions;
    }
}
