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

import tools.vitruv.dsls.reactions.runtime.controlflows.DefaultControlFlow;
import tools.vitruv.dsls.reactions.runtime.correspondence.CorrespondenceFactory;
import tools.vitruv.dsls.reactions.runtime.correspondence.ReactionsCorrespondence;
import tools.vitruv.dsls.reactions.runtime.state.ReactionExecutionState;
import tools.vitruv.dsls.reactions.runtime.structure.CallHierarchyHaving;

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
      var cfGen = new ChangePropagationActionGenerator(DefaultControlFlow::new);
      this.rootControlFlowID = ((DefaultControlFlow) cfGen.generateActionInstance()).getID();
      this.con.addAbsentActionGen(rootControlFlowID, new ChangePropagationActionGenerator(DefaultControlFlow::new));
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

  public Object delegateToReaction(CallHierarchyHaving caller, EChange<EObject> change, ReactionExecutionState state, String reactionID) {
      var re = this.getActionContainer().getReaction(reactionID);
      re.setCallHierarchyHaving(caller);
      return re.executeAction(change, state);
  }

  public Object delegateToRoutine(CallHierarchyHaving caller, ReactionExecutionState state, String routineID) {
      var ro = this.getActionContainer().getRoutine(routineID);
      ro.setCallHierarchyHaving(caller);
      return ro.execute();
  }

  public Object delegateToControlFlow(CallHierarchyHaving caller, EChange<EObject> change, ReactionExecutionState state, String controlFlowID) {
      var cf = this.getActionContainer().getControlFlow(controlFlowID);
      cf.setCallHierarchyHaving(caller);
      return cf.executeAction(change, state);
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
    this.con.addAbsentActionGen(((AbstractReaction) reaction).getID(), new ReactionsChangePropagationActionGenerator((AbstractReaction) reaction));
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
    delegateToControlFlow(null, change, state, rootControlFlowID);
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
