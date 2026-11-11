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

        getCallBlock(executionState).updateModels(this);
    }

    protected abstract PureJavaCall getCallBlock(ReactionExecutionState executionState);

    /**
     * Matches the structure of the currently generated "call" blocks
     */
    protected static abstract class PureJavaCall extends Update {
        public PureJavaCall(ReactionExecutionState executionState) {
            super(executionState);
        }

        /**
         * Updates the models accordingly.
         * 
         * @param containingReaction The Reaction that contains this Call block, which
         *                           also has the parameters required by this method
         */
        public abstract void updateModels(ReactionBase containingReaction);
    }
}
