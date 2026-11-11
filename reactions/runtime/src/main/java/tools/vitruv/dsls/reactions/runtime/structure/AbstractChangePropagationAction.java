package tools.vitruv.dsls.reactions.runtime.structure;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;

public abstract class AbstractChangePropagationAction extends CallHierarchyHaving
        implements ChangePropagationAction {
    private String id;

    private final Map<String, Object> paramNameToVal = new LinkedHashMap<>();

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

    public Map<String, Object> getAllParams() {
        return Map.copyOf(paramNameToVal);
    }

    public void putAllParams(AbstractChangePropagationAction caller) {
        this.paramNameToVal.putAll(caller.paramNameToVal);
    }

    public void putParam(String paramName, Object paramVal) {
        this.paramNameToVal.put(paramName, paramVal);
    }

    @SuppressWarnings("unchecked")
    public <T> T getParam(String paramName) {
        if (hasParam(paramName)) {
            var val = this.paramNameToVal.get(paramName);
            return val != null ? (T) val : null;
        }

        if (this.getCalledBy() instanceof AbstractChangePropagationAction castedCaller)
            return castedCaller.getParam(paramName);

        return null;
    }

    public boolean hasParam(String paramName) {
        return this.paramNameToVal.containsKey(paramName);
    }

    protected URI getFullEObjectURI(EObject eobj) {
        return eobj.eResource().getURI()
                .appendFragment(
                        eobj.eResource().getURIFragment(eobj));
    }
}
