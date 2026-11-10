package tools.vitruv.dsls.reactions.runtime.reactions;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;

import tools.vitruv.change.atomic.EChange;
import tools.vitruv.dsls.reactions.runtime.routines.Update;
import tools.vitruv.dsls.reactions.runtime.state.ReactionExecutionState;

public abstract class PureJavaReaction extends ReactionBase {
    private EChange<EObject> triggeringChange;
    private ReactionExecutionState executionState;

    public abstract boolean isCurrentChangeMatchingTrigger(final EChange<EObject> change);

    // TODO: Check and adjust the name
    public abstract boolean performWithBlock();

    public EChange<EObject> getChange() {
        return this.triggeringChange;
    }

    public EObject getAffectedEObject() {
        throw new UnsupportedOperationException("Implement");
    }

    public EStructuralFeature getAffectedFeature() {
        throw new UnsupportedOperationException("Implement");
    }

    // TODO: Implement getters for all other change features

    public ReactionExecutionState getExecutionState() {
        return this.executionState;
    }

    @Override
    public void execute(EChange<EObject> change, ReactionExecutionState executionState) {
        if (!isCurrentChangeMatchingTrigger(change))
            return;
        if (!performWithBlock())
            return;

        getCallBlock().updateModels();
    }

    protected abstract PureJavaCall getCallBlock();

    /**
     * Matches the structure of the currently generated "call" blocks
     */
    protected static abstract class PureJavaCall extends Update {
        private ReactionBase containingReaction;

        public PureJavaCall(ReactionBase containingReaction, ReactionExecutionState executionState) {
            super(executionState);
            this.containingReaction = containingReaction;
        }

        public ReactionBase getContainingReaction() {
            return this.containingReaction;
        }

        /**
         * Updates the models accordingly, retrieve the necessary parameters from
         * {@link #getContainingReaction()}
         */
        public abstract void updateModels();
    }
}
