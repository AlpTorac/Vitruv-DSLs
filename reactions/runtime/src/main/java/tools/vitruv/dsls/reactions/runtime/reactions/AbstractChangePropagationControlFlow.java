package tools.vitruv.dsls.reactions.runtime.reactions;

import tools.vitruv.dsls.reactions.runtime.state.ReactionExecutionState;
import tools.vitruv.dsls.reactions.runtime.structure.AbstractReactionsChangePropagationAction;

public abstract class AbstractChangePropagationControlFlow extends AbstractReactionsChangePropagationAction implements ChangePropagationControlFlow {
    private ReactionExecutionState executionState;

    public AbstractChangePropagationControlFlow(ReactionExecutionState executionState) {
        this.executionState = executionState;
    }

    public AbstractChangePropagationControlFlow() {}

    public void setExecutionState(ReactionExecutionState executionState) {
        this.executionState = executionState;
    }

    public ReactionExecutionState getExecutionState() {
        return this.executionState;
    }

    protected AbstractReactionsChangePropagationSpecification getCPS() {
        return (AbstractReactionsChangePropagationSpecification) this.executionState.changePropagationObservable();
    }

    protected ChangePropagationActionContainer getActionContainer() {
        return this.getCPS().getActionContainer();
    }
}
