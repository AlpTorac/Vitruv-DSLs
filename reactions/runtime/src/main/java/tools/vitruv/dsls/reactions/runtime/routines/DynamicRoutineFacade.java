package tools.vitruv.dsls.reactions.runtime.routines;

import java.util.Map;
import java.util.function.Supplier;

public class DynamicRoutineFacade extends AbstractRoutinesFacade {
    private Map<String, Supplier<AbstractRoutine>> ro;

    public DynamicRoutineFacade(Map<String, Supplier<AbstractRoutine>> ro) {
        this.ro = ro;
    }

    public boolean executeRoutine(String routineID) {
        var routineGen = this.ro.get(routineID);
        var routine = routineGen.get();
        routine.setCallHierarchyHaving(this.getCurrentCaller());
        routine.setRoutinesFacade(this);
        routine.setReactionExecutionState(getExecutionState());
        System.out.println("Dynamic routine facade directed to: " + routine);
        return routine.execute();
    }
}
