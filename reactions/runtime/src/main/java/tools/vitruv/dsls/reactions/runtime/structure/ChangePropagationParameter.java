package tools.vitruv.dsls.reactions.runtime.structure;

public interface ChangePropagationParameter {
    public String getName();
    public Class<?> getType();
    public boolean isNullable();
}
