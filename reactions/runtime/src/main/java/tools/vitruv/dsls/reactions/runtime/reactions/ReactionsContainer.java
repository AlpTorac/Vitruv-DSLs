package tools.vitruv.dsls.reactions.runtime.reactions;

import java.util.List;

import org.eclipse.emf.ecore.EObject;

import tools.vitruv.change.atomic.EChange;
import tools.vitruv.dsls.reactions.runtime.state.ReactionExecutionState;

public interface ReactionsContainer {
    public void addReaction(Reaction reaction);
    public void addReaction(String reactionID, Reaction reaction);
    public Reaction removeReaction(String reactionID);
    public Reaction getReaction(String reactionID);
    public List<Reaction> getAllReactions();

    public void propagateChange(EChange<EObject> change, ReactionExecutionState state);
    public void clear();
}
