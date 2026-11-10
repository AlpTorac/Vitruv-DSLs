package tools.vitruv.dsls.reactions.runtime.routines;

import org.eclipse.xtext.xbase.lib.Extension;

import tools.vitruv.dsls.reactions.runtime.state.ReactionExecutionState;

/**
 * Match directly implements {@link CorrespondenceRetriever} for finding
 * matching model elements.
 * <code>match</code> blocks in routines are translated to implementations of
 * Match.
 */
public class Match extends CorrespondenceRetriever {
    /**
     * The execution state to work on.
     */
    @Extension
    protected final ReactionExecutionState executionState;

    /**
     * Creates a new Match statement.
     *
     * @param executionState - {@link ReactionExecutionState}
     */
    protected Match(ReactionExecutionState executionState) {
        super(executionState.getCorrespondenceModel());
        this.executionState = executionState;
    }
}