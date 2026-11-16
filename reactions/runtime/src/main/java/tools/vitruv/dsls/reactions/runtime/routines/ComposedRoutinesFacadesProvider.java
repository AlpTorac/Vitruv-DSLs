package tools.vitruv.dsls.reactions.runtime.routines;

import java.util.ArrayList;
import java.util.List;

import tools.vitruv.dsls.reactions.runtime.state.ReactionExecutionState;
import tools.vitruv.dsls.reactions.runtime.structure.ReactionsImportPath;

public class ComposedRoutinesFacadesProvider extends AbstractRoutinesFacadesProvider {
    private List<RoutinesFacadesProvider> providers = new ArrayList<>();

    public ComposedRoutinesFacadesProvider(ReactionExecutionState executionState) {
        super(executionState);
    }

    public boolean addProvider(RoutinesFacadesProvider provider) {
        return this.providers.add(provider);
    }

    public boolean removeProvider(RoutinesFacadesProvider provider) {
        return this.providers.remove(provider);
    }

    @Override
    public AbstractRoutinesFacade createRoutinesFacade(ReactionsImportPath reactionsImportPath) {
        for (var provider : providers) {
            try {
                if (provider instanceof AbstractRoutinesFacadesProvider absProv) {
                    return absProv.createRoutinesFacade(reactionsImportPath);
                } else {
                    return provider.getRoutinesFacade(reactionsImportPath);
                }
            } catch (IllegalArgumentException e) {
                // The current provider cannot produce the desired routines facade
                // keep iterating the providers
            }
        }
        return null;
    }
}
