package tools.vitruv.dsls.reactions.runtime.routines;

import tools.vitruv.dsls.reactions.runtime.state.ReactionExecutionState;

public abstract class RoutinesFacadesProviderBase implements RoutinesFacadesProvider 
// , Cloneable
{
  private ReactionExecutionState executionState;

  /**
   * Creates a new {@link AbstractRoutinesFacadesProvider}.
   *
   * @param executionState - {@link ReactionExecutionState}
   */
  protected RoutinesFacadesProviderBase(ReactionExecutionState executionState) {
    this.executionState = executionState;
  }

  public ReactionExecutionState getExecutionState() {
    return executionState;
  }

  public void setExecutionState(ReactionExecutionState executionState) {
    this.executionState = executionState;
  }

  // /**
  //  * Creates a clone of this instance
  //  * 
  //  * @see java.lang.Object#clone()
  //  */
  // @Override
  // public RoutinesFacadesProviderBase clone() {
  //   try {
  //     var clone = (RoutinesFacadesProviderBase) super.clone();
  //     if (executionState != null) {
  //       clone.executionState = new ReactionExecutionState(executionState.userInteractor(),
  //           executionState.getCorrespondenceModel(), executionState.getResourceAccess(),
  //           executionState.changePropagationObservable());
  //     }
  //     return clone;
  //   } catch (CloneNotSupportedException e) {
  //     throw new UnsupportedOperationException(e);
  //   }
  // }
}
