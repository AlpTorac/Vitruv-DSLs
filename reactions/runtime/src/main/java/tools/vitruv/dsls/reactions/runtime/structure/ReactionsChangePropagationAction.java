package tools.vitruv.dsls.reactions.runtime.structure;

import java.util.Map;

import tools.vitruv.dsls.reactions.runtime.controlflows.AbstractReactionsChangePropagationControlFlow;

public interface ReactionsChangePropagationAction extends ChangePropagationAction {
    public AbstractReactionsChangePropagationControlFlow getCallingControlFlow();

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
