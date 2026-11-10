package tools.vitruv.dsls.reactions.runtime.routines;

import static com.google.common.base.Preconditions.checkState;

import java.util.Collection;
import java.util.function.Predicate;

import org.eclipse.emf.ecore.EObject;

import tools.vitruv.change.correspondence.view.EditableCorrespondenceModelView;
import tools.vitruv.dsls.reactions.runtime.correspondence.ReactionsCorrespondence;
import tools.vitruv.dsls.reactions.runtime.reactions.AbstractReactionsChangePropagationSpecification;
import tools.vitruv.dsls.reactions.runtime.state.ReactionExecutionState;
import tools.vitruv.dsls.reactions.runtime.structure.Loggable;

/**
 * Routines use CorrespondenceRetriever objects to look up correspondences
 * between two model elements.
 */
public class CorrespondenceRetriever extends Loggable {
    private EditableCorrespondenceModelView<ReactionsCorrespondence> correspondenceModel;

    /**
     * Creates a new CorrespondenceRetriever that works on
     * <code>correspondenceModel</code>.
     *
     * @param correspondenceModel - {@link EditableCorrespondenceModelView}
     */
    protected CorrespondenceRetriever(
            EditableCorrespondenceModelView<ReactionsCorrespondence> correspondenceModel) {
        this.correspondenceModel = correspondenceModel;
    }

    public AbstractReactionsChangePropagationSpecification getCPS(ReactionExecutionState state) {
        return ((AbstractReactionsChangePropagationSpecification) state.changePropagationObservable());
    }

    /**
     * Tests that some model element of type <code>expectedType</code>
     * corresponds to <code>correspondenceSource</code>.
     *
     * @param <T>                              - Type of returned elements.
     * @param correspondenceSource             - {@link EObject}
     * @param expectedType                     - {@link Class}
     * @param correspondencePreconditionMethod - {@link Predicate}
     * @param tag                              - String
     * @return boolean
     */
    protected <T extends EObject> boolean hasCorrespondingElements(
            EObject correspondenceSource,
            Class<T> expectedType,
            Predicate<T> correspondencePreconditionMethod,
            String tag) {
        return !getCorrespondingElements(
                correspondenceSource, expectedType, correspondencePreconditionMethod, tag).isEmpty();
    }

    /**
     * Returns all model elements of type <code>expectedType</code>
     * that correspond to <code>correspondenceSource</code>.
     *
     * @param <T>                              - Type of returned elements.
     * @param correspondenceSource             - {@link EObject}
     * @param expectedType                     - {@link Class}
     * @param correspondencePreconditionMethod - {@link Predicate}
     *                                         Optional. When set, only returns
     *                                         corresponding elements that fulfill
     *                                         <code>correspondencePreconditionMethod</code>.
     * @param expectedTag                      - String
     *                                         Optional. When set, only returns
     *                                         corresponding elements where the
     *                                         correspondence
     *                                         has the tag <code>expectedTag</code>
     * @return {@link Collection}
     */
    protected <T extends EObject> Collection<T> getCorrespondingElements(
            EObject correspondenceSource,
            Class<T> expectedType,
            Predicate<T> correspondencePreconditionMethod,
            String expectedTag) {
        final Predicate<T> preconditionMethod = correspondencePreconditionMethod != null
                ? correspondencePreconditionMethod
                : anything -> true;

        return correspondenceModel
                .getCorrespondingEObjects(correspondenceSource, expectedTag)
                .stream()
                .filter(e -> e != null)
                .filter(expectedType::isInstance)
                .map(expectedType::cast)
                .filter(preconditionMethod::test)
                .toList();
    }

    /**
     * Returns some model element of type <code>expectedType</code>
     * that corresponds to <code>correspondenceSource</code>, if one exists.
     *
     * @param <T>                              - Type of returned elements.
     * @param correspondenceSource             - {@link EObject}
     * @param expectedType                     - {@link Class}
     * @param correspondencePreconditionMethod - {@link Predicate}
     * @param tag                              - String
     * @param asserted                         - boolean
     *                                         When set to true, asserts that
     *                                         exactly one corresponding element
     *                                         exists.
     *                                         Otherwise, permit no corresponding
     *                                         element.
     * @return T
     */
    protected <T extends EObject> T getCorrespondingElement(
            EObject correspondenceSource,
            Class<T> expectedType,
            Predicate<T> correspondencePreconditionMethod,
            String tag,
            boolean asserted) {
        Collection<T> retrievedElements = getCorrespondingElements(correspondenceSource, expectedType,
                correspondencePreconditionMethod, tag);
        checkState(retrievedElements.size() <= 1 && (!asserted || retrievedElements.size() == 1),
                "There were (%s) corresponding elements of type %s for: %s, which are: %s",
                retrievedElements.size(),
                expectedType.getSimpleName(),
                correspondenceSource,
                retrievedElements);

        return retrievedElements.isEmpty() ? null : retrievedElements.iterator().next();
    }

}