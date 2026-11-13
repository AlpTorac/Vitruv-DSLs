package tools.vitruv.dsls.reactions.runtime.routines;

import java.util.Deque;
import java.util.LinkedList;

import tools.vitruv.dsls.reactions.runtime.state.ReactionExecutionState;
import tools.vitruv.dsls.reactions.runtime.structure.CallHierarchyHaving;
import tools.vitruv.dsls.reactions.runtime.structure.Loggable;
import tools.vitruv.dsls.reactions.runtime.structure.ReactionsImportPath;

/**
 * Note: All methods start with an underscore here to not conflict with the
 * methods
 * that are generated from the routines by concrete implementations.
 */
public abstract class AbstractRoutinesFacade extends Loggable implements RoutinesFacade
// ,Cloneable
{
  /**
   * Used by concrete implementations to request routines facades of executed
   * routines.
   */
  private RoutinesFacadesProvider routinesFacadesProvider;
  /**
   * The absolute path inside the import hierarchy to the segment this routines
   * facade belongs to. Never null.
   */
  private ReactionsImportPath reactionsImportPath;
  /**
   * Shared execution state among all routines facades in the import hierarchy.
   */
  private ReactionExecutionState executionState;
  private final Deque<CallHierarchyHaving> callerStack = new LinkedList<>();

  /**
   * Creates a new {@link AbstractRoutinesFacade}.
   *
   * @param routinesFacadesProvider - {@link RoutinesFacadesProvider}
   * @param reactionsImportPath     - {@link ReactionsImportPath}
   */
  protected AbstractRoutinesFacade(
      RoutinesFacadesProvider routinesFacadesProvider, ReactionsImportPath reactionsImportPath) {
    this.routinesFacadesProvider = routinesFacadesProvider;
    this.reactionsImportPath = reactionsImportPath;
  }

  /**
   * Returns the routine facade provider of this facade.
   *
   * @return routinesFacadesProvider
   */
  public RoutinesFacadesProvider getRoutinesFacadesProvider() {
    return routinesFacadesProvider;
  }

  /**
   * Returns the import path for the segment this routines facade belongs to.
   *
   * @return reactionsImportPath
   */
  public ReactionsImportPath getReactionsImportPath() {
    return reactionsImportPath;
  }

  public void setRoutinesFacadesProvider(RoutinesFacadesProvider routinesFacadesProvider) {
    this.routinesFacadesProvider = routinesFacadesProvider;
  }

  public void setReactionsImportPath(ReactionsImportPath reactionsImportPath) {
    this.reactionsImportPath = reactionsImportPath;
  }

  @Override
  public void setExecutionState(ReactionExecutionState executionState) {
    this.executionState = executionState;
  }

  @Override
  public ReactionExecutionState getExecutionState() {
    return executionState;
  }

  @Override
  public void pushCaller(CallHierarchyHaving caller) {
    callerStack.push(caller);
  }

  @Override
  public CallHierarchyHaving getCurrentCaller() {
    return (!callerStack.isEmpty()) ? callerStack.peek() : null;
  }

  @Override
  public void dropLastCaller() {
    callerStack.pop();
  }

  // /**
  //  * Creates a clone of this instance
  //  * 
  //  * @see java.lang.Object#clone()
  //  */
  // @Override
  // public AbstractRoutinesFacade clone() {
  //   try {
  //     var clone = (AbstractRoutinesFacade) super.clone();
  //     clone.executionState = new ReactionExecutionState(executionState.userInteractor(),
  //         executionState.getCorrespondenceModel(), executionState.getResourceAccess(),
  //         executionState.changePropagationObservable());
  //     clone.callerStack = new LinkedList<>(callerStack);
  //     clone.reactionsImportPath = reactionsImportPath;
  //     if (routinesFacadesProvider instanceof AbstractRoutinesFacadesProvider castedProv) {
  //       clone.routinesFacadesProvider = castedProv.clone();
  //     }
  //     return clone;
  //   } catch (CloneNotSupportedException e) {
  //     throw new UnsupportedOperationException(e);
  //   }
  // }
}
