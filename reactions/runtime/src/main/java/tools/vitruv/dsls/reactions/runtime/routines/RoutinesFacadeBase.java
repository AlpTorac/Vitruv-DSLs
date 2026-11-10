package tools.vitruv.dsls.reactions.runtime.routines;

import java.util.Deque;
import java.util.LinkedList;

import tools.vitruv.dsls.reactions.runtime.state.ReactionExecutionState;
import tools.vitruv.dsls.reactions.runtime.structure.CallHierarchyHaving;
import tools.vitruv.dsls.reactions.runtime.structure.Loggable;
import tools.vitruv.dsls.reactions.runtime.structure.ReactionsImportPath;

public abstract class RoutinesFacadeBase extends Loggable implements RoutinesFacade {
  /**
   * Shared execution state among all routines facades in the import hierarchy.
   */
  private ReactionExecutionState executionState;
  private final Deque<CallHierarchyHaving> callerStack = new LinkedList<>();

  
  /**
   * Creates a new {@link AbstractRoutinesFacade}.
   *
   * @param routinesFacadesProvider - {@link RoutinesFacadesProvider}
   * @param reactionsImportPath - {@link ReactionsImportPath}
   */
  public RoutinesFacadeBase() {}

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
}
