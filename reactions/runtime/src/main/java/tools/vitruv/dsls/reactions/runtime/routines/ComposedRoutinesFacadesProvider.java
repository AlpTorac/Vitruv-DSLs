package tools.vitruv.dsls.reactions.runtime.routines;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import tools.vitruv.dsls.reactions.runtime.state.ReactionExecutionState;
import tools.vitruv.dsls.reactions.runtime.structure.ReactionsImportPath;

public class ComposedRoutinesFacadesProvider extends RoutinesFacadesProviderBase {
    private final static Predicate<RoutinesFacade> ALWAYS_TRUE_PRED = (fac) -> true;
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

    /**
     * {@inheritDoc}
     * 
     * <p>
     * Returns the first non-null RoutinesFacade that a provider generates, if any
     * provider generates a RoutinesFacade. Does not check whether the returned
     * RoutinesFacade's type is T, as there is no reliable way to check for generic
     * types. Use
     * {@link #getRoutinesFacade(ReactionsImportPath, Predicate)} to specify
     * constraints on the returned RoutinesFacade.
     */
    @Override
    public <T extends AbstractRoutinesFacade> T getRoutinesFacade(ReactionsImportPath reactionsImportPath) {
        return getRoutinesFacade(reactionsImportPath, ALWAYS_TRUE_PRED);
    }

    /**
     * {@inheritDoc}
     * 
     * @param cond - Constraints that the returned RoutinesFacade must fulfil, apart
     *             from being non-null.
     */
    @SuppressWarnings("unchecked")
    public <T extends AbstractRoutinesFacade> T getRoutinesFacade(ReactionsImportPath reactionsImportPath,
            Predicate<RoutinesFacade> cond) {
        var facList = getRoutinesFacades(reactionsImportPath, cond, 1);
        return !facList.isEmpty() ? (T) facList.get(0) : null;
    }

    public List<AbstractRoutinesFacade> getRoutinesFacades(ReactionsImportPath reactionsImportPath) {
        return getRoutinesFacades(reactionsImportPath, ALWAYS_TRUE_PRED);
    }

    public List<AbstractRoutinesFacade> getRoutinesFacades(ReactionsImportPath reactionsImportPath,
            Predicate<RoutinesFacade> cond) {
        return getRoutinesFacades(reactionsImportPath, cond, Integer.MAX_VALUE);
    }

    public List<AbstractRoutinesFacade> getRoutinesFacades(ReactionsImportPath reactionsImportPath,
            int facLimit) {
        return getRoutinesFacades(reactionsImportPath, ALWAYS_TRUE_PRED, facLimit);
    }

    public List<AbstractRoutinesFacade> getRoutinesFacades(ReactionsImportPath reactionsImportPath,
            Predicate<RoutinesFacade> cond, int facLimit) {
        var facList = new ArrayList<AbstractRoutinesFacade>();
        for (var providerIt = providers.iterator(); providerIt.hasNext() && facList.size() < facLimit;) {
            var provider = providerIt.next();
            try {
                var fac = provider.getRoutinesFacade(reactionsImportPath);
                if (fac != null && cond.test(fac)) {
                    facList.add(fac);
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
        return facList;
    }
}
