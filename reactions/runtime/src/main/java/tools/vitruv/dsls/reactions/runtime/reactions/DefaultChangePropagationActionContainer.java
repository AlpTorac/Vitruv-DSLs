package tools.vitruv.dsls.reactions.runtime.reactions;

import java.util.LinkedHashMap;

import java.util.Map;

import tools.vitruv.dsls.reactions.runtime.structure.ChangePropagationAction;

public class DefaultChangePropagationActionContainer implements ReactionsChangePropagationActionContainer {
    private Map<String, ChangePropagationActionGenerator> idToGensMap;

    public DefaultChangePropagationActionContainer() {
        this.idToGensMap = new LinkedHashMap<>();
    }

    @Override
    public boolean addAbsentActionGen(String actionID, ChangePropagationActionGenerator actionGen) {
        return this.idToGensMap.putIfAbsent(actionID, actionGen) == null;
    }

    @Override
    public ChangePropagationActionGenerator overrideActionGen(String actionID,
            ChangePropagationActionGenerator actionGen) {
        return this.idToGensMap.put(actionID, actionGen);
    }

    @Override
    public ChangePropagationActionGenerator removeActionGen(String actionID) {
        return this.idToGensMap.remove(actionID);
    }

    @Override
    public Map<String, ChangePropagationActionGenerator> getAllIdToGenMap() {
        // Deliberately use LinkedHashMap to preserve (actionID, actionGen) insertion
        // order
        return new LinkedHashMap<>(idToGensMap);
    }

    @Override
    public ChangePropagationAction getAction(String actionID) {
        return this.idToGensMap.get(actionID).generateActionInstance();
    }

    @Override
    public boolean hasAction(String actionID) {
        return this.idToGensMap.containsKey(actionID);
    }
}
