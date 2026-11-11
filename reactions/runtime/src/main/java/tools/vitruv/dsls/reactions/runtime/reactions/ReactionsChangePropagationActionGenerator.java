package tools.vitruv.dsls.reactions.runtime.reactions;

import java.util.function.Supplier;

import tools.vitruv.dsls.reactions.runtime.structure.AbstractChangePropagationAction;
import tools.vitruv.dsls.reactions.runtime.structure.ChangePropagationAction;

public class ReactionsChangePropagationActionGenerator extends ChangePropagationActionGenerator {
    private String baseIDOverride;

    public ReactionsChangePropagationActionGenerator(AbstractChangePropagationAction actionToReturn) {
        super(() -> actionToReturn);
    }

    public ReactionsChangePropagationActionGenerator(Supplier<ChangePropagationAction> minimalActionGen) {
        super(minimalActionGen);
    }

    public void setBaseIDOverride(String baseIDOverride) {
        this.baseIDOverride = baseIDOverride;
    }

    public String getBaseIDOverride() {
        return this.baseIDOverride;
    }

    public String getDefaultActionID() {
        return ((AbstractChangePropagationAction) this.getInstanceSample()).getDefaultID();
    }

    public boolean isBaseIDOverrideEqual(String baseIDOverride) {
        if (this.baseIDOverride == baseIDOverride)
            return true;
        if (this.baseIDOverride == null ^ baseIDOverride == null)
            return false;
        return this.baseIDOverride.equals(baseIDOverride);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends ChangePropagationAction> T generateActionInstance() {
        var actionInstance = (AbstractChangePropagationAction) super.generateActionInstance();
        if (this.getBaseIDOverride() != null)
            actionInstance.setID(this.getBaseIDOverride());
        return (T) actionInstance;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ReactionsChangePropagationActionGenerator castedO) {
            return super.equals(obj) && this.isBaseIDOverrideEqual(castedO.getBaseIDOverride());
        }
        return false;
    }
}
