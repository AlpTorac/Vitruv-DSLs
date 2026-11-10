package tools.vitruv.dsls.reactions.runtime.reactions;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.emf.ecore.EObject;

import tools.vitruv.change.atomic.EChange;
import tools.vitruv.change.composite.MetamodelDescriptor;
import tools.vitruv.change.correspondence.Correspondence;
import tools.vitruv.change.correspondence.view.EditableCorrespondenceModelView;
import tools.vitruv.change.interaction.UserInteractor;
import tools.vitruv.change.propagation.ChangePropagationSpecification;
import tools.vitruv.change.propagation.impl.AbstractChangePropagationSpecification;
import tools.vitruv.change.utils.ResourceAccess;

import tools.vitruv.dsls.reactions.runtime.controlflows.DefaultReactionsChangePropagationControlFlow;
import tools.vitruv.dsls.reactions.runtime.correspondence.CorrespondenceFactory;
import tools.vitruv.dsls.reactions.runtime.correspondence.ReactionsCorrespondence;
import tools.vitruv.dsls.reactions.runtime.state.ReactionExecutionState;

/**
 * A {@link ChangePropagationSpecification} that executes {@link Reaction}s.
 */
public abstract class AbstractReactionsChangePropagationSpecification 
    extends AbstractChangePropagationSpecification {
  private static Logger logger = LogManager.getLogger(
      AbstractReactionsChangePropagationSpecification.class);

  private String rootControlFlowID;
  private ReactionsChangePropagationActionContainer con;

  /**
   * Creates an {@link AbstractReactionsChangePropagationSpecification} without reactions 
   * between the metamodels described by sourceMetamodelDescriptor and targetMetamodelDescriptor.
   *
   * @param sourceMetamodelDescriptor - {@link MetamodelDescriptor}
   * @param targetMetamodelDescriptor - {@link MetamodelDescriptor}
   */
  protected AbstractReactionsChangePropagationSpecification(
      MetamodelDescriptor sourceMetamodelDescriptor,
      MetamodelDescriptor targetMetamodelDescriptor) {
    this(sourceMetamodelDescriptor, targetMetamodelDescriptor, null);
  }

  public AbstractReactionsChangePropagationSpecification(
      MetamodelDescriptor sourceMetamodelDescriptor,
      MetamodelDescriptor targetMetamodelDescriptor,
    String rootControlFlowID) {
    super(sourceMetamodelDescriptor, targetMetamodelDescriptor);
    this.con = new DefaultReactionsChangePropagationActionContainer();
    if (rootControlFlowID != null) {
      this.rootControlFlowID = rootControlFlowID;
    } else {
      var defaultControlFlow = new DefaultReactionsChangePropagationControlFlow();
      this.rootControlFlowID = defaultControlFlow.getID();
      this.con.addControlFlow(defaultControlFlow);
    }
    this.setup();
  }

  public void setRootControlFlowID(String rootControlFlowID) {
    this.rootControlFlowID = rootControlFlowID;
  }

  public String getRootControlFlowID() {
    return this.rootControlFlowID;
  }

  public ReactionsChangePropagationActionContainer getActionContainer() {
      return con;
  }

  public void setActionContainer(ReactionsChangePropagationActionContainer con) {
      this.con = con;
  }

  public Object delegateToReaction(EChange<EObject> change, ReactionExecutionState state, String reactionID) {
      var container = getActionContainer();
      return container.hasReaction(reactionID) ? container.performReaction(change, state, reactionID) : null;
  }

  public Object delegateToRoutine(ReactionExecutionState state, String facadeGenID, String routineID) {
      var container = getActionContainer();
      if (container.hasRoutinesFacadesGenerator(facadeGenID)) {
          return container.performRoutine(state, routineID);
      }
      return null;
  }

  public Object delegateToControlFlow(EChange<EObject> change, ReactionExecutionState state, String controlFlowID) {
    var container = getActionContainer();
    return container.hasControlFlow(controlFlowID) ? container.performControlFlow(change, state, controlFlowID) : null;
  }

  /**
   * Adds reaction to this specification.
   *
   * Marked as deprecated and kept for backwards compatibility purposes, since generated mir code requires
   * this method. Use {@code getActionContainer().addReaction(...)} in the future.
   * 
   * @param reaction - {@link Reaction}
   */
  @Deprecated
  protected void addReaction(Reaction reaction) {
    this.con.addReaction(((AbstractReaction) reaction).getID(), (AbstractReaction) reaction);
  }

  /**
   * {@inheritDoc}
   * By default, an {@link AbstractReactionsChangePropagationSpecification} handles any change.
   */
  @Override
  public boolean doesHandleChange(EChange<EObject> change,
      EditableCorrespondenceModelView<Correspondence> correspondenceModel) {
    return true;
  }

  protected ReactionExecutionState getState(EditableCorrespondenceModelView<Correspondence> correspondenceModel, 
      ResourceAccess resourceAccess) {
    return new ReactionExecutionState(
          getUserInteractor(),
          getReactionsView(correspondenceModel),
          resourceAccess,
          this);
  }

  /**
   * {@inheritDoc}
   * These modifications happen by executing all reactions this specification has.
   */
  @Override
  public void propagateChange(EChange<EObject> change, 
      EditableCorrespondenceModelView<Correspondence> correspondenceModel, 
      ResourceAccess resourceAccess) {
    logger.trace("Call relevant reactions from %s to %s", 
        getSourceMetamodelDescriptor(), getTargetMetamodelDescriptor());
    var state = getState(correspondenceModel,resourceAccess);
    con.performControlFlow(change, state, rootControlFlowID);
  }
  
  private static EditableCorrespondenceModelView<ReactionsCorrespondence> getReactionsView(
      EditableCorrespondenceModelView<Correspondence> correspondenceModel) {
    return correspondenceModel.getEditableView(ReactionsCorrespondence.class, 
        CorrespondenceFactory.eINSTANCE::createReactionsCorrespondence);
  }

  @Override
  public void setUserInteractor(UserInteractor userInteractor) {
    super.setUserInteractor(userInteractor);
    setup();
  }

  /**
   * Sets up this {@link ChangePropagationSpecification}.
   */
  protected abstract void setup();
}
