package tools.vitruv.dsls.reactions.runtime.reactions;

import java.util.function.Function;
import lombok.val;
import org.eclipse.emf.ecore.EObject;
import tools.vitruv.change.atomic.EChange;
import tools.vitruv.dsls.reactions.runtime.routines.RoutinesFacade;
import tools.vitruv.dsls.reactions.runtime.state.ReactionExecutionState;
import tools.vitruv.dsls.reactions.runtime.structure.AbstractReactionsChangePropagationAction;

/**
 * An {@link AbstractReaction} retrieves a routines facade upon each execution and applies the
 * current execution's state to that facade such that it gets
 * propagated through all routines and their used facades.
 */
public abstract class AbstractReaction extends AbstractReactionsChangePropagationAction implements Reaction {
  private Function<ReactionExecutionState, RoutinesFacade> routinesFacadeGenerator;
  /**
   * Assigns routinesFacadeGenerator to retrieve routine facades from.
   *
   * @param routinesFacadeGenerator - {@link Function}
   */
  protected AbstractReaction(
      Function<ReactionExecutionState, RoutinesFacade> routinesFacadeGenerator) {
        this.routinesFacadeGenerator = routinesFacadeGenerator;
  }

  public AbstractReaction() {}

  @Override
  public void registerRoutinesFacadesGenerator() {
    this.registerRoutinesFacadesGenerator(routinesFacadeGenerator);
  }

  @Override
  public void execute(EChange<EObject> change, ReactionExecutionState reactionExecutionState) {
    val routinesFacade = this.getCPS().getReactionsContainer().getRoutinesFacadeGenerator(this.getID()).apply(reactionExecutionState);
    routinesFacade.pushCaller(this);
    try {
      executeReaction(change, reactionExecutionState, routinesFacade);
    } finally {
      routinesFacade.dropLastCaller();
    }
  }

  @Override
  public Object executeAction(EChange<EObject> change, ReactionExecutionState state) {
      execute(change, state);
      return true;
  }

  /**
   * Acutally executes this reaction with the appropriate routines, as represented by routineFacade.
   *
   * @param change - {@link EChange}
   * @param executionState - {@link ReactionExecutionState}
   * @param routinesFacade - {@link RoutinesFacade}
   */
  protected abstract void executeReaction(EChange<EObject> change,
      ReactionExecutionState executionState, RoutinesFacade routinesFacade);

}
