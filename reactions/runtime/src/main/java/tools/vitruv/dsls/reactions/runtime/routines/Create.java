package tools.vitruv.dsls.reactions.runtime.routines;

import java.util.function.Supplier;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.xbase.lib.Extension;

import tools.vitruv.dsls.reactions.runtime.state.ReactionExecutionState;
import tools.vitruv.dsls.reactions.runtime.structure.Loggable;

/**
 * <code>Create</code> blocks in routines are translated to implementations of
 * the Create class.
 */
public class Create extends Loggable {
    /**
     * The execution state to work on.
     */
    @Extension
    protected final ReactionExecutionState executionState;

    /**
     * Creates a new {@link Create} object.
     *
     * @param executionState - {@link ReactionExecutionState}
     */
    protected Create(ReactionExecutionState executionState) {
        this.executionState = executionState;
    }

    /**
     * Creates a new model element according to <code>creator</code>.
     *
     * @param <T>     - The type of model element.
     * @param creator - {@link Supplier}
     * @return new T
     */
    protected <T extends EObject> T createObject(Supplier<T> creator) {
        T createdObject = creator.get();
        notifyObjectCreated(createdObject);
        return createdObject;
    }

    private void notifyObjectCreated(EObject createdObject) {
        executionState.changePropagationObservable().notifyObjectCreated(createdObject);
    }
}