package tools.vitruv.dsls.reactions.runtime.routines;

import java.util.List;

import tools.vitruv.dsls.reactions.runtime.state.ReactionExecutionState;
import tools.vitruv.dsls.reactions.runtime.structure.CallHierarchyHaving;

/**
 * Note: All methods start with an underscore here to prevent conflict with the methods that are
 * generated from the routines by concrete implementations of a facade.
 */
@SuppressWarnings("squid:S100")
public interface RoutinesFacade {
  /**
   * Sets the execution state for the current execution run.
   *
   * @param executionState - {@link ReactionExecutionState}
   */
  void setExecutionState(final ReactionExecutionState executionState);

  /**
   * Returns the execution state of the current execution run.
   *
   * @return {@link ReactionExecutionState}
   */
  ReactionExecutionState getExecutionState();

  /**
   * Pushes the given caller to the call stack.
   *
   * @param caller - {@link CallHierarchyHaving}
   */
  void pushCaller(final CallHierarchyHaving caller);

  /**
   * Returns the current routine caller.
   *
   * @return {@link CallHierarchyHaving}
   */
  CallHierarchyHaving getCurrentCaller();

  /**
   * Drops the last caller from the call stack.
   */
  void dropLastCaller();

  /**
   * Executes the implied Routine
   */
  Object executeRoutine(String routineID, Class<?>[] paramClss, Object[] params);
  /**
   * Executes the implied Routine, dynamically looks up all params
   */
  Object executeRoutine(String routineID, Class<?>[] paramClss);
  /**
   * Executes the implied Routine, derives parameter classes from the given parameter array
   */
  default Object executeRoutine(String routineID, Object[] params) {
    return executeRoutine(routineID, List.of(params).stream().map(p -> p.getClass()).toArray(Class[]::new), params);
  }
  /**
   * Executes the implied Routine, asserts that there is only one Routine with a matching ID (i.e. there are no overloading versions of the Routine declared in the .reactions file)
  */
  Object executeRoutine(String routineID);
}
