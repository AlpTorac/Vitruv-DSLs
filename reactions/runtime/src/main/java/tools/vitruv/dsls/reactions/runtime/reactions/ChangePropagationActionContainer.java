package tools.vitruv.dsls.reactions.runtime.reactions;

import java.util.LinkedHashMap;
import java.util.Map;

import tools.vitruv.dsls.reactions.runtime.structure.ChangePropagationAction;

public interface ChangePropagationActionContainer {

    public boolean addAbsentActionGen(String actionID, ChangePropagationActionGenerator actionGen);

    public ChangePropagationActionGenerator overrideActionGen(String actionID,
            ChangePropagationActionGenerator actionGen);

    public ChangePropagationActionGenerator removeActionGen(String actionID);

    public Map<String, ChangePropagationActionGenerator> getAllIdToGenMap();

    public ChangePropagationAction getAction(String actionID);

    public boolean hasAction(String actionID);

    public default Map<String, ChangePropagationActionGenerator> copyAbsentTo(
            ChangePropagationActionContainer otherContainer) {
        var ignoredOverlaps = new LinkedHashMap<String, ChangePropagationActionGenerator>();
        this.getAllIdToGenMap()
                .forEach((id, gen) -> {
                    if (!otherContainer.addAbsentActionGen(id, gen)) {
                        ignoredOverlaps.put(id, gen);
                    }
                });
        return ignoredOverlaps;
    }

    public default Map<String, ChangePropagationActionGenerator> copyAllTo(
            ChangePropagationActionContainer otherContainer) {
        var replacedOverlaps = new LinkedHashMap<String, ChangePropagationActionGenerator>();
        this.getAllIdToGenMap()
                .forEach((id, gen) -> {
                    var oldGen = otherContainer.overrideActionGen(id, gen);
                    if (oldGen != null)
                        replacedOverlaps.put(id, oldGen);
                });
        return replacedOverlaps;
    }

}