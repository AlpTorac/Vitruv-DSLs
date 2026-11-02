package tools.vitruv.dsls.reactions.runtime.reactions;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.emf.ecore.EObject;

import tools.vitruv.change.atomic.EChange;
import tools.vitruv.dsls.reactions.runtime.state.ReactionExecutionState;

public class DefaultReactionsContainer implements ReactionsContainer {
    private final Map<String, Reaction> reactions;

    public DefaultReactionsContainer() {
        this.reactions = new LinkedHashMap<>();
    }

    @Override
    public void addReaction(Reaction reaction) {
        this.addReaction(reaction.getClass().getName(), reaction);
    }

    @Override
    public void addReaction(String reactionID, Reaction reaction) {
        this.reactions.put(reactionID, reaction);
    }

    @Override
    public Reaction removeReaction(String reactionID) {
        return this.reactions.remove(reactionID);
    }

    @Override
    public Reaction getReaction(String reactionID) {
        return this.reactions.getOrDefault(reactionID, null);
    }

    @Override
    public void propagateChange(EChange<EObject> change, ReactionExecutionState state) {
        for (var reaction : reactions.values()) {
            reaction.execute(change, state);
        }
    }

    @Override
    public void clear() {
        this.reactions.clear();
    }
}
