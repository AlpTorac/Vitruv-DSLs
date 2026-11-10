package tools.vitruv.dsls.reactions.runtime.routines;

import org.eclipse.xtext.xbase.lib.Exceptions;

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
  protected <T extends AbstractRoutinesFacade> T getRoutinesFacade() {
    return (T) routinesFacade;
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
}
