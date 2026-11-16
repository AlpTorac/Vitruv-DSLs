package tools.vitruv.dsls.reactions.runtime.routines;

import java.io.IOException;

import org.eclipse.xtext.xbase.lib.Exceptions;

import tools.vitruv.dsls.reactions.runtime.reactions.AbstractReactionsChangePropagationSpecification;
import tools.vitruv.dsls.reactions.runtime.state.ReactionExecutionState;
import tools.vitruv.dsls.reactions.runtime.structure.AbstractChangePropagationAction;
import tools.vitruv.dsls.reactions.runtime.structure.CallHierarchyHaving;
import tools.vitruv.dsls.reactions.runtime.structure.CallHistoryHaving;

public abstract class RoutineBase extends AbstractChangePropagationAction implements Routine {
    private ReactionExecutionState executionState;

    public RoutineBase(
            ReactionExecutionState executionState,
            CallHierarchyHaving calledBy) {
        super(calledBy);
        this.executionState = executionState;
    }

    public RoutineBase() {
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

    /**
     * @return The default ID of the calling Routine. By default, de-capitalises the
     *         first letter as per the current Vitruvius Routine naming convention.
     */
    @Override
    public String getDefaultID() {
        var clsName = this.getClass().getSimpleName();
        
        // This check is needed for potential anonymous Routine types
        if (clsName != null && clsName.length() > 1) {
            return clsName.substring(0, 1).toLowerCase() + clsName.substring(1);
        } else {
            return super.getDefaultID();
        }
    }

    @Override
    public boolean execute() {
        /*
         * Add this Routine as a called action in its caller. It has to be done here,
         * since the RoutinesFacades are currently generated without adding this as a
         * called action. Furthermore, the Routine calling methods in the current
         * RoutinesFacades cannot be overridden from outside in a reasonable way.
         */
        if (this.getCalledBy() instanceof CallHistoryHaving historyHavingCaller) {
            historyHavingCaller.addCalledAction(this);
        }

        var success = false;
        Throwable exc = null;
        try {
            success = executeRoutine();
        } catch (Exception e) {
            exc = e;
            throw Exceptions.sneakyThrow(e);
        } finally {
            finallyAfterExecution(success, exc);
        }
        return success;
    }

    /**
     * Performs any necessary actions after the execution of this Routine. This
     * method will be called in the "finally" block of a "try - catch - finally"
     * statement.
     * 
     * Does nothing by default, can be overridden in the implementors.
     * 
     * @param wasSuccessful Whether this Routine executed successfully
     * @param throwable     The Throwable that was thrown during the execution of
     *                      this Routine, if any
     */
    protected void finallyAfterExecution(boolean wasSuccessful, Throwable throwable) {
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
