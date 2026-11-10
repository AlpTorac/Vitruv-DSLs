package tools.vitruv.dsls.reactions.runtime.routines;

import java.io.IOException;

import tools.vitruv.dsls.reactions.runtime.reactions.AbstractReactionsChangePropagationSpecification;
import tools.vitruv.dsls.reactions.runtime.state.ReactionExecutionState;
import tools.vitruv.dsls.reactions.runtime.structure.AbstractReactionsChangePropagationAction;
import tools.vitruv.dsls.reactions.runtime.structure.CallHierarchyHaving;

public abstract class RoutineBase extends AbstractReactionsChangePropagationAction implements Routine {
    private ReactionExecutionState executionState;

    public RoutineBase(ReactionExecutionState executionState,
            CallHierarchyHaving calledBy) {
        super(calledBy);
        this.executionState = executionState;
    }

    public RoutineBase() {
    }

    public ReactionExecutionState getExecutionState() {
        return this.executionState;
    }

    public void setExecutionState(ReactionExecutionState state) {
        this.executionState = state;
    }

    public AbstractReactionsChangePropagationSpecification getCPS() {
        return (AbstractReactionsChangePropagationSpecification) this.executionState.changePropagationObservable();
    }

    /**
     * Executes the routine and modifies the underlying models. May also persist the
     * modified models.
     *
     * @return true if executing the routine succeeds
     * @throws IOException should saving the modified models fail
     */
    protected abstract boolean executeRoutine() throws IOException;
}
