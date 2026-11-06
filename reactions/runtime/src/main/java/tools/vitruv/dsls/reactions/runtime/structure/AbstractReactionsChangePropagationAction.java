package tools.vitruv.dsls.reactions.runtime.structure;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;

import tools.vitruv.dsls.reactions.runtime.controlflows.AbstractReactionsChangePropagationControlFlow;

public abstract class AbstractReactionsChangePropagationAction extends CallHierarchyHaving
        implements ReactionsChangePropagationAction {
    private String id;

    private final Map<String, Object> paramNameToVal = new LinkedHashMap<>();

    public AbstractReactionsChangePropagationAction(CallHierarchyHaving caller) {
        super(caller);
        this.id = getDefaultID();
    }

    public AbstractReactionsChangePropagationAction() {
        this(null);
    }

    @Override
    public AbstractReactionsChangePropagationControlFlow getCallingControlFlow() {
        AbstractReactionsChangePropagationControlFlow oldestControlFlow = null;
        if (this instanceof AbstractReactionsChangePropagationControlFlow _this) {
            oldestControlFlow = _this;
        }

        CallHierarchyHaving oldestCalledBy = this;
        
        // Assume that there are no cycles in the CallHierarchyHaving tree
        while (oldestCalledBy != null) {
            oldestCalledBy = oldestCalledBy.getCalledBy();
            if (oldestCalledBy instanceof AbstractReactionsChangePropagationControlFlow cpa) {
                oldestControlFlow = cpa;
            }
        }
        return oldestControlFlow;
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

        if (this.getCalledBy() instanceof ReactionsChangePropagationAction castedCaller)
            return castedCaller.getParam(paramName);

        return null;
    }

    @Override
    public boolean hasParam(String paramName) {
        return this.paramNameToVal.containsKey(paramName);
    }

    protected URI getFullEObjectURI(EObject eobj) {
        return eobj.eResource().getURI()
                .appendFragment(
                        eobj.eResource().getURIFragment(eobj));
    }
}
