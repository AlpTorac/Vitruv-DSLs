package tools.vitruv.dsls.reactions.runtime.reactions;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.emf.ecore.EObject;

import com.google.common.reflect.ClassPath;
import com.google.common.reflect.ClassPath.ClassInfo;

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
import tools.vitruv.dsls.reactions.runtime.routines.RoutinesFacade;
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

  public Object delegateToRoutine(ReactionExecutionState state, String facadeGenID, String routineID, Class<?>[] paramClss, Object[] params) {
      var container = getActionContainer();
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
   * Marked as deprecated for backwards compatibility purposes, since generated mir code requires this method
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
    con.getControlFlow(this.rootControlFlowID).propagateChange(change, getState(correspondenceModel,resourceAccess));
  }
  
  private static EditableCorrespondenceModelView<ReactionsCorrespondence> getReactionsView(
      EditableCorrespondenceModelView<Correspondence> correspondenceModel) {
    return correspondenceModel.getEditableView(ReactionsCorrespondence.class, 
        CorrespondenceFactory.eINSTANCE::createReactionsCorrespondence);
  }

  @Override
  public void setUserInteractor(UserInteractor userInteractor) {
    super.setUserInteractor(userInteractor);
    getActionContainer().clear();
    setup();
  }

  public void registerRoutinesFacades() {
    /*
      FIXME: There is currently no way to cleanly register the routines
      without modifying the code generation. Therefore, use the CPR layout
      to find out, which routines facade generators directly call which routines.
      Then register them, in order to allow them to be located during
      change propagation.

      An alternative to this method, without code generation modifications,
      is to manually register all routines facade generators within the
      setup() method.
    */
    List<ClassInfo> relevantClasses;
    try {
        relevantClasses = ClassPath.from(ClassLoader.getSystemClassLoader())
                .getAllClasses().stream()
                .toList();
    } catch (IOException e) {
        throw new IllegalStateException(e);
    }

    // The directory, under which all files related to all generated routines
    //
    // Note that this directory may include the routines generated for other
    // .reactions files too. This way, all possible imports are covered
    var routinesRootDir = new File("").getAbsoluteFile().toPath().resolve("target").resolve("generated-sources")
            .resolve("reactions").resolve("mir").resolve("routines");

    for (var routinesDir : routinesRootDir.toFile().listFiles((f) -> f.isDirectory())) {
        final File routinesFacade = routinesDir.listFiles((f) -> f.getName().endsWith("RoutinesFacade.java"))[0];
        // Remove the file extension
        var routinesFacadeClassName = routinesFacade.getName().split("\\.")[0];

        Class<?> routineFacadeCls = relevantClasses.stream()
                .filter(cls -> cls.getSimpleName().equals(routinesFacadeClassName))
                .findFirst().orElse(null).load();

        Function<ReactionExecutionState, RoutinesFacade> routinesFacadesGenerator = (state) -> {
          RoutinesFacade routineFacadeInstance;
          try {
              routineFacadeInstance = (RoutinesFacade) routineFacadeCls.getDeclaredConstructors()[0].newInstance(state,
                      null);
          } catch (InstantiationException | IllegalAccessException | IllegalArgumentException
                  | InvocationTargetException | SecurityException e) {
              throw new IllegalStateException(e);
          }
          return routineFacadeInstance;
        };

        for (var routineFile : routinesDir.listFiles((f) -> f.getName().endsWith("Routine.java"))) {
            // Remove the file extension
            var routineClassName = routineFile.getName().split("\\.")[0];

            // De-capitalise the first letter, in order to comply with the routine call convention
            var routineNameToRegister = routineClassName.substring(0,1).toLowerCase() + routineClassName.substring(1);
            this.getActionContainer().setRoutinesFacadesGenerator(routineNameToRegister,
                    routinesFacadesGenerator);
        }
    }
  }

  /**
   * Sets up this {@link ChangePropagationSpecification}.
   */
  protected abstract void setup();
}
