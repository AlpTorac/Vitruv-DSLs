package tools.vitruv.dsls.reactions.runtime.routines;

import java.io.IOException;

import org.eclipse.xtext.xbase.lib.Exceptions;

import tools.vitruv.dsls.reactions.runtime.reactions.AbstractReactionsChangePropagationSpecification;
import tools.vitruv.dsls.reactions.runtime.state.ReactionExecutionState;
import tools.vitruv.dsls.reactions.runtime.structure.AbstractReactionsChangePropagationAction;
import tools.vitruv.dsls.reactions.runtime.structure.CallHierarchyHaving;

public abstract class RoutineBase extends AbstractReactionsChangePropagationAction implements Routine {
    private RoutinesFacadeBase routinesFacade;
    private ReactionExecutionState executionState;

    public RoutineBase(
            RoutinesFacadeBase routinesFacade,
            ReactionExecutionState executionState,
            CallHierarchyHaving calledBy) {
        super(calledBy);
        this.routinesFacade = routinesFacade;
        this.executionState = executionState;
    }

    public RoutineBase() {
    }

    public void setRoutinesFacadeBase(RoutinesFacadeBase routinesFacade) {
        this.routinesFacade = routinesFacade;
    }

    public RoutinesFacadeBase getRoutinesFacadeBase() {
        return this.routinesFacade;
    }

    public ReactionExecutionState getExecutionState() {
        return this.executionState;
    }

    public void setExecutionState(ReactionExecutionState state) {
        this.executionState = state;
    }

    public AbstractReactionsChangePropagationSpecification getCPS() {
        return (AbstractReactionsChangePropagationSpecification) this.executionState.changePropagationObservable();
    }

    @Override
    public boolean execute() {
        routinesFacade.pushCaller(this);
        var success = false;

        try {
            success = executeRoutine();
        } catch (Exception e) {
            throw Exceptions.sneakyThrow(e);
        } finally {
            routinesFacade.dropLastCaller();
        }
        return success;
    }

    /**
     * Executes the routine and modifies the underlying models. May also persist the
     * modified models.
     *
     * @return true if executing the routine succeeds
     * @throws IOException should saving the modified models fail
     */
    protected abstract boolean executeRoutine() throws IOException;
}
