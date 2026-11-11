package tools.vitruv.dsls.reactions.runtime.structure;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;

public abstract class AbstractChangePropagationAction extends CallHierarchyHaving
        implements ChangePropagationAction {
    private String id;

    private final Map<String, Object> paramNameToVal = new LinkedHashMap<>();
    private final List<Object> unnamedParams = new ArrayList<>();

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

    public void putAllParams(Map<String, Object> params) {
        this.paramNameToVal.putAll(params);
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

    protected URI getFullEObjectURI(EObject eobj) {
        return eobj.eResource().getURI()
                .appendFragment(
                        eobj.eResource().getURIFragment(eobj));
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
