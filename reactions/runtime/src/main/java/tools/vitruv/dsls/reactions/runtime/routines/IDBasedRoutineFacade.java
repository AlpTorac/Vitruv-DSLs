package tools.vitruv.dsls.reactions.runtime.routines;

import java.util.function.Supplier;

import tools.vitruv.dsls.reactions.runtime.reactions.ReactionsChangePropagationActionContainer;

public class IDBasedRoutineFacade extends RoutinesFacadeBase {
    private Supplier<ReactionsChangePropagationActionContainer> actionContainerAccess;

    public IDBasedRoutineFacade(Supplier<ReactionsChangePropagationActionContainer> actionContainerAccess) {
        this.actionContainerAccess = actionContainerAccess;
    }

    public boolean executeRoutine(String routineID) {
        var con = actionContainerAccess.get();
        con.performRoutine(getExecutionState(), routineID);
        return true;
    }
}
