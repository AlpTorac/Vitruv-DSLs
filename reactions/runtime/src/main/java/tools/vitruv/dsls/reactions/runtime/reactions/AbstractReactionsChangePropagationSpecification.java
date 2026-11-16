package tools.vitruv.dsls.reactions.runtime.reactions;

import java.util.ArrayList;
import java.util.List;

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
import tools.vitruv.dsls.reactions.runtime.routines.Routine;
import tools.vitruv.dsls.reactions.runtime.routines.RoutinesFacadesProvider;
import tools.vitruv.dsls.reactions.runtime.state.ReactionExecutionState;
import tools.vitruv.dsls.reactions.runtime.structure.AbstractChangePropagationAction;
import tools.vitruv.dsls.reactions.runtime.structure.CallHistoryHaving;

/**
 * A {@link ChangePropagationSpecification} that executes {@link Reaction}s.
 */
public abstract class AbstractReactionsChangePropagationSpecification
    extends AbstractChangePropagationSpecification implements CallHistoryHaving {
  private static Logger logger = LogManager.getLogger(
      AbstractReactionsChangePropagationSpecification.class);

  private String rootControlFlowID;
  private ReactionsChangePropagationActionContainer con;

  private final List<AbstractChangePropagationAction> calledActions = new ArrayList<>();

  /**
   * Creates an {@link AbstractReactionsChangePropagationSpecification} without
   * reactions
   * between the metamodels described by sourceMetamodelDescriptor and
   * targetMetamodelDescriptor.
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
      // Assume that the root control flow will be added in this.setup()
    } else {
      var cfGen = new ChangePropagationActionGenerator(DefaultControlFlow::new);
      this.rootControlFlowID = ((DefaultControlFlow) cfGen.generateActionInstance()).getID();
      this.con.addAbsentActionGen(this.rootControlFlowID, cfGen);
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

  /**
   * Replaces the current {@link ReactionsChangePropagationActionContainer} with
   * con, without transfering any existing action generators.
   */
  public void setActionContainer(ReactionsChangePropagationActionContainer con) {
    this.con = con;
  }

  public Routine getRoutine(String routineID) {
    return this.getActionContainer().getRoutine(routineID);
  }

  /**
   * Adds reaction to this specification.
   *
   * Marked as deprecated and kept for backwards compatibility purposes, since
   * generated mir code requires
   * this method. Use {@code getActionContainer().addReaction(...)} in the future.
   * 
   * @param reaction - {@link Reaction}
   */
  @Deprecated
  protected void addReaction(Reaction reaction) {
    var id = ((AbstractReaction) reaction).getID();
    this.con.addAbsentActionGen(id,
        new ReactionsChangePropagationActionGenerator(() -> ((AbstractReaction) reaction).clone())
            .withBaseIDOverride(id));
  }

  /**
   * {@inheritDoc}
   * By default, an {@link AbstractReactionsChangePropagationSpecification}
   * handles any change.
   */
  @Override
  public boolean doesHandleChange(EChange<EObject> change,
      EditableCorrespondenceModelView<Correspondence> correspondenceModel) {
    return true;
  }

  public ReactionExecutionState getState() {
    return new ReactionExecutionState(getUserInteractor(), null, null, this);
  }

  protected ReactionExecutionState getState(EditableCorrespondenceModelView<Correspondence> correspondenceModel,
      ResourceAccess resourceAccess) {
    EditableCorrespondenceModelView<ReactionsCorrespondence> view = null;
    if (correspondenceModel != null)
      view = getReactionsView(correspondenceModel);
    return new ReactionExecutionState(
        getUserInteractor(),
        view,
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
    var state = getState(correspondenceModel, resourceAccess);
    var cf = this.getActionContainer().getControlFlow(rootControlFlowID);
    addCalledAction(cf);
    cf.executeAction(change, state);
  }

  private static EditableCorrespondenceModelView<ReactionsCorrespondence> getReactionsView(
      EditableCorrespondenceModelView<Correspondence> correspondenceModel) {
    return correspondenceModel.getEditableView(ReactionsCorrespondence.class,
        CorrespondenceFactory.eINSTANCE::createReactionsCorrespondence);
  }

  @Override
  public void setUserInteractor(UserInteractor userInteractor) {
    super.setUserInteractor(userInteractor);
    /*
     * The clear() method call was most likely made, in order to reset the
     * AbstractRoutinesFacades, as they "cache" the RoutinesFacades they generate.
     * Since now a fresh Reaction instance is generated for each individual Reaction
     * call, this is no longer necessary.
     * 
     * The reason for generating a fresh Reaction instance per Reaction call is that
     * dynamic parameters have been introduced. By isolating the Reaction instances
     * for individual Reaction calls, the dynamic parameters are automatically
     * scoped as intended.
     */
    setup();
  }

  @Override
  public List<AbstractChangePropagationAction> getCalledActions() {
    return List.copyOf(calledActions);
  }

  @Override
  public void clearCalledActions() {
    this.calledActions.clear();
  }

  @Override
  public void addCalledAction(AbstractChangePropagationAction calledAction) {
    this.calledActions.add(calledAction);
  }

  public RoutinesFacadesProvider getRoutinesFacadesProvider(final ReactionExecutionState executionState) {
    return createRoutinesFacadesProvider(executionState);
  }

  protected RoutinesFacadesProvider createRoutinesFacadesProvider(final ReactionExecutionState executionState) {
    throw new UnsupportedOperationException(
        "This change propagation specification does not use a RoutinesFacadesProvider");
  }

  /**
   * Sets up this {@link ChangePropagationSpecification}.
   */
  protected abstract void setup();
}
