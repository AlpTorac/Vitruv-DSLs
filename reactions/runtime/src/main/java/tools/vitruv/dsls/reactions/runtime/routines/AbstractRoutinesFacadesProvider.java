package tools.vitruv.dsls.reactions.runtime.routines;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.HashMap;
import java.util.Map;
import tools.vitruv.dsls.reactions.runtime.state.ReactionExecutionState;
import tools.vitruv.dsls.reactions.runtime.structure.ReactionsImportPath;

/**
 * A RoutinesFacadesProvider which caches created routines facades.
 *
 * <p>
 * Implementations are required to override
 * {@link #createRoutinesFacade(ReactionsImportPath)}
 * to create the routines facades of the handled import hierarchy there.
 */
public abstract class AbstractRoutinesFacadesProvider implements RoutinesFacadesProvider
// , Cloneable
{

  /**
   * Routine facades that were created so far.
   */
  private final Map<ReactionsImportPath, AbstractRoutinesFacade> routinesFacades = new HashMap<>();

  private ReactionExecutionState executionState;

  /**
   * Creates a new {@link AbstractRoutinesFacadesProvider}.
   *
   * @param executionState - {@link ReactionExecutionState}
   */
  protected AbstractRoutinesFacadesProvider(ReactionExecutionState executionState) {
    this.executionState = executionState;
  }

  public ReactionExecutionState getExecutionState() {
    return executionState;
  }

  public void setExecutionState(ReactionExecutionState executionState) {
    this.executionState = executionState;
  }

  /**
   * Creates the specified routines facade for the given import path.
   *
   * @param reactionsImportPath - {@link ReactionsImportPath}
   * @return new {@link AbstractRoutinesFacade}
   */
  protected abstract AbstractRoutinesFacade createRoutinesFacade(
      ReactionsImportPath reactionsImportPath);

  @Override
  public <T extends AbstractRoutinesFacade> T getRoutinesFacade(
      ReactionsImportPath reactionsImportPath) {
    checkNotNull(reactionsImportPath, "reactionsImportPath is null");
    // check if we already created the requested routines facade:
    T routinesFacade = (T) routinesFacades.get(reactionsImportPath);
    if (routinesFacade != null) {
      return routinesFacade;
    }

    // create the routines facade:
    routinesFacade = (T) this.createRoutinesFacade(reactionsImportPath);
    if (routinesFacade != null) {
      routinesFacade.setExecutionState(executionState);
      // store created routines facade:
      routinesFacades.put(reactionsImportPath, routinesFacade);
      return routinesFacade;
    }
    return routinesFacade;
  }

  // /**
  //  * Creates a clone of this instance
  //  * 
  //  * @see java.lang.Object#clone()
  //  */
  // @Override
  // public AbstractRoutinesFacadesProvider clone() {
  //   try {
  //     var clone = (AbstractRoutinesFacadesProvider) super.clone();
  //     if (executionState != null) {
  //       clone.executionState = new ReactionExecutionState(executionState.userInteractor(),
  //           executionState.getCorrespondenceModel(), executionState.getResourceAccess(),
  //           executionState.changePropagationObservable());
  //     }
  //     // Do not clone the routinesFacades, in order to avoid provider field inconsistencies
  //     clone.routinesFacades = new HashMap<>();
  //     return clone;
  //   } catch (CloneNotSupportedException e) {
  //     throw new UnsupportedOperationException(e);
  //   }
  // }
}
