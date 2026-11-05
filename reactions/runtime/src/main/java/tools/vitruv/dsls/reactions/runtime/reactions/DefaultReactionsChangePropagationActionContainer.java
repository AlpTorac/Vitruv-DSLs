package tools.vitruv.dsls.reactions.runtime.reactions;

public class DefaultReactionsChangePropagationActionContainer extends DefaultChangePropagationActionContainer {
    @Override
    public void addReaction(String reactionID, Reaction reaction) {
        super.addReaction(reactionID, reaction);
        if (reaction instanceof AbstractReaction castedR) {
            this.setRoutinesFacadesGenerator(reactionID, castedR.getRoutinesFacadeGenerator());
        }
    }

    @Override
    public Reaction overrideExistingReaction(String reactionID, Reaction newReaction) {
        if (newReaction instanceof AbstractReaction castedR) {
            this.setRoutinesFacadesGenerator(reactionID, castedR.getRoutinesFacadeGenerator());
        }
        return super.overrideExistingReaction(reactionID, newReaction);
    }
}
