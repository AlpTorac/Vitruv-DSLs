package tools.vitruv.dsls.reactions.runtime.reactions;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.eclipse.emf.ecore.EObject;

import tools.vitruv.change.atomic.EChange;
import tools.vitruv.dsls.reactions.runtime.routines.RoutinesFacade;
import tools.vitruv.dsls.reactions.runtime.state.ReactionExecutionState;

public interface ReactionsContainer {
    public boolean hasReaction(String reactionID);

    public boolean hasRoutinesFacadesGenerator(String routineID);

    public Object performReaction(EChange<EObject> change, ReactionExecutionState state, String reactionID);

    public Object performRoutine(ReactionExecutionState state, String facadeGenID, String routineName, Class<?>[] paramClss,
            Object[] params);

    public default Object performRoutine(ReactionExecutionState state, String facadeGenID, String routineName, Object[] params) {
        return performRoutine(state, facadeGenID, routineName, List.of(params).stream().map(p -> p.getClass()).toArray(Class[]::new),
                params);
    }

    public void setRoutinesFacadesGenerator(String facadeGenID,
            Function<ReactionExecutionState, RoutinesFacade> routinesFacadeGenerator);

    public Function<ReactionExecutionState, RoutinesFacade> getRoutinesFacadeGenerator(String id);

    public Map<String, Function<ReactionExecutionState, RoutinesFacade>> getAllIdToRoutinesFacadesGeneratorMappings();

    public void addReaction(Reaction reaction);

    public void addReaction(String reactionID, Reaction reaction);

    public Reaction removeReaction(String reactionID);

    public Reaction getReaction(String reactionID);

    public List<Reaction> getAllReactions();

    public Map<String, Reaction> getAllIdToReactionMappings();

    public void propagateChange(EChange<EObject> change, ReactionExecutionState state);

    public void clear();

    public default void copyTo(ReactionsContainer otherContainer) {
        this.getAllIdToRoutinesFacadesGeneratorMappings().entrySet()
                .forEach(e -> otherContainer.setRoutinesFacadesGenerator(e.getKey(), e.getValue()));
        this.getAllIdToReactionMappings().entrySet().forEach(e -> otherContainer.addReaction(e.getKey(), e.getValue()));
    }
}
