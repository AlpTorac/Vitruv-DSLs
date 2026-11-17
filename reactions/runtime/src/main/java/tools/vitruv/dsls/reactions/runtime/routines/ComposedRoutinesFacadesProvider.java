package tools.vitruv.dsls.reactions.runtime.routines;

import java.util.LinkedHashSet;
import java.util.Set;

import tools.vitruv.dsls.reactions.runtime.state.ReactionExecutionState;
import tools.vitruv.dsls.reactions.runtime.structure.ReactionsImportPath;

public class ComposedRoutinesFacadesProvider extends AbstractRoutinesFacadesProvider {
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

    @Override
    public AbstractRoutinesFacade createRoutinesFacade(ReactionsImportPath reactionsImportPath) {
        for (var provider : providers) {
            try {
                var fac = provider.getRoutinesFacade(reactionsImportPath);
                if (fac != null)
                    return fac;
            } catch (IllegalArgumentException e) {
                /*
                 * The current provider cannot produce the desired AbstractRoutinesFacade, so
                 * keep iterating the providers.
                 * 
                 * RoutinesFacadesProviders throw IllegalArgumentExceptions instead of
                 * returning null, when they cannot create a RoutinesFacade for the given
                 * ReactionsImportPath. Since there is currently no clean way to check whether a
                 * certain provider considers a certain ReactionsImportPath and there are
                 * multiple providers, the only way to iterate through them is to catch the
                 * exceptions they throw.
                 */
            }
        }
        return null;
    }
}
