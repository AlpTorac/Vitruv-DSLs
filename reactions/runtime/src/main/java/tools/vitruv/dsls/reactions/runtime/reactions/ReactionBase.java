package tools.vitruv.dsls.reactions.runtime.reactions;

import org.eclipse.emf.ecore.EObject;

import tools.vitruv.change.atomic.EChange;
import tools.vitruv.dsls.reactions.runtime.routines.RoutinesFacade;
import tools.vitruv.dsls.reactions.runtime.state.ReactionExecutionState;
import tools.vitruv.dsls.reactions.runtime.structure.AbstractReactionsChangePropagationAction;

public abstract class ReactionBase extends AbstractReactionsChangePropagationAction implements Reaction {
  @Override
  public void execute(EChange<EObject> change, ReactionExecutionState reactionExecutionState) {
    var routinesFacade = getRoutinesFacade(reactionExecutionState);
    routinesFacade.pushCaller(this);
    try {
      executeReaction(change, reactionExecutionState, routinesFacade);
    } finally {
      routinesFacade.dropLastCaller();
    }
  }

  protected abstract RoutinesFacade getRoutinesFacade(ReactionExecutionState reactionExecutionState);

  /**
   * Actually executes this reaction with the appropriate routines, as represented by routineFacade.
   *
   * @param change - {@link EChange}
   * @param executionState - {@link ReactionExecutionState}
   * @param routinesFacade - {@link RoutinesFacade}
   */
  protected abstract void executeReaction(EChange<EObject> change,
      ReactionExecutionState executionState, RoutinesFacade routinesFacade);

}
