package tools.vitruv.dsls.reactions.runtime.reactions;

import tools.vitruv.dsls.reactions.runtime.routines.IDBasedRoutineFacade;
import tools.vitruv.dsls.reactions.runtime.routines.RoutinesFacade;
import tools.vitruv.dsls.reactions.runtime.state.ReactionExecutionState;

public abstract class PureJavaReaction extends ReactionBase {
    @Override
    protected RoutinesFacade getRoutinesFacade(ReactionExecutionState reactionExecutionState) {
        var cps = (AbstractReactionsChangePropagationSpecification) reactionExecutionState
                .changePropagationObservable();
        var idBasedFacade = new IDBasedRoutineFacade(() -> cps.getActionContainer());
        return idBasedFacade;
    }
}
