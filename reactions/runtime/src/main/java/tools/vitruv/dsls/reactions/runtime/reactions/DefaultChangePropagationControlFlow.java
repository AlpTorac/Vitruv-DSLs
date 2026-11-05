package tools.vitruv.dsls.reactions.runtime.reactions;

import org.eclipse.emf.ecore.EObject;

import tools.vitruv.change.atomic.EChange;
import tools.vitruv.dsls.reactions.runtime.state.ReactionExecutionState;

public class DefaultChangePropagationControlFlow extends AbstractChangePropagationControlFlow {
    @Override
    public void propagateChange(EChange<EObject> change, ReactionExecutionState state) {
        this.setExecutionState(state);
        getActionContainer().getAllReactions().forEach(reaction -> reaction.execute(change, state));
    }

    @Override
    public Object executeAction(EChange<EObject> change, ReactionExecutionState state) {
        propagateChange(change, state);
        return true;
    }
}
