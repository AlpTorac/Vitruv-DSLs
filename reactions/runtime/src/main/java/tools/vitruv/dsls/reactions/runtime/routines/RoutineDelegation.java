package tools.vitruv.dsls.reactions.runtime.routines;

import java.util.ArrayList;
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

    private RoutineBase routine;

    //
    //
    // viaX
    //
    //

    public RoutineDelegation viaFacade(RoutinesFacade facade) {
        this.facade = facade;
        this.caller = facade.getCurrentCaller();
        this.state = facade.getExecutionState();
        this.cps = (AbstractReactionsChangePropagationSpecification) facade.getExecutionState()
                .changePropagationObservable();
        return this;
    }

    public RoutineDelegation viaCPS(AbstractReactionsChangePropagationSpecification cps) {
        this.cps = cps;
        return this;
    }

    //
    //
    // withX
    //
    //

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

    public RoutineDelegation addParams(List<Object> params) {
        this.unnamedParamList.addAll(params);
        return this;
    }

    public RoutineDelegation addParams(Object... params) {
        for (var param : params)
            addParam(param);
        return this;
    }

    public RoutineDelegation addNamedParam(String paramName, Object paramVal) {
        this.namedParams.put(paramName, paramVal);
        return this;
    }

    //
    //
    // terminal operations
    //
    //

    public RoutineBase getRoutine() {
        initRoutineIfNull();
        setupRoutine(routine);
        return routine;
    }

    public Object execute() {
        var ro = getRoutine();
        return ro.execute();
    }

    public RoutineDelegation pause() {
        return this;
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
        if (ro instanceof ReactionsRoutineWrapper wrapperRo) {
            setupWrapperRoutine(wrapperRo);
        } else if (ro instanceof RoutineBase castedRo) {
            setupRoutineBase(castedRo);
        }
    }

    protected void setupRoutineBase(RoutineBase castedRo) {
        castedRo.setCallHierarchyHaving(caller);
        castedRo.setExecutionState(state);
        castedRo.clearUnnamedParams();
        castedRo.putAllUnnamedParams(unnamedParamList);
        castedRo.putAllParams(namedParams);
    }

    protected void setupWrapperRoutine(ReactionsRoutineWrapper wrapperRo) {
        if (this.facade instanceof AbstractRoutinesFacade castedFacade) {
            wrapperRo.setCallerRoutinesFacade(castedFacade);
        }
        wrapperRo.clearUnnamedParams();
        wrapperRo.putAllUnnamedParams(unnamedParamList);
        wrapperRo.putAllParams(namedParams);
        // wrapperRo.init();
    }
}
