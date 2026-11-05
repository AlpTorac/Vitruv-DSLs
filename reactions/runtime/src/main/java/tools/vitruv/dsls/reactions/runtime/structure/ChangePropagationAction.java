package tools.vitruv.dsls.reactions.runtime.structure;

import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;

import tools.vitruv.change.atomic.EChange;
import tools.vitruv.dsls.reactions.runtime.reactions.ChangePropagationControlFlow;
import tools.vitruv.dsls.reactions.runtime.state.ReactionExecutionState;

public interface ChangePropagationAction {
    public ChangePropagationControlFlow getCallingControlFlow();

    public default Object delegateToReaction(EChange<EObject> change, ReactionExecutionState state, String reactionID) {
        return getCallingControlFlow().delegateToReaction(change, state, reactionID);
    }

    public default Object delegateToRoutine(ReactionExecutionState state, String facadeGenID, String routineID,
            Class<?>[] paramClss, Object[] params) {
        return getCallingControlFlow().delegateToRoutine(state, facadeGenID, routineID, paramClss, params);
    }

    public default Object delegateToRoutine(ReactionExecutionState state, String facadeGenID, String routineName,
            Object[] params) {
        return delegateToRoutine(state, facadeGenID, routineName,
                List.of(params).stream().map(p -> p.getClass()).toArray(Class[]::new), params);
    }

    public Object executeAction(EChange<EObject> change, ReactionExecutionState state);

    public default String getDefaultID() {
        return this.getClass().getSimpleName();
    }

    public void setID(String id);

    public String getID();

    public Map<String, Object> getAllParams();

    public void putParam(String paramName, Object paramVal);

    public void putAllParams(AbstractReactionsChangePropagationAction caller);

    public <T> T getParam(String paramName);

    public boolean hasParam(String paramName);
}
