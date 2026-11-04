package tools.vitruv.dsls.reactions.runtime.structure;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;

import tools.vitruv.change.atomic.EChange;
import tools.vitruv.dsls.reactions.runtime.reactions.AbstractReactionsChangePropagationSpecification;
import tools.vitruv.dsls.reactions.runtime.routines.RoutinesFacade;
import tools.vitruv.dsls.reactions.runtime.state.ReactionExecutionState;

public abstract class AbstractReactionsChangePropagationAction extends CallHierarchyHaving
        implements ChangePropagationAction {
    private String id;
    private AbstractReactionsChangePropagationSpecification cps;

    private final Map<String, Object> paramNameToVal = new LinkedHashMap<>();

    public AbstractReactionsChangePropagationAction(CallHierarchyHaving caller) {
        super(caller);
        this.id = getDefaultID();
    }

    public AbstractReactionsChangePropagationAction() {
        this(null);
    }

    @Override
    public Object delegateToReaction(EChange<EObject> change, ReactionExecutionState state, String reactionID) {
        return this.getCPS().delegateToReaction(change, state, reactionID);
    }

    @Override
    public Object delegateToRoutine(ReactionExecutionState state, String routineID, Class<?>[] paramClss, Object[] params) {
        return this.getCPS().delegateToRoutine(state, routineID, paramClss, params);
    }

    @Override
    public void setID(String id) {
        this.id = id;
    }

    @Override
    public String getID() {
        return id;
    }

    @Override
    public void setCallHierarchyHaving(CallHierarchyHaving caller) {
        this.setCallHierarchyHaving(caller);
    }

    @Override
    public CallHierarchyHaving getCalledBy() {
        return this.getCalledBy();
    }

    @Override
    public Map<String, Object> getAllParams() {
        return Map.copyOf(paramNameToVal);
    }

    @Override
    public void putAllParams(AbstractReactionsChangePropagationAction caller) {
        this.paramNameToVal.putAll(caller.paramNameToVal);
    }

    @Override
    public void putParam(String paramName, Object paramVal) {
        this.paramNameToVal.put(paramName, paramVal);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getParam(String paramName) {
        if (hasParam(paramName)) {
            var val = this.paramNameToVal.get(paramName);
            return val != null ? (T) val : null;
        }

        if (this.getCalledBy() instanceof ChangePropagationAction castedCaller)
            return castedCaller.getParam(paramName);

        return null;
    }

    @Override
    public boolean hasParam(String paramName) {
        return this.paramNameToVal.containsKey(paramName);
    }

    @Override
    public void setCPS(AbstractReactionsChangePropagationSpecification cps) {
        this.cps = cps;
    }

    @Override
    public AbstractReactionsChangePropagationSpecification getCPS() {
        return this.cps;
    }

    protected URI getFullEObjectURI(EObject eobj) {
        return eobj.eResource().getURI()
                .appendFragment(
                        eobj.eResource().getURIFragment(eobj));
    }

    @Override
    public void registerRoutinesFacadesGenerator(
            Function<ReactionExecutionState, RoutinesFacade> routinesFacadeGenerator) {
        this.getCPS().getReactionsContainer().setRoutinesFacadesGenerator(this.getID(), routinesFacadeGenerator);
    }

    @Override
    public Function<ReactionExecutionState, RoutinesFacade> getRoutinesFacadeGenerator() {
        return this.getCPS().getReactionsContainer().getRoutinesFacadeGenerator(this.getID());
    }
}
