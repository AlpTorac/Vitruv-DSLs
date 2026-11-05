package tools.vitruv.dsls.reactions.runtime.reactions;

import org.eclipse.emf.ecore.EObject;

import tools.vitruv.change.atomic.EChange;
import tools.vitruv.dsls.reactions.runtime.state.ReactionExecutionState;

public class DefaultChangePropagationControlFlow extends AbstractChangePropagationControlFlow {
    @Override
    public void propagateChange(EChange<EObject> change, ReactionExecutionState state) {
        var reactions = ((AbstractReactionsChangePropagationSpecification) state.changePropagationObservable())
                .getActionContainer()
                .getAllReactions();
        // Deliberately do not use reactions.forEach(...), so that exceptions are not
        // sneakily eaten
        for (var reaction : reactions) {
            reaction.execute(change, state);
        }
    }

    @Override
    public Object executeAction(EChange<EObject> change, ReactionExecutionState state) {
        propagateChange(change, state);
        return true;
    }
}
