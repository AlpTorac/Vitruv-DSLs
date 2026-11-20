package tools.vitruv.dsls.reactions.runtime.structure;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractChangePropagationAction extends CallHierarchyHaving
        implements ChangePropagationAction, CallHistoryHaving {
    private String id;

    private final Map<ChangePropagationParameter, Object> paramToVal = new LinkedHashMap<>();

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

    @SuppressWarnings("unchecked")
    public abstract static class AbstractActionParameterAccess<T extends AbstractActionParameterAccess<T>> {
        protected final AbstractChangePropagationAction action;
        protected boolean includeDirectParams = true;
        protected boolean includeCallerParams = false;
        protected boolean includeParamNames = false;

        protected AbstractActionParameterAccess(AbstractChangePropagationAction action) {
            this.action = action;
        }

        public T includeDirect() {
            this.includeDirectParams = true;
            return (T) this;
        }

        public T includeCallers() {
            this.includeCallerParams = true;
            return (T) this;
        }

        public T includeParamNames() {
            this.includeParamNames = true;
            return (T) this;
        }

        public T excludeDirect() {
            this.includeDirectParams = false;
            return (T) this;
        }

        public T excludeCallers() {
            this.includeCallerParams = false;
            return (T) this;
        }

        public T excludeParamNames() {
            this.includeParamNames = false;
            return (T) this;
        }

        public T directOnly() {
            return this.includeDirect().excludeCallers();
        }

        public T callersOnly() {
            return this.excludeDirect().includeCallers();
        }

        public T includeAllParams() {
            return this.includeDirect().includeCallers();
        }

        public T getAccessForAction(AbstractChangePropagationAction otherAction) {
            var access = createAccessForAction(otherAction);
            access.includeDirectParams = this.includeDirectParams;
            access.includeCallerParams = this.includeCallerParams;
            access.includeParamNames = this.includeParamNames;
            return (T) access;
        }

        public boolean hasParam(ChangePropagationParameter param) {
            if (includeDirectParams) {
                if (action.paramToVal.containsKey(param)) {
                    return true;
                }
                if (includeParamNames && action.paramToVal.keySet().stream()
                        .anyMatch(p -> p.getName().equals(param.getName()))) {
                    return true;
                }
            }

            if (includeCallerParams && action.getCalledBy() instanceof AbstractChangePropagationAction actionCaller) {
                return createAccessForAction(actionCaller).includeAllParams().hasParam(param);
            }

            return false;
        }

        public abstract T createAccessForAction(AbstractChangePropagationAction otherAction);
    }

    public static class ActionParameterAccess extends AbstractActionParameterAccess<ActionParameterAccess> {
        protected ActionParameterAccess(AbstractChangePropagationAction action) {
            super(action);
        }

        public ActionParameterAccess createAccessForAction(AbstractChangePropagationAction otherAction) {
            return new ActionParameterAccess(otherAction);
        }
    }

    @SuppressWarnings("unchecked")
    public static class ActionParameterGetter extends AbstractActionParameterAccess<ActionParameterGetter> {
        protected ActionParameterGetter(AbstractChangePropagationAction action) {
            super(action);
        }

        public <T> T getParam(ChangePropagationParameter param) throws IllegalArgumentException {
            if (includeDirectParams) {
                if (action.paramToVal.containsKey(param)) {
                    var val = action.paramToVal.get(param);
                    return val != null ? (T) val : null;
                }
                if (includeParamNames) {
                    var valOpt = action.paramToVal.entrySet().stream()
                            .filter(e -> e.getKey().getName().equals(param.getName())).findFirst();
                    if (valOpt.isPresent()) {
                        var val = action.paramToVal.get(valOpt.get().getKey());
                        return val != null ? (T) val : null;
                    }
                }
            }

            if (includeCallerParams && action.getCalledBy() instanceof AbstractChangePropagationAction actionCaller) {
                return (T) getAccessForAction(actionCaller).includeAllParams().getParam(param);
            }

            return throwExceptionFor(param);
        }

        protected <T> T throwExceptionFor(ChangePropagationParameter param) {
            if (includeCallerParams) {
                throw new IllegalArgumentException(
                        "No parameter " + param.getName() + " is set in any of the callers of this instance");
            }
            if (includeDirectParams) {
                throw new IllegalArgumentException(
                        "No parameter " + param.getName() + " is directly set in this instance");
            }

            throw new IllegalStateException("This throw should never be reached");
        }

        public Map<ChangePropagationParameter, Object> getAllParams() {
            var params = new LinkedHashMap<ChangePropagationParameter, Object>();
            if (includeDirectParams) {
                action.paramToVal.forEach((p, v) -> params.put(p, v));
            }
            if (includeCallerParams && action.getCalledBy() instanceof AbstractChangePropagationAction actionCaller) {
                getAccessForAction(actionCaller).includeAllParams().getAllParams()
                        .forEach((p, v) -> params.putIfAbsent(p, v));
            }
            return params;
        }

        @Override
        public ActionParameterGetter getAccessForAction(AbstractChangePropagationAction otherAction) {
            return (ActionParameterGetter) super.getAccessForAction(otherAction);
        }

        @Override
        public ActionParameterGetter createAccessForAction(AbstractChangePropagationAction otherAction) {
            return new ActionParameterGetter(otherAction);
        }
    }

    public static class ActionParameterSetter extends AbstractActionParameterAccess<ActionParameterSetter> {
        protected boolean overrideExisting = true;

        protected ActionParameterSetter(AbstractChangePropagationAction action) {
            super(action);
        }

        public ActionParameterSetter override() {
            this.overrideExisting = true;
            return this;
        }

        public ActionParameterSetter doNotOverride() {
            this.overrideExisting = false;
            return this;
        }

        /**
         * Should not throw an exception, since caller should contain valid parameter
         * values
         */
        public Map<ChangePropagationParameter, Object> putAllParams(AbstractChangePropagationAction caller) {
            return putAllParams(new ActionParameterGetter(caller).includeDirect().getAllParams());
        }

        public Map<ChangePropagationParameter, Object> putAllParams(Map<ChangePropagationParameter, Object> params)
                throws IllegalArgumentException {
            // Deliberately use LinkedHashMap to allow null values
            var overriddenVals = new LinkedHashMap<ChangePropagationParameter, Object>();
            for (var e : params.entrySet()) {
                var param = e.getKey();
                var paramVal = e.getValue();
                if (this.getAccessForAction(action).directOnly().hasParam(param)) {
                    overriddenVals.put(param, new ActionParameterGetter(action).directOnly().getParam(param));
                }
                putParam(param, paramVal);
            }
            return overriddenVals;
        }

        public Object putParam(ChangePropagationParameter param, Object paramVal) throws IllegalArgumentException {
            // Check whether the given paramVal conforms to the foreseen type of param
            if (param != null && !param.getType().isAssignableFrom(paramVal.getClass())) {
                throw new IllegalArgumentException(param.getName() + " cannot be assigned " + paramVal);
            }

            if (overrideExisting || !new ActionParameterAccess(action).directOnly().hasParam(param)) {
                return action.paramToVal.put(param, paramVal);
            } else {
                throw new IllegalStateException(param.getName() + " is already assigned " + paramVal
                        + " . Call this.override() to allow overriding the existing value");
            }
        }

        @SuppressWarnings("unchecked")
        public <T> T removeParam(ChangePropagationParameter param) {
            var removedVal = this.action.paramToVal.remove(param);
            return removedVal != null ? (T) removedVal : null;
        }

        public Map<ChangePropagationParameter, Object> clearParams() {
            var clearedParams = new LinkedHashMap<>(action.paramToVal);
            action.paramToVal.clear();
            return clearedParams;
        }

        @Override
        public ActionParameterSetter getAccessForAction(AbstractChangePropagationAction otherAction) {
            var setter = super.getAccessForAction(otherAction);
            setter.overrideExisting = this.overrideExisting;
            return setter;
        }

        @Override
        public ActionParameterSetter createAccessForAction(AbstractChangePropagationAction otherAction) {
            return new ActionParameterSetter(otherAction);
        }
    }

    /**
     * A convenience class that enables retrieving unnamed parameters of an action
     * instance easier and using them without having to type-cast.
     */
    public static class OrderedActionParameterRetriever {
        private final Map<ChangePropagationParameter, Object> unnamedParamMap;

        private int unnamedParamItIndex = 0;
        private final Iterator<Object> unnamedParamIt;

        protected OrderedActionParameterRetriever(AbstractChangePropagationAction action) {
            this.unnamedParamMap = new ActionParameterGetter(action).includeDirect().getAllParams();
            this.unnamedParamIt = this.unnamedParamMap.values().iterator();
        }

        public int getUnnamedParamCount() {
            return unnamedParamMap.size();
        }

        public int getRemainingUnnamedParamCount() {
            return unnamedParamMap.size() - unnamedParamItIndex;
        }

        public boolean hasNext() {
            return unnamedParamIt.hasNext();
        }

        @SuppressWarnings("unchecked")
        public <T> T next() {
            var elem = unnamedParamIt.next();
            unnamedParamItIndex++;
            return elem != null ? (T) elem : null;
        }
    }
}
