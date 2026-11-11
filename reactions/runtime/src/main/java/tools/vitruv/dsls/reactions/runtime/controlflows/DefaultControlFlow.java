package tools.vitruv.dsls.reactions.runtime.controlflows;

import org.eclipse.emf.ecore.EObject;

import tools.vitruv.change.atomic.EChange;
import tools.vitruv.dsls.reactions.runtime.reactions.AbstractReactionsChangePropagationSpecification;
import tools.vitruv.dsls.reactions.runtime.state.ReactionExecutionState;

public class DefaultControlFlow extends ControlFlowBase {
    @Override
    public void propagateChange(EChange<EObject> change, ReactionExecutionState state) {
        var reactions = ((AbstractReactionsChangePropagationSpecification) state.changePropagationObservable())
                .getActionContainer()
                .getAllReactions();
        for (var reaction : reactions) {
            reaction.setCallHierarchyHaving(this);
            reaction.execute(change, state);
        }
    }

    @Override
    public Object executeAction(EChange<EObject> change, ReactionExecutionState state) {
        propagateChange(change, state);
        return true;
    }
}
