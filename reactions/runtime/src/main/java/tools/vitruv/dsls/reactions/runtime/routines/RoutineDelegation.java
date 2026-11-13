package tools.vitruv.dsls.reactions.runtime.routines;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import tools.vitruv.dsls.reactions.runtime.reactions.AbstractReactionsChangePropagationSpecification;
import tools.vitruv.dsls.reactions.runtime.state.ReactionExecutionState;
import tools.vitruv.dsls.reactions.runtime.structure.CallHierarchyHaving;

public class RoutineDelegation {
    private AbstractReactionsChangePropagationSpecification cps;
    private CallHierarchyHaving caller;
    private RoutinesFacade facade;
    private ReactionExecutionState state;

    private String routineID;
    private final List<Object> unnamedParamList = new ArrayList<>();
    private final Map<String, Object> namedParams = new LinkedHashMap<>();

    private Routine routine;

    //
    //
    // viaX
    //
    //

    public RoutineDelegation viaFacade(RoutinesFacade facade) {
        resetVia();
        withFacade(facade);
        withCPS((AbstractReactionsChangePropagationSpecification) facade.getExecutionState()
                .changePropagationObservable());
        withCaller(facade.getCurrentCaller());
        withState(facade.getExecutionState());
        return this;
    }

    public RoutineDelegation viaCPS(AbstractReactionsChangePropagationSpecification cps) {
        resetVia();
        withCPS(cps);
        withState(cps.getState());
        return this;
    }

    //
    //
    // withX
    //
    //

    public RoutineDelegation withCPS(AbstractReactionsChangePropagationSpecification cps) {
        this.cps = cps;
        return this;
    }

    public RoutineDelegation withFacade(RoutinesFacade facade) {
        this.facade = facade;
        return this;
    }

    public RoutineDelegation withState(ReactionExecutionState state) {
        this.state = state;
        return this;
    }

    public RoutineDelegation withCaller(CallHierarchyHaving caller) {
        this.caller = caller;
        return this;
    }

    //
    //
    // to
    //
    //

    public RoutineDelegation to(String routineID) {
        this.routineID = routineID;
        return this;
    }

    //
    //
    // param operations
    //
    //

    public RoutineDelegation addParam(Object param) {
        this.unnamedParamList.add(param);
        return this;
    }

    /**
     * Adds the elements contained within the given Collection as one individual
     * parameter each.
     * 
     * {@code addParams(List.of(p1, p2, ..., pN))} is the shorthand for
     * {@code addParam(p1).addParam(p2)...addParam(pN)}.
     * 
     * Note: Use {@link #addParam(Object)} for passing a Collection-type parameter
     * p, if p should be added as a single, Collection-typed parameter; as opposed
     * to its contents being added as one parameter each.
     */
    public <C extends Collection<?>> RoutineDelegation addParams(C params) {
        this.unnamedParamList.addAll(params);
        return this;
    }

    /**
     * Adds each given parameter individually.
     * 
     * {@code addParams(p1, p2, ..., pN)} is the shorthand for
     * {@code addParam(p1).addParam(p2)...addParam(pN)}.
     * 
     * Note: Use {@link #addParam(Object)} for passing a Collection-type parameter
     * p, if p should be added as a single, Collection-typed parameter; as opposed
     * to its contents being added as one parameter each.
     */
    public RoutineDelegation addParams(Object... params) {
        for (var param : params)
            addParam(param);
        return this;
    }

    public RoutineDelegation setNamedParam(String paramName, Object paramVal) {
        this.namedParams.put(paramName, paramVal);
        return this;
    }

    //
    //
    // Reset operations
    //
    //

    public RoutineDelegation resetRoutine() {
        this.routine = null;
        return this;
    }

    //
    //
    // terminal operations
    //
    //

    @SuppressWarnings("unchecked")
    public <T extends Routine> T getRoutine() {
        initRoutineIfNull();
        setupRoutine(routine);
        return (T) routine;
    }

    public Object execute() {
        var ro = getRoutine();
        return ro.execute();
    }

    //
    //
    // Getters
    //
    //

    public AbstractReactionsChangePropagationSpecification getCps() {
        return cps;
    }

    public CallHierarchyHaving getCaller() {
        return caller;
    }

    public RoutinesFacade getFacade() {
        return facade;
    }

    public ReactionExecutionState getState() {
        return state;
    }

    public String getRoutineID() {
        return routineID;
    }

    public List<Object> getUnnamedParamList() {
        return List.copyOf(unnamedParamList);
    }

    public Map<String, Object> getNamedParams() {
        return Map.copyOf(namedParams);
    }

    @SuppressWarnings("unchecked")
    public <T extends Routine> T getCurrentRoutine() {
        if (routine == null)
            return null;
        return (T) routine;
    }

    //
    //
    // Internal operations
    //
    //

    protected void initRoutineIfNull() {
        if (this.routine == null) {
            this.routine = this.cps.getRoutine(routineID);
        }
    }

    protected void setupRoutine(Routine ro) {
        if (ro instanceof RoutineBase castedRo) {
            if (ro instanceof ReactionsRoutineWrapper wrapperRo
                    && this.facade instanceof AbstractRoutinesFacade castedFacade) {
                wrapperRo.setCallerRoutinesFacade(castedFacade);
            } else {
                castedRo.setCallHierarchyHaving(caller);
                castedRo.setExecutionState(state);
            }
            castedRo.clearUnnamedParams();
            castedRo.putAllUnnamedParams(unnamedParamList);
            castedRo.putAllParams(namedParams);
        }
    }

    protected void resetVia() {
        this.facade = null;
        this.cps = null;
    }
}
