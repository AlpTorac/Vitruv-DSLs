package tools.vitruv.dsls.reactions.runtime.reactions;

public class DefaultReactionsChangePropagationActionContainer extends DefaultChangePropagationActionContainer
        implements ReactionsChangePropagationActionContainer {
    @Override
    public boolean addAbsentActionGen(String actionID, ChangePropagationActionGenerator actionGen) {
        var isAdded = super.addAbsentActionGen(actionID, actionGen);
        setBaseIDOverride(actionGen, actionID);
        return isAdded;
    }

    @Override
    public ChangePropagationActionGenerator overrideActionGen(String actionID,
            ChangePropagationActionGenerator actionGen) {
        var overridden = super.overrideActionGen(actionID, actionGen);
        setBaseIDOverride(actionGen, actionID);
        return overridden;
    }

    protected String setBaseIDOverride(ChangePropagationActionGenerator actionGen, String baseIdOverride) {
        String oldBaseID = null;
        if (actionGen instanceof ReactionsChangePropagationActionGenerator castedGen) {
            oldBaseID = castedGen.getBaseIDOverride();
            castedGen.withBaseIDOverride(baseIdOverride);
        }
        return oldBaseID;
    }
}
