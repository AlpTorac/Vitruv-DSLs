package tools.vitruv.dsls.reactions.runtime.structure;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;

public abstract class AbstractChangePropagationAction extends CallHierarchyHaving
        implements ChangePropagationAction, CallHistoryHaving {
    private String id;

    private final Map<ChangePropagationParameter, Object> paramToVal = new LinkedHashMap<>();
    private final List<Object> unnamedParams = new ArrayList<>();

    private final List<AbstractChangePropagationAction> calledActions = new ArrayList<>();

    public AbstractChangePropagationAction(CallHierarchyHaving caller) {
        super(caller);
        this.id = getDefaultID();
    }

    public AbstractChangePropagationAction() {
        this(null);
    }

    public String getDefaultID() {
        return this.getClass().getSimpleName();
    }

    public void setID(String id) {
        this.id = id;
    }

    public String getID() {
        return id;
    }

    public Map<ChangePropagationParameter, Object> getAllParams() {
        return Map.copyOf(paramToVal);
    }

    /**
     * Should not throw an exception, since caller should contain valid parameter
     * values
     */
    public Map<ChangePropagationParameter, Object> putAllParams(AbstractChangePropagationAction caller) {
        return putAllParams(caller.getAllParams());
    }

    public Map<ChangePropagationParameter, Object> putAllParams(Map<ChangePropagationParameter, Object> params)
            throws IllegalArgumentException {
        var overriddenVals = new LinkedHashMap<ChangePropagationParameter, Object>();
        for (var e : params.entrySet()) {
            var param = e.getKey();
            var paramVal = e.getValue();
            if (hasParamDirectly(param)) {
                overriddenVals.put(param, getParamDirectly(param));
            }
            putParam(param, paramVal);
        }
        return overriddenVals;
    }

    public Object putParam(ChangePropagationParameter param, Object paramVal) throws IllegalArgumentException {
        // Check whether the parameter can be null
        if (!param.isNullable() && paramVal == null) {
            throw new IllegalArgumentException(param.getName() + " cannot be null");
        }

        // Check whether the given paramVal conforms to the foreseen type of param
        if (!param.isNullable() && !param.getType().isAssignableFrom(paramVal.getClass())) {
            throw new IllegalArgumentException(param.getName() + " cannot be assigned " + paramVal);
        }
        return this.paramToVal.put(param, paramVal);
    }

    public <T> T getParam(ChangePropagationParameter param) throws IllegalArgumentException {
        if (hasParamDirectly(param)) {
            return getParamDirectly(param);
        }

        return getParamViaCallers(param);
    }

    @SuppressWarnings("unchecked")
    public <T> T getParamDirectly(ChangePropagationParameter param) throws IllegalArgumentException {
        if (!hasParamDirectly(param))
            throw new IllegalArgumentException("No parameter " + param.getName() + " is directly set in this instance");

        var val = this.paramToVal.get(param);
        return val != null ? (T) val : null;
    }

    public <T> T getParamViaCallers(ChangePropagationParameter param) throws IllegalArgumentException {
        if (this.getCalledBy() instanceof AbstractChangePropagationAction castedCaller) {
            if (castedCaller.hasParamDirectly(param)) {
                return castedCaller.getParamDirectly(param);
            }
            return castedCaller.getParamViaCallers(param);
        }

        throw new IllegalArgumentException(
                "No parameter " + param.getName() + " is set in any of the callers of this instance");
    }

    public boolean hasParam(ChangePropagationParameter param) {
        return hasParamDirectly(param) || hasParamViaCallers(param);
    }

    public boolean hasParamDirectly(ChangePropagationParameter param) {
        return this.paramToVal.containsKey(param);
    }

    public boolean hasParamViaCallers(ChangePropagationParameter param) {
        if (this.getCalledBy() instanceof AbstractChangePropagationAction castedCaller)
            return castedCaller.hasParamDirectly(param) || castedCaller.hasParamViaCallers(param);

        return false;
    }

    @SuppressWarnings("unchecked")
    public <T> T removeParam(ChangePropagationParameter param) {
        var removedVal = this.paramToVal.remove(param);
        return removedVal != null ? (T) removedVal : null;
    }

    public Map<ChangePropagationParameter, Object> clearNamedParams() {
        var clearedParams = Map.copyOf(this.paramToVal);
        this.paramToVal.clear();
        return clearedParams;
    }

    public void putUnnamedParam(Object paramVal) {
        this.unnamedParams.add(paramVal);
    }

    public void putAllUnnamedParams(List<Object> paramVals) {
        this.unnamedParams.addAll(paramVals);
    }

    @SuppressWarnings("unchecked")
    public <T> T getUnnamedParam(int index) {
        return (T) this.unnamedParams.get(index);
    }

    public List<Object> getUnnamedParams() {
        return List.copyOf(unnamedParams);
    }

    public UnnamedParamIt getUnnamedParamIt() {
        return new UnnamedParamIt(this);
    }

    public List<Object> clearUnnamedParams() {
        var unnamedParams = List.copyOf(this.unnamedParams);
        this.unnamedParams.clear();
        return unnamedParams;
    }

    protected URI getFullEObjectURI(EObject eobj) {
        return eobj.eResource().getURI()
                .appendFragment(
                        eobj.eResource().getURIFragment(eobj));
    }

    @Override
    public void addCalledAction(AbstractChangePropagationAction calledAction) {
        this.calledActions.add(calledAction);
    }

    @Override
    public void clearCalledActions() {
        this.calledActions.clear();
    }

    @Override
    public List<AbstractChangePropagationAction> getCalledActions() {
        return List.copyOf(calledActions);
    }

    protected static class UnnamedParamIt {
        private final Iterator<Object> unnamedParamIt;

        public UnnamedParamIt(AbstractChangePropagationAction action) {
            this.unnamedParamIt = action.getUnnamedParams().iterator();
        }

        public boolean hasNext() {
            return unnamedParamIt.hasNext();
        }

        @SuppressWarnings("unchecked")
        public <T> T next() {
            return (T) unnamedParamIt.next();
        }
    }
}
