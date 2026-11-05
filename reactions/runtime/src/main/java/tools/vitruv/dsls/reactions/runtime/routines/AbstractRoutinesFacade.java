package tools.vitruv.dsls.reactions.runtime.routines;

import java.lang.reflect.InvocationTargetException;
import java.util.Deque;
import java.util.LinkedList;

import com.google.common.collect.Lists;

import tools.vitruv.dsls.reactions.runtime.state.ReactionExecutionState;
import tools.vitruv.dsls.reactions.runtime.structure.CallHierarchyHaving;
import tools.vitruv.dsls.reactions.runtime.structure.Loggable;
import tools.vitruv.dsls.reactions.runtime.structure.ReactionsImportPath;


/**
 * Note: All methods start with an underscore here to not conflict with the methods 
 * that are generated from the routines by concrete implementations.
 */
public abstract class AbstractRoutinesFacade extends Loggable implements RoutinesFacade {
  /** 
   * Used by concrete implementations to request routines facades of executed routines.
   */
  private RoutinesFacadesProvider routinesFacadesProvider;
  /**
   * The absolute path inside the import hierarchy to the segment this routines facade belongs to.
   * Never null.
   */
  private ReactionsImportPath reactionsImportPath;
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
  protected AbstractRoutinesFacade(
      RoutinesFacadesProvider routinesFacadesProvider, ReactionsImportPath reactionsImportPath) {
    this.routinesFacadesProvider = routinesFacadesProvider;
    this.reactionsImportPath = reactionsImportPath;
  }

  public AbstractRoutinesFacade() {}

  private java.lang.reflect.Method findRoutineCallMethod(String routineName, Class<?>[] paramClss) {
      /*
        FIXME: There is currently no way to cleanly execute a Routine through an
        arbitrary RoutinesFacade, since concrete RoutinesFacades declare methods
        that find and call the Routines, hence the usage of the reflection library
      */

      try {
        var metMatches = Lists.newArrayList(this.getClass().getDeclaredMethods());

        // Eliminate methods with mismatching parameter count
        metMatches.removeIf(m -> m.getParameterCount() != paramClss.length);
        if (metMatches.size() == 1) {
          return metMatches.get(0);
        }

        // Eliminate methods with mismatching names
        metMatches.removeIf(m -> !m.getName().equals(routineName));
        if (metMatches.size() == 1) {
          return metMatches.get(0);
        }

        // Filter by param types, look for signature compatibility
        for (int i = 0; i < paramClss.length; i++) {
          final var idx = i;
          metMatches.removeIf(m -> !m.getParameters()[idx].getType().isAssignableFrom(paramClss[idx]));
          if (metMatches.size() == 1) {
            return metMatches.get(0);
          }
        }

        return null;
      } catch (SecurityException e) {
        throw new IllegalStateException("Security exception while looking for: " + routineName, e);
      }
  }

  @Override
  public Object executeRoutine(String routineName, Class<?>[] paramClss, Object[] params) {
      /*
        FIXME: There is currently no way to cleanly execute a Routine through an
        arbitrary RoutinesFacade, since concrete RoutinesFacades declare methods
        that find and call the Routines, hence the usage of the reflection library
      */

      java.lang.reflect.Method routineCallMethod = findRoutineCallMethod(routineName, paramClss);
      Object result;
      try {
        result = routineCallMethod.invoke(this, params);
      } catch (IllegalAccessException | InvocationTargetException e) {
        throw new IllegalArgumentException("Exception while calling: " + routineName, e);
      }
      return result;
  }

  public void setRoutinesFacadesProvider(RoutinesFacadesProvider routinesFacadesProvider) {
    this.routinesFacadesProvider = routinesFacadesProvider;
  }

  public void setReactionsImportPath(ReactionsImportPath reactionsImportPath) {
    this.reactionsImportPath = reactionsImportPath;
  }

  /**
   * Returns the routine facade provider of this facade.
   *
   * @return routinesFacadesProvider
   */
  public RoutinesFacadesProvider getRoutinesFacadesProvider() {
    return routinesFacadesProvider;
  }

  /**
   * Returns the import path for the segment this routines facade belongs to.
   *
   * @return reactionsImportPath
   */
  public ReactionsImportPath getReactionsImportPath() {
    return reactionsImportPath;
  }

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
