package tools.vitruv.dsls.reactions.runtime.routines;

import java.io.IOException;

import org.eclipse.xtext.xbase.lib.Exceptions;

import tools.vitruv.change.correspondence.view.EditableCorrespondenceModelView;
import tools.vitruv.dsls.reactions.runtime.correspondence.ReactionsCorrespondence;
import tools.vitruv.dsls.reactions.runtime.state.ReactionExecutionState;
import tools.vitruv.dsls.reactions.runtime.structure.CallHierarchyHaving;

/**
 * AbstractRoutine provides a base implementation of a {@link Routine}.
 */
public abstract class AbstractRoutine extends RoutineBase {
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
  protected <T extends AbstractRoutinesFacade> T getRoutinesFacade() {
    return (T) this.routinesFacade;
  }

  @Override
  public boolean execute() {
      routinesFacade.pushCaller(this);
      var success = false;

      try {
          success = executeRoutine();
      } catch (Exception e) {
          throw Exceptions.sneakyThrow(e);
      } finally {
          routinesFacade.dropLastCaller();
      }
      return success;
  }

  /**
   * Executes the routine and modifies the underlying models. May also persist the
   * modified models.
   *
   * @return true if executing the routine succeeds
   * @throws IOException should saving the modified models fail
   */
  protected abstract boolean executeRoutine() throws IOException;

  // Placeholder type so that the generated Routines remain valid
  protected static class CorrespondenceRetriever extends tools.vitruv.dsls.reactions.runtime.routines.CorrespondenceRetriever {
    protected CorrespondenceRetriever(EditableCorrespondenceModelView<ReactionsCorrespondence> correspondenceModel) {
      super(correspondenceModel);
    }
  }

  // Placeholder type so that the generated Routines remain valid
  public static class Match extends tools.vitruv.dsls.reactions.runtime.routines.Match {
    protected Match(ReactionExecutionState executionState) {
      super(executionState);
    }
  }

  // Placeholder type so that the generated Routines remain valid
  public static class Create extends tools.vitruv.dsls.reactions.runtime.routines.Create {
    protected Create(ReactionExecutionState executionState) {
      super(executionState);
    }
  }

  // Placeholder type so that the generated Routines remain valid
  public static class Update extends tools.vitruv.dsls.reactions.runtime.routines.Update {
    protected Update(ReactionExecutionState executionState) {
      super(executionState);
    }
  }
}
