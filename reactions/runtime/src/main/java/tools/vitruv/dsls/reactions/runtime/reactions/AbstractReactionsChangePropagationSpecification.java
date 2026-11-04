package tools.vitruv.dsls.reactions.runtime.reactions;

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

  private ReactionsContainer reactions;

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
    super(sourceMetamodelDescriptor, targetMetamodelDescriptor);
    this.reactions = new DefaultReactionsContainer();
    this.setup();
  }

  public AbstractReactionsChangePropagationSpecification(
      MetamodelDescriptor sourceMetamodelDescriptor,
      MetamodelDescriptor targetMetamodelDescriptor,
    ReactionsContainer container) {
    super(sourceMetamodelDescriptor, targetMetamodelDescriptor);
    this.reactions = container;
    this.setup();
  }

  public void setReactionsContainer(ReactionsContainer con) {
    this.reactions = con;
    this.setup();
  }

  public ReactionsContainer getReactionsContainer() {
    return this.reactions;
  }

  public Object delegateToReaction(EChange<EObject> change, ReactionExecutionState state, String reactionID) {
      var container = getReactionsContainer();
      return container.hasReaction(reactionID) ? container.performReaction(change, state, reactionID) : null;
  }

  public Object delegateToRoutine(ReactionExecutionState state, String facadeGenID, String routineID, Class<?>[] paramClss, Object[] params) {
      var container = getReactionsContainer();
      if (container.hasRoutinesFacadesGenerator(facadeGenID)) {
          return paramClss == null ? container.performRoutine(state, facadeGenID, routineID, params)
                  : container.performRoutine(state, facadeGenID, routineID, paramClss, params);
      }
      return null;
  }

  public Object delegateToRoutine(ReactionExecutionState state, String facadeGenID, String routineID, Object[] params) {
    return delegateToRoutine(state, facadeGenID, routineID, List.of(params).stream().map(p -> p.getClass()).toArray(Class[]::new), params);
  }

  /**
   * Adds reaction to this specification.
   *
   * @param reaction - {@link Reaction}
   */
  public void addReaction(Reaction reaction) {
    reaction.setCPS(this);
    reaction.registerRoutinesFacadesGenerator();
    this.reactions.addReaction(reaction);
  }

  public void addReaction(String reactionID, Reaction reaction) {
    reaction.setCPS(this);
    reaction.registerRoutinesFacadesGenerator();
    this.reactions.addReaction(reactionID, reaction);
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
    reactions.propagateChange(change, getState(correspondenceModel,resourceAccess));
  }
  
  private static EditableCorrespondenceModelView<ReactionsCorrespondence> getReactionsView(
      EditableCorrespondenceModelView<Correspondence> correspondenceModel) {
    return correspondenceModel.getEditableView(ReactionsCorrespondence.class, 
        CorrespondenceFactory.eINSTANCE::createReactionsCorrespondence);
  }

  @Override
  public void setUserInteractor(UserInteractor userInteractor) {
    super.setUserInteractor(userInteractor);
    reactions.clear();
    setup();
  }

  /**
   * Sets up this {@link ChangePropagationSpecification}.
   */
  protected abstract void setup();
}
