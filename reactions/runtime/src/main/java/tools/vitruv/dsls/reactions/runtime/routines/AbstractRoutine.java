package tools.vitruv.dsls.reactions.runtime.routines;

import java.io.IOException;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.eclipse.emf.ecore.EObject;

import tools.vitruv.change.correspondence.view.EditableCorrespondenceModelView;
import tools.vitruv.dsls.reactions.runtime.correspondence.ReactionsCorrespondence;
import tools.vitruv.dsls.reactions.runtime.state.ReactionExecutionState;
import tools.vitruv.dsls.reactions.runtime.structure.CallHierarchyHaving;

/**
 * AbstractRoutine provides a base implementation of a {@link Routine}.
 */
public abstract class AbstractRoutine extends RoutineBase implements Cloneable {
  private AbstractRoutinesFacade routinesFacade;

  /**
   * Creates a new AbstractRoutine.
   *
   * @param routinesFacade - {@link AbstractRoutinesFacade}
   * @param executionState - {@link ReactionExecutionState}
   * @param calledBy       - {@link CallHierarchyHaving}
   */
  protected AbstractRoutine(
      AbstractRoutinesFacade routinesFacade,
      ReactionExecutionState executionState,
      CallHierarchyHaving calledBy) {
    super(executionState, calledBy);
    this.routinesFacade = routinesFacade;
  }

  /**
   * Casts the routine facade to type T.
   *
   * @param <T> - generic return type for convenience.
   *            The requested type has to match the type of the facade provided
   *            during construction.
   * @return <code>routinesFacade</code>
   */
  @SuppressWarnings("unchecked")
  public <T extends AbstractRoutinesFacade> T getRoutinesFacade() {
    return (T) this.routinesFacade;
  }

  @Override
  public boolean execute() {
    routinesFacade.pushCaller(this);
    return super.execute();
  }

  @Override
  protected void finallyAfterExecution(boolean wasSuccessful, Throwable throwable) {
    super.finallyAfterExecution(wasSuccessful, throwable);
    routinesFacade.dropLastCaller();
  }

  /**
   * Executes the routine and modifies the underlying models. May also persist the
   * modified models.
   *
   * @return true if executing the routine succeeds
   * @throws IOException should saving the modified models fail
   */
  protected abstract boolean executeRoutine() throws IOException;

  /**
   * Creates a clone of this instance. The clone will not share any fields of this
   * instance, since the clone is meant to represent an independent call to this
   * Routine.
   * 
   * @see java.lang.Object#clone()
   */
  @Override
  protected Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  /*
   * The inner classes below and the method overrides therein are required by
   * other Reactions packages. Otherwise, some Reactions language code generation
   * tests fail and generated Routines classes do not compile as intended.
   * 
   * They are only placeholders and do not introduce any additional functionality.
   */

  // Placeholder type so that the generated Routines remain valid
  protected static class CorrespondenceRetriever
      extends tools.vitruv.dsls.reactions.runtime.routines.CorrespondenceRetriever {
    protected CorrespondenceRetriever(EditableCorrespondenceModelView<ReactionsCorrespondence> correspondenceModel) {
      super(correspondenceModel);
    }

    @Override
    protected <T extends EObject> T getCorrespondingElement(EObject correspondenceSource, Class<T> expectedType,
        Predicate<T> correspondencePreconditionMethod, String tag, boolean asserted) {
      return super.getCorrespondingElement(correspondenceSource, expectedType, correspondencePreconditionMethod, tag,
          asserted);
    }

    @Override
    protected <T extends EObject> Collection<T> getCorrespondingElements(EObject correspondenceSource,
        Class<T> expectedType, Predicate<T> correspondencePreconditionMethod, String expectedTag) {
      return super.getCorrespondingElements(correspondenceSource, expectedType, correspondencePreconditionMethod,
          expectedTag);
    }

    @Override
    protected <T extends EObject> boolean hasCorrespondingElements(EObject correspondenceSource, Class<T> expectedType,
        Predicate<T> correspondencePreconditionMethod, String tag) {
      return super.hasCorrespondingElements(correspondenceSource, expectedType, correspondencePreconditionMethod, tag);
    }
  }

  // Placeholder type so that the generated Routines remain valid
  public static class Match extends tools.vitruv.dsls.reactions.runtime.routines.Match {
    protected Match(ReactionExecutionState executionState) {
      super(executionState);
    }

    @Override
    protected <T extends EObject> T getCorrespondingElement(EObject correspondenceSource, Class<T> expectedType,
        Predicate<T> correspondencePreconditionMethod, String tag, boolean asserted) {
      return super.getCorrespondingElement(correspondenceSource, expectedType, correspondencePreconditionMethod, tag,
          asserted);
    }

    @Override
    protected <T extends EObject> Collection<T> getCorrespondingElements(EObject correspondenceSource,
        Class<T> expectedType, Predicate<T> correspondencePreconditionMethod, String expectedTag) {
      return super.getCorrespondingElements(correspondenceSource, expectedType, correspondencePreconditionMethod,
          expectedTag);
    }

    @Override
    protected <T extends EObject> boolean hasCorrespondingElements(EObject correspondenceSource, Class<T> expectedType,
        Predicate<T> correspondencePreconditionMethod, String tag) {
      return super.hasCorrespondingElements(correspondenceSource, expectedType, correspondencePreconditionMethod, tag);
    }
  }

  // Placeholder type so that the generated Routines remain valid
  public static class Create extends tools.vitruv.dsls.reactions.runtime.routines.Create {
    protected Create(ReactionExecutionState executionState) {
      super(executionState);
    }

    @Override
    protected <T extends EObject> T createObject(Supplier<T> creator) {
      return super.createObject(creator);
    }
  }

  // Placeholder type so that the generated Routines remain valid
  public static class Update extends tools.vitruv.dsls.reactions.runtime.routines.Update {
    protected Update(ReactionExecutionState executionState) {
      super(executionState);
    }

    @Override
    protected <T extends EObject> T getCorrespondingElement(EObject correspondenceSource, Class<T> expectedType,
        Predicate<T> correspondencePreconditionMethod, String tag, boolean asserted) {
      return super.getCorrespondingElement(correspondenceSource, expectedType, correspondencePreconditionMethod, tag,
          asserted);
    }

    @Override
    protected <T extends EObject> Collection<T> getCorrespondingElements(EObject correspondenceSource,
        Class<T> expectedType, Predicate<T> correspondencePreconditionMethod, String expectedTag) {
      return super.getCorrespondingElements(correspondenceSource, expectedType, correspondencePreconditionMethod,
          expectedTag);
    }

    @Override
    protected <T extends EObject> boolean hasCorrespondingElements(EObject correspondenceSource, Class<T> expectedType,
        Predicate<T> correspondencePreconditionMethod, String tag) {
      return super.hasCorrespondingElements(correspondenceSource, expectedType, correspondencePreconditionMethod, tag);
    }

    @Override
    protected void addCorrespondenceBetween(EObject firstElement, EObject secondElement) {
      super.addCorrespondenceBetween(firstElement, secondElement);
    }

    @Override
    protected void addCorrespondenceBetween(EObject firstElement, EObject secondElement, String tag) {
      super.addCorrespondenceBetween(firstElement, secondElement, tag);
    }

    @Override
    protected void persistProjectRelative(EObject alreadyPersistedObject, EObject elementToPersist,
        String persistencePath) {
      super.persistProjectRelative(alreadyPersistedObject, elementToPersist, persistencePath);
    }

    @Override
    protected void removeCorrespondenceBetween(EObject firstElement, EObject secondElement) {
      super.removeCorrespondenceBetween(firstElement, secondElement);
    }

    @Override
    protected void removeCorrespondenceBetween(EObject firstElement, EObject secondElement, String tag) {
      super.removeCorrespondenceBetween(firstElement, secondElement, tag);
    }

    @Override
    protected void removeObject(EObject element) {
      super.removeObject(element);
    }

  }
}
