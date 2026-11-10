package tools.vitruv.dsls.reactions.runtime.routines;

import tools.vitruv.dsls.reactions.runtime.structure.ReactionsImportPath;

/**
 * Note: All methods start with an underscore here to not conflict with the methods 
 * that are generated from the routines by concrete implementations.
 */
public abstract class AbstractRoutinesFacade extends RoutinesFacadeBase {
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
}
