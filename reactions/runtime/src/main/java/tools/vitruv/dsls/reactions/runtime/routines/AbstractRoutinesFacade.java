package tools.vitruv.dsls.reactions.runtime.routines;

import java.lang.reflect.InvocationTargetException;

import java.util.ArrayList;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import tools.vitruv.dsls.reactions.runtime.state.ReactionExecutionState;
import tools.vitruv.dsls.reactions.runtime.structure.AbstractReactionsChangePropagationAction;
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

  /**
   * Looks for a routine call method, whose name matches the given routineName. Assumes that only one method will be matched, otherwise throws an exception.
   */
  private java.lang.reflect.Method findRoutineCallMethod(String routineName) {
      java.lang.reflect.Method[] mets = null;

      try {
        mets = this.getClass().getDeclaredMethods();
      } catch (SecurityException e) {
        throw new IllegalStateException("Security exception while looking for: " + routineName, e);
      }

      var metMatches = new ArrayList<java.lang.reflect.Method>();
      for (int i = 0; i < mets.length; i++) {
        metMatches.add(mets[i]);
      }

      // Eliminate methods with mismatching names
      metMatches.removeIf(m -> !m.getName().equals(routineName));
      if (metMatches.size() == 1) {
        return metMatches.get(0);
      } else {
        throw new IllegalArgumentException("The given routine name has overloading routine call methods, hence its parameter classes must be specified");
      }
  }

  private java.lang.reflect.Method findRoutineCallMethod(String routineName, Class<?>[] paramClss) {
      /*
        FIXME: There is currently no way to cleanly execute a Routine through an
        arbitrary RoutinesFacade, since concrete RoutinesFacades declare methods
        that find and call the Routines, hence the usage of the reflection library
      */

      java.lang.reflect.Method[] mets = null;

      try {
        mets = this.getClass().getDeclaredMethods();
      } catch (SecurityException e) {
        throw new IllegalStateException("Security exception while looking for: " + routineName, e);
      }

      var metMatches = new ArrayList<java.lang.reflect.Method>();
      for (int i = 0; i < mets.length; i++) {
        metMatches.add(mets[i]);
      }

      // Eliminate methods with mismatching parameter count
      var paramCount = paramClss != null ? paramClss.length : 0;
      metMatches.removeIf(m -> m.getParameterCount() != paramCount);
      if (metMatches.size() == 1) {
        return metMatches.get(0);
      }

      // Eliminate methods with mismatching names
      metMatches.removeIf(m -> !m.getName().equals(routineName));
      if (metMatches.size() == 1) {
        return metMatches.get(0);
      }

      // Filter by param types, look for signature compatibility
      for (int i = 0; i < paramCount; i++) {
        final var idx = i;
        // TODO: Refine the type checking here to match Java behaviour
        metMatches.removeIf(m -> !m.getParameters()[idx].getType().isAssignableFrom(paramClss[idx]));
        if (metMatches.size() == 1) {
          return metMatches.get(0);
        }
      }

      if (metMatches.size() > 1) {
        throw new IllegalArgumentException("The given routineName and paramClss lead to ambiguous routine call methods, hence paramClss must be more precise");
      } else if (metMatches.isEmpty()) {
        throw new IllegalArgumentException("No such routine call method");
      } else {
        throw new IllegalStateException("This case should never be reached");
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

  @Override
  public Object executeRoutine(String routineName, Class<?>[] paramClss) {
      java.lang.reflect.Method routineCallMethod = findRoutineCallMethod(routineName, paramClss);
      Object result;

      var params = lookupDynamicParameters(routineCallMethod);

      try {
        result = routineCallMethod.invoke(this, params);
      } catch (IllegalAccessException | InvocationTargetException e) {
        throw new IllegalArgumentException("Exception while calling: " + routineName, e);
      }
      return result;
  }

  @Override
  public Object executeRoutine(String routineName) {
      java.lang.reflect.Method routineCallMethod = findRoutineCallMethod(routineName);
      Object result;

      var params = lookupDynamicParameters(routineCallMethod);

      try {
        result = routineCallMethod.invoke(this, params);
      } catch (IllegalAccessException | InvocationTargetException e) {
        throw new IllegalArgumentException("Exception while calling: " + routineName, e);
      }
      return result;
  }

  private List<Object> lookupDynamicParameters(java.lang.reflect.Method routineCallMethod) {
      var caller = (AbstractReactionsChangePropagationAction) this.getCurrentCaller();
      // Deliberately use ArrayList, as it allows null values
      var params = new ArrayList<>();
      for (var p : routineCallMethod.getParameters()) {
        var pVal = caller.getParam(p.getName());
        params.add(pVal);
      }
      return params;
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
