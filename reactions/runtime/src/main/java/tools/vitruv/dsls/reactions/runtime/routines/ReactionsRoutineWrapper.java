package tools.vitruv.dsls.reactions.runtime.routines;

import tools.vitruv.dsls.reactions.runtime.state.ReactionExecutionState;

public abstract class ReactionsRoutineWrapper extends AbstractRoutine {
    private AbstractRoutinesFacade wrappedFacade;

    public ReactionsRoutineWrapper() {
    }

    public void wrapRoutinesFacade(AbstractRoutinesFacade wrapee) {
        this.wrappedFacade = wrapee;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends AbstractRoutinesFacade> T getRoutinesFacade() {
        return (T) this.wrappedFacade;
    }

    @Override
    public ReactionExecutionState getExecutionState() {
        if (wrappedFacade != null)
            return wrappedFacade.getExecutionState();
        return super.getExecutionState();
    }
}
