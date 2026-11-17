package tools.vitruv.dsls.reactions.runtime.controlflows;

import org.eclipse.emf.ecore.EObject;

import tools.vitruv.change.atomic.EChange;
import tools.vitruv.dsls.reactions.runtime.state.ReactionExecutionState;
import tools.vitruv.dsls.reactions.runtime.structure.AbstractChangePropagationAction;

public abstract class ControlFlowBase extends AbstractChangePropagationAction implements ChangePropagationControlFlow {
    @Override
    public Object executeAction(EChange<EObject> change, ReactionExecutionState state) {
        propagateChange(change, state);
        return true;
    }
}
