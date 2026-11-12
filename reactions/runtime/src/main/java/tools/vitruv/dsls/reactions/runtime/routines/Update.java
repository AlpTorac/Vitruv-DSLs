package tools.vitruv.dsls.reactions.runtime.routines;

import java.util.List;

import org.apache.logging.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.xtext.xbase.lib.Extension;

import lombok.val;
import tools.vitruv.dsls.reactions.runtime.helper.PersistenceHelper;
import tools.vitruv.dsls.reactions.runtime.reactions.AbstractReactionsChangePropagationSpecification;
import tools.vitruv.dsls.reactions.runtime.state.ReactionExecutionState;

/**
 * Update extends {@link CorrespondenceRetriever} for managing correspondences,
 * i.e. adding and removing them.
 * Also, within Update blocks, model elements can be persisted or deleted.
 * 
 * <p>
 * <code>update</code> blocks in routines are translated to implementations of
 * Update.
 */
public class Update extends CorrespondenceRetriever {
    /**
     * The execution state to work on.
     */
    @Extension
    protected final ReactionExecutionState executionState;

    /**
     * Creates a new Update statement.
     *
     * @param executionState - {@link ReactionExecutionState}
     */
    protected Update(ReactionExecutionState executionState) {
        super(executionState.getCorrespondenceModel());
        this.executionState = executionState;
    }

    /**
     * Persists a given {@link EObject} as root object in the {@link Resource}
     * at the specified path, relative to the project root folder.
     *
     * @param alreadyPersistedObject - An object that was already persisted within
     *                               the project
     *                               (necessary for retrieving the project folder).
     * @param elementToPersist       - The element to be persisted.
     * @param persistencePath        -
     *                               The path relative to the project root folder at
     *                               which the element shall be persisted,
     *                               using "/" as separator char and including the
     *                               file name with extension.
     */
    protected void persistProjectRelative(EObject alreadyPersistedObject, EObject elementToPersist,
            String persistencePath) {
        if (alreadyPersistedObject == null || elementToPersist == null || persistencePath == null) {
            throw new IllegalArgumentException(
                    "correspondenceSource, element and persistancePath must be specified");
        }

        final URI resourceURI = PersistenceHelper
                .getURIFromSourceProjectFolder(alreadyPersistedObject, persistencePath);
        persistAsRoot(elementToPersist, resourceURI);
    }

    private void persistAsRoot(EObject rootObject, URI uri) {
        getLogger().trace("Registered to persist root %s in %s", rootObject, uri);
        val existingURI = rootObject.eContainer() != null ? rootObject.eResource().getURI() : null;
        if (existingURI != uri) {
            EcoreUtil.remove(rootObject);
            this.executionState.getResourceAccess().persistAsRoot(rootObject, uri);
        }
    }

    /**
     * Adds a correspondence between firstElement and secondElement
     * with the empty tag <code>""</code>.
     *
     * @param firstElement  - {@link EObject}
     * @param secondElement - {@link EObject}
     */
    protected void addCorrespondenceBetween(EObject firstElement, EObject secondElement) {
        addCorrespondenceBetween(firstElement, secondElement, null);
    }

    /**
     * Adds a correspondence between firstElement and secondElement
     * with the given <code>tag</code>.
     *
     * @param firstElement  - {@link EObject}
     * @param secondElement - {@link EObject}
     * @param tag           - String
     */
    protected void addCorrespondenceBetween(
            EObject firstElement, EObject secondElement, String tag) {
        executionState
                .getCorrespondenceModel()
                .addCorrespondenceBetween(firstElement, secondElement, tag == null ? "" : tag);
    }

    /**
     * Removes <code>element</code> from its container.
     *
     * @param element - {@link EObject}
     */
    protected void removeObject(EObject element) {
        if (element == null) {
            return;
        }
        Logger logger = getLogger();
        if (logger.isDebugEnabled()) {
            logger.debug("Removing object %s from %s", element, element.eContainer());
        }
        EcoreUtil.remove(element);
    }

    /**
     * Removes all correspondences between firstElement and secondElement.
     *
     * @param firstElement  - {@link EObject}
     * @param secondElement - {@link EObject}
     */
    protected void removeCorrespondenceBetween(EObject firstElement, EObject secondElement) {
        removeCorrespondenceBetween(firstElement, secondElement, null);
    }

    /**
     * Removes the correspondence between firstElement and secondElement
     * with the given <code>tag</code>.
     *
     * @param firstElement  - {@link EObject}
     * @param secondElement - {@link EObject}
     * @param tag           - String
     */
    protected void removeCorrespondenceBetween(
            EObject firstElement, EObject secondElement, String tag) {
        executionState
                .getCorrespondenceModel()
                .removeCorrespondencesBetween(firstElement, secondElement, tag);
    }

    /*
     * The methods below this comment are only for convenience and less verbosity in
     * CPR implementation. They have do not add any new capabilities to the update
     * blocks.
     */

    /**
     * @return The {@link AbstractReactionsChangePropagationSpecification} from the executionState.
     */
    protected AbstractReactionsChangePropagationSpecification getCPS() {
        return ((AbstractReactionsChangePropagationSpecification) executionState.changePropagationObservable());
    }

    /**
     * Delegates to the pure Java Routine that is registered under the given ID.
     * 
     * @param facade The facade of the Routine / Reaction that contains this block
     * @param params The parameters of the delegated Routine (except the facade, caller, state)
     */
    protected Object delegateToJavaRoutine(AbstractRoutinesFacade facade, String routineID, List<Object> params) {
        return getCPS().delegateToJavaRoutine(facade.getCurrentCaller(), executionState, routineID, params);
    }

    /**
     * Delegates to the Reactions language Routine that is registered under the given ID.
     * 
     * @param facade The facade of the Routine / Reaction that contains this block
     * @param params The parameters of the delegated Routine (except the facade, caller, state)
     */
    protected Object delegateToReactionsRoutine(AbstractRoutinesFacade facade, String routineID, List<Object> params) {
        return getCPS().delegateToReactionsRoutine(facade, routineID, params);
    }
}