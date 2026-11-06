package tools.vitruv.dsls.reactions.runtime.structure;

import org.eclipse.emf.ecore.EObject;

import tools.vitruv.change.atomic.EChange;
import tools.vitruv.dsls.reactions.runtime.state.ReactionExecutionState;

public interface ChangePropagationAction {
    public Object executeAction(EChange<EObject> change, ReactionExecutionState state);
}