package tools.vitruv.dsls.reactions.runtime.structure;

import java.util.ArrayList;
import java.util.List;

public interface CallHistoryHaving {
    public void addCalledAction(AbstractChangePropagationAction calledAction);
    public void clearCalledActions();
    public List<AbstractChangePropagationAction> getCalledActions();
    public default List<AbstractChangePropagationAction> getAllCalledActions() {
        var allCalledActions = new ArrayList<AbstractChangePropagationAction>();
        getCalledActions().forEach(ca -> {
            allCalledActions.add(ca);
            allCalledActions.addAll(ca.getAllCalledActions());
        });
        return allCalledActions;
    }
}
