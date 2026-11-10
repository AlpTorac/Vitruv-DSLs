package tools.vitruv.dsls.reactions.runtime.routines;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class DynamicRoutineFacade extends RoutinesFacadeBase {
    private Map<String, Supplier<AbstractRoutine>> ro;

    public DynamicRoutineFacade(Map<String, Supplier<AbstractRoutine>> ro) {
        this.ro = ro;
    }

    public DynamicRoutineFacade(DynamicRoutineFacade base, Map<String, Supplier<AbstractRoutine>> extensions) {
        this.ro = new LinkedHashMap<>();
        this.putAllRoutinesFrom(base, true);
        this.ro.putAll(extensions);
    }

    public Object executeRoutine(String routineID) {
        var routineGen = this.ro.get(routineID);
        var routine = routineGen.get();
        routine.setCallHierarchyHaving(this.getCurrentCaller());
        // routine.setRoutinesFacade(this);
        routine.setExecutionState(getExecutionState());
        System.out.println("Dynamic routine facade directed to: " + routine);
        return routine.execute();
    }

    public void putAllRoutinesFrom(DynamicRoutineFacade otherFacade, boolean overrideExisting) {
        if (overrideExisting) {
            this.ro.putAll(otherFacade.ro);
        } else {
            otherFacade.ro.entrySet().forEach(e -> this.ro.putIfAbsent(e.getKey(), e.getValue()));
        }
    }
}
