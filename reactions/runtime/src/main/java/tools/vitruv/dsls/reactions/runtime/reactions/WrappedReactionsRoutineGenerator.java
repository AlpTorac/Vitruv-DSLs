package tools.vitruv.dsls.reactions.runtime.reactions;

import java.util.function.Supplier;

import tools.vitruv.dsls.reactions.runtime.routines.ReactionsRoutineWrapper;
import tools.vitruv.dsls.reactions.runtime.structure.ChangePropagationAction;

public class WrappedReactionsRoutineGenerator extends ReactionsChangePropagationActionGenerator {
    private String wrappedRoutineBaseIDOverride;

    public WrappedReactionsRoutineGenerator(ReactionsRoutineWrapper actionToReturn) {
        super(() -> actionToReturn);
    }

    public WrappedReactionsRoutineGenerator(Supplier<ChangePropagationAction> minimalActionGen) {
        super(minimalActionGen);
    }

    @Override
    protected ReactionsRoutineWrapper getInstanceSample() {
        return (ReactionsRoutineWrapper) super.getInstanceSample();
    }

    public WrappedReactionsRoutineGenerator withWrappedRoutineBaseIDOverride(String wrappedRoutineBaseIDOverride) {
        this.wrappedRoutineBaseIDOverride = wrappedRoutineBaseIDOverride;
        return this;
    }

    public String getWrappedRoutineBaseIDOverride() {
        return this.wrappedRoutineBaseIDOverride;
    }

    @Override
    public WrappedReactionsRoutineGenerator withBaseIDOverride(String baseIDOverride) {
        return (WrappedReactionsRoutineGenerator) super.withBaseIDOverride(baseIDOverride);
    }

    public String getDefaultActionGenID() {
        return getWrappedRoutineBaseIDOverride() != null ? getWrappedRoutineBaseIDOverride()
                : this.getInstanceSample().getWrappedRoutineDefaultID();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ChangePropagationAction> T generateActionInstance() {
        var actionInstance = (ReactionsRoutineWrapper) super.generateActionInstance();
        if (this.getWrappedRoutineBaseIDOverride() != null) {
            actionInstance.setWrappedRoutineIDOverride(getWrappedRoutineBaseIDOverride());
        }
        return (T) actionInstance;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof WrappedReactionsRoutineGenerator castedO) {
            return super.equals(castedO);
        }
        return false;
    }
}
