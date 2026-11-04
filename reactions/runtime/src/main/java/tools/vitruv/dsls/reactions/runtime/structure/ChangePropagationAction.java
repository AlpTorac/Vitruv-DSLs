package tools.vitruv.dsls.reactions.runtime.structure;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.eclipse.emf.ecore.EObject;

import tools.vitruv.change.atomic.EChange;
import tools.vitruv.dsls.reactions.runtime.reactions.AbstractReactionsChangePropagationSpecification;
import tools.vitruv.dsls.reactions.runtime.routines.RoutinesFacade;
import tools.vitruv.dsls.reactions.runtime.state.ReactionExecutionState;

public interface ChangePropagationAction {
    public Object delegateToReaction(EChange<EObject> change, ReactionExecutionState state, String reactionID);

    public Object delegateToRoutine(ReactionExecutionState state, String facadeGenID, String routineName, Class<?>[] paramClss, Object[] params);
    public default Object delegateToRoutine(ReactionExecutionState state, String facadeGenID, String routineName, Object[] params) {
        return delegateToRoutine(state, facadeGenID, routineName, List.of(params).stream().map(p -> p.getClass()).toArray(Class[]::new), params);
    }

    public Object executeAction(EChange<EObject> change, ReactionExecutionState state);

    public default String getDefaultID() {
        return this.getClass().getSimpleName();
    }
    public void setID(String id);
    public String getID();

    public void setCallHierarchyHaving(CallHierarchyHaving caller);
    public CallHierarchyHaving getCalledBy();

    public void setCPS(AbstractReactionsChangePropagationSpecification cps);
    public AbstractReactionsChangePropagationSpecification getCPS();

    public void registerRoutinesFacadesGenerator();
    public void registerRoutinesFacadesGenerator(Function<ReactionExecutionState, RoutinesFacade> routinesFacadeGenerator);

    // public default RoutinesFacadesProvider getRoutinesFacadesProvider() {
    //     return this.getCPS().getReactionsContainer().getRoutinesFacadesProvider();
    // }
    // public default void setRoutinesFacadesProvider(RoutinesFacadesProvider routinesFacadesProvider) {
    //     this.getCPS().getReactionsContainer().setRoutinesFacadesProvider(routinesFacadesProvider);
    // }

    public Map<String, Object> getAllParams();
    public void putParam(String paramName, Object paramVal);
    public void putAllParams(AbstractReactionsChangePropagationAction caller);
    public <T> T getParam(String paramName);
    public boolean hasParam(String paramName);
}
