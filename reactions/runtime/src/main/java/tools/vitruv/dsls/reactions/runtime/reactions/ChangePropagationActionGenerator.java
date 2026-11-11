package tools.vitruv.dsls.reactions.runtime.reactions;

import java.util.function.Supplier;

import tools.vitruv.dsls.reactions.runtime.structure.ChangePropagationAction;

public class ChangePropagationActionGenerator {
    /**
     * An instance sample, in order to accelerate type checking and sparing the need
     * to pass the expected generated type as a constructor parameter. Since only
     * default constructors are called, generating a bare-bones instance should not
     * be computationally intensive.
     */
    private final ChangePropagationAction instanceSample;
    private Supplier<ChangePropagationAction> minimalActionGen;

    public ChangePropagationActionGenerator(Supplier<ChangePropagationAction> minimalActionGen) {
        this.minimalActionGen = minimalActionGen;
        this.instanceSample = this.minimalActionGen.get();
    }

    public ChangePropagationActionGenerator(ChangePropagationAction actionToReturn) {
        this(() -> actionToReturn);
    }

    protected ChangePropagationAction getInstanceSample() {
        return this.instanceSample;
    }

    public boolean isOfType(Class<? extends ChangePropagationAction> clazz) {
        return clazz.isAssignableFrom(instanceSample.getClass());
    }

    public boolean isOfSameType(Class<? extends ChangePropagationAction> clazz) {
        return clazz.equals(instanceSample.getClass());
    }

    @SuppressWarnings("unchecked")
    public <T extends ChangePropagationAction> T generateActionInstance() {
        return (T) this.minimalActionGen.get();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ChangePropagationActionGenerator castedO) {
            var thisSample = this.getInstanceSample();
            var otherSample = castedO.getInstanceSample();
            if (thisSample == otherSample)
                return true;
            if (thisSample == null ^ otherSample == null)
                return false;
            return thisSample.getClass().equals(otherSample.getClass());
        }
        return false;
    }
}
