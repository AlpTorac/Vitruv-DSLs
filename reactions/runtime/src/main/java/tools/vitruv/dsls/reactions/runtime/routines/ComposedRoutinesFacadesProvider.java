package tools.vitruv.dsls.reactions.runtime.routines;

import java.util.LinkedHashSet;
import java.util.Set;

import tools.vitruv.dsls.reactions.runtime.state.ReactionExecutionState;
import tools.vitruv.dsls.reactions.runtime.structure.ReactionsImportPath;

public class ComposedRoutinesFacadesProvider extends RoutinesFacadesProviderBase {
    private final Set<RoutinesFacadesProvider> providers = new LinkedHashSet<>();

    public ComposedRoutinesFacadesProvider(ReactionExecutionState executionState) {
        super(executionState);
    }

    public boolean addProvider(RoutinesFacadesProvider provider) {
        return this.providers.add(provider);
    }

    public boolean removeProvider(RoutinesFacadesProvider provider) {
        return this.providers.remove(provider);
    }

    /**
     * @return The set of providers that have been added to this. Explicitly returns
     *         a LinkedHashSet, in order to preserve the insertion order of the
     *         providers.
     */
    public LinkedHashSet<RoutinesFacadesProvider> getProviders() {
        return new LinkedHashSet<RoutinesFacadesProvider>(providers);
    }

    // TODO: Try and refactor the methods below

    /**
     * Gets the routines facade for the specified absolute reactions import path and
     * the specified type. The returned RoutinesFacade will be exactly of the
     * specified type, if the returned RoutinesFacade is not null.
     *
     * @param <T>                 - the type of the requested routines facade
     * @param facCls              - The type of the requested routines facade, in
     *                            the form of a class object. This parameter is
     *                            necessary, as there is no clean way to type-check
     *                            for generic types.
     * @param reactionsImportPath - The absolute import path (starting with the root
     *                            of the import hierarchy).
     * 
     * @return The routines facade.
     */
    @SuppressWarnings("unchecked")
    public <T extends AbstractRoutinesFacade> T getRoutinesFacadeOfExactType(Class<T> facCls,
            ReactionsImportPath reactionsImportPath) {
        for (var provider : providers) {
            try {
                var fac = provider.getRoutinesFacade(reactionsImportPath);
                if (fac != null && facCls.equals(fac.getClass())) {
                    /*
                     * If fac is null or is of an undesired type, continue with the next provider.
                     */
                    return (T) fac;
                }
            } catch (IllegalArgumentException e) {
                /*
                 * The current provider cannot produce the desired AbstractRoutinesFacade, so
                 * keep iterating the providers.
                 * 
                 * RoutinesFacadesProviders throw IllegalArgumentExceptions instead of
                 * returning null, when they cannot create a RoutinesFacade for the given
                 * ReactionsImportPath. There is currently no clean way to check whether a
                 * certain provider considers a certain ReactionsImportPath.
                 * 
                 * As the purpose of this class is to compose providers, iterate through them
                 * and to return a RoutinesFacade of the desired type, it has to iterate the
                 * added providers. Since iterating them is only possible by catching the
                 * exceptions they may throw, this try-catch block is necessary in general.
                 */
            }
        }
        return null;
    }

    /**
     * Gets the routines facade for the specified absolute reactions import path and
     * of a compatible type with respect to the given type. The returned
     * RoutinesFacade will either be of type facClsOrSuperCls or a sub-type thereof,
     * if the returned RoutinesFacade is not null.
     *
     * @param <T>                 - the type of the requested routines facade
     * @param facClsOrSuperCls    - The type or super-type of the requested routines
     *                            facade, in the form of a class object. This
     *                            parameter is necessary, as there is no clean way
     *                            to type-check for generic types.
     * @param reactionsImportPath - The absolute import path (starting with the root
     *                            of the import hierarchy).
     * 
     * @return The routines facade.
     */
    @SuppressWarnings("unchecked")
    public <T extends AbstractRoutinesFacade> T getRoutinesFacadeOfCompatibleType(Class<T> facClsOrSuperCls,
            ReactionsImportPath reactionsImportPath) {
        for (var provider : providers) {
            try {
                var fac = provider.getRoutinesFacade(reactionsImportPath);
                if (fac != null && facClsOrSuperCls.isAssignableFrom(fac.getClass())) {
                    /*
                     * If fac is null or is of an undesired type, continue with the next provider.
                     */
                    return (T) fac;
                }
            } catch (IllegalArgumentException e) {
                /*
                 * The current provider cannot produce the desired AbstractRoutinesFacade, so
                 * keep iterating the providers.
                 * 
                 * RoutinesFacadesProviders throw IllegalArgumentExceptions instead of
                 * returning null, when they cannot create a RoutinesFacade for the given
                 * ReactionsImportPath. There is currently no clean way to check whether a
                 * certain provider considers a certain ReactionsImportPath.
                 * 
                 * As the purpose of this class is to compose providers, iterate through them
                 * and to return a RoutinesFacade of the desired type, it has to iterate the
                 * added providers. Since iterating them is only possible by catching the
                 * exceptions they may throw, this try-catch block is necessary in general.
                 */
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * Returns the first non-null RoutinesFacade that a provider generates. Does not
     * check whether the returned RoutinesFacade's type is T, as there is no
     * reliable way to check for generic types. Use
     * {@link #getRoutinesFacadeOfCompatibleType(Class, ReactionsImportPath)} for a
     * more precise type-checking, if there are multiple providers that could return
     * non-null RoutinesFacades for the given ReactionsImportPath.
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T extends AbstractRoutinesFacade> T getRoutinesFacade(ReactionsImportPath reactionsImportPath) {
        for (var provider : providers) {
            try {
                var fac = provider.getRoutinesFacade(reactionsImportPath);
                if (fac != null) {
                    /*
                     * If fac is null or is of an undesired type, continue with the next provider.
                     */
                    return (T) fac;
                }
            } catch (IllegalArgumentException e) {
                /*
                 * The current provider cannot produce the desired AbstractRoutinesFacade, so
                 * keep iterating the providers.
                 * 
                 * RoutinesFacadesProviders throw IllegalArgumentExceptions instead of
                 * returning null, when they cannot create a RoutinesFacade for the given
                 * ReactionsImportPath. There is currently no clean way to check whether a
                 * certain provider considers a certain ReactionsImportPath.
                 * 
                 * As the purpose of this class is to compose providers, iterate through them
                 * and to return a RoutinesFacade of the desired type, it has to iterate the
                 * added providers. Since iterating them is only possible by catching the
                 * exceptions they may throw, this try-catch block is necessary in general.
                 */
            }
        }
        return null;
    }
}
