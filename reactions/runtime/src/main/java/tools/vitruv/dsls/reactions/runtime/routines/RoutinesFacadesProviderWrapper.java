package tools.vitruv.dsls.reactions.runtime.routines;

import tools.vitruv.dsls.reactions.runtime.reactions.AbstractReactionsChangePropagationSpecification;
import tools.vitruv.dsls.reactions.runtime.state.ReactionExecutionState;
import tools.vitruv.dsls.reactions.runtime.structure.ReactionsImportPath;

public class RoutinesFacadesProviderWrapper extends AbstractRoutinesFacadesProvider {
    private RoutinesFacadesProvider originalProvider;
    private ReactionExecutionState state;

    public RoutinesFacadesProviderWrapper(AbstractRoutinesFacadesProvider originalProvider,
            ReactionExecutionState state) {
        this.originalProvider = originalProvider;
        this.state = state;
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * Delegates to the wrapped {@link RoutinesFacadesProvider}
     * ({@link #originalProvider}). Meant to be used for accessing the
     * {@link RoutinesFacade}s in the current reactions code generation.
     */
    @Override
    protected AbstractRoutinesFacade createRoutinesFacade(ReactionsImportPath reactionsImportPath) {
        return originalProvider.getRoutinesFacade(reactionsImportPath);
    }

    /**
     * {@inheritDoc}
     * 
     * <p>
     * Delegates to the {@link ChangePropagationActionContainer} within the
     * {@link AbstractReactionsChangePropagationSpecification} inside
     * {@link #getExecutionState()}.
     */
    @SuppressWarnings("unchecked")
    @Override
    public AbstractRoutinesFacade getRoutinesFacade(String routineID) {
        return (AbstractRoutinesFacade) ((AbstractReactionsChangePropagationSpecification) this.state.changePropagationObservable())
                .getActionContainer().getRoutinesFacadeGenerator(routineID).apply(this.state);
    }
}
