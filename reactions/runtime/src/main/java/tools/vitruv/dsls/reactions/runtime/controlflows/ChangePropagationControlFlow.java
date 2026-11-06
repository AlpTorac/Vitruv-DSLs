package tools.vitruv.dsls.reactions.runtime.controlflows;

import org.eclipse.emf.ecore.EObject;

import tools.vitruv.change.atomic.EChange;
import tools.vitruv.dsls.reactions.runtime.state.ReactionExecutionState;
import tools.vitruv.dsls.reactions.runtime.structure.ChangePropagationAction;

public interface ChangePropagationControlFlow extends ChangePropagationAction {
    public void propagateChange(EChange<EObject> change, ReactionExecutionState state);

    @Override
    default Object executeAction(EChange<EObject> change, ReactionExecutionState state) {
        propagateChange(change, state);
        return true;
    }
}