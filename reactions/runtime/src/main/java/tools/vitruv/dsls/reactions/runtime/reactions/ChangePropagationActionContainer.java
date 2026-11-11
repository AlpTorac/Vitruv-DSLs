package tools.vitruv.dsls.reactions.runtime.reactions;

import java.util.Map;

import tools.vitruv.dsls.reactions.runtime.structure.ChangePropagationAction;

public interface ChangePropagationActionContainer {

    public boolean addAbsentActionGen(String actionID, ChangePropagationActionGenerator actionGen);

    public ChangePropagationActionGenerator overrideActionGen(String actionID, ChangePropagationActionGenerator actionGen);

    public ChangePropagationActionGenerator removeActionGen(String actionID);

    public Map<String, ChangePropagationActionGenerator> getAllIdToGenMap();

    public ChangePropagationAction getAction(String actionID);

    public boolean hasAction(String actionID);

    public default void copyAbsentTo(ChangePropagationActionContainer otherContainer) {
        this.getAllIdToGenMap()
                .forEach((id, gen) -> otherContainer.addAbsentActionGen(id, gen));
    }

    public default void copyAllTo(ChangePropagationActionContainer otherContainer) {
        this.getAllIdToGenMap()
                .forEach((id, gen) -> otherContainer.overrideActionGen(id, gen));
    }

}