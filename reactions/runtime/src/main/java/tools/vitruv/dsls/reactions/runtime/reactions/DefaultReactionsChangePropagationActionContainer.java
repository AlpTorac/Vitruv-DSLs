package tools.vitruv.dsls.reactions.runtime.reactions;

public class DefaultReactionsChangePropagationActionContainer extends DefaultChangePropagationActionContainer {
    @Override
    public void addReaction(String reactionID, AbstractReaction reaction) {
        super.addReaction(reactionID, reaction);
        this.setRoutinesFacadesGenerator(reactionID, reaction.getRoutinesFacadeGenerator());
    }

    @Override
    public AbstractReaction overrideExistingReaction(String reactionID, AbstractReaction newReaction) {
        this.setRoutinesFacadesGenerator(reactionID, newReaction.getRoutinesFacadeGenerator());
        return super.overrideExistingReaction(reactionID, newReaction);
    }
}
