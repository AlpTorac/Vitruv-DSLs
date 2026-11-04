package tools.vitruv.dsls.reactions.runtime.reactions;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.eclipse.emf.ecore.EObject;

import tools.vitruv.change.atomic.EChange;
import tools.vitruv.dsls.reactions.runtime.routines.RoutinesFacade;
import tools.vitruv.dsls.reactions.runtime.state.ReactionExecutionState;

public class DefaultReactionsContainer implements ReactionsContainer {
    private Map<String, Function<ReactionExecutionState, RoutinesFacade>> routineFacadeGenerators;
    private Map<String, Reaction> reactions;

    public DefaultReactionsContainer() {
        this.routineFacadeGenerators = new LinkedHashMap<>();
        this.reactions = new LinkedHashMap<>();
    }

    @Override
    public void addReaction(Reaction reaction) {
        this.addReaction(reaction.getDefaultID(), reaction);
    }

    @Override
    public void addReaction(String reactionID, Reaction reaction) {
        this.reactions.put(reactionID, reaction);
    }

    @Override
    public Reaction removeReaction(String reactionID) {
        return this.reactions.remove(reactionID);
    }

    @Override
    public Reaction getReaction(String reactionID) {
        return this.reactions.getOrDefault(reactionID, null);
    }

    @Override
    public void propagateChange(EChange<EObject> change, ReactionExecutionState state) {
        for (var reaction : reactions.values()) {
            reaction.execute(change, state);
        }
    }

    @Override
    public void clear() {
        this.reactions.clear();
    }

    @Override
    public List<Reaction> getAllReactions() {
        return List.copyOf(this.reactions.values());
    }

    @Override
    public void setRoutinesFacadesGenerator(String facadeGenID,
            Function<ReactionExecutionState, RoutinesFacade> routinesFacadeGenerator) {
        this.routineFacadeGenerators.put(facadeGenID, routinesFacadeGenerator);
    }

    @Override
    public Function<ReactionExecutionState, RoutinesFacade> getRoutinesFacadeGenerator(String facadeGenID) {
        return this.routineFacadeGenerators.getOrDefault(facadeGenID, null);
    }

    @Override
    public Object performReaction(EChange<EObject> change, ReactionExecutionState state, String reactionID) {
        return this.reactions.get(reactionID).executeAction(change, state);
    }

    @Override
    public Object performRoutine(ReactionExecutionState state, String facadeGenID, String routineID, Class<?>[] paramClss,
            Object[] params) {
        return this.routineFacadeGenerators.get(facadeGenID).apply(state).executeRoutine(routineID, paramClss, params);
    }

    @Override
    public boolean hasReaction(String reactionID) {
        return this.reactions.containsKey(reactionID);
    }

    @Override
    public boolean hasRoutinesFacadesGenerator(String routineID) {
        return this.routineFacadeGenerators.containsKey(routineID);
    }

    @Override
    public Map<String, Function<ReactionExecutionState, RoutinesFacade>> getAllIdToRoutinesFacadesGeneratorMappings() {
        return Map.copyOf(this.routineFacadeGenerators);
    }

    @Override
    public Map<String, Reaction> getAllIdToReactionMappings() {
        return Map.copyOf(this.reactions);
    }

    @Override
    public Function<ReactionExecutionState, RoutinesFacade> removeRoutinesFacadesGenerator(String facadeID) {
        return this.routineFacadeGenerators.remove(facadeID);
    }

    @Override
    public void removeID(String id) {
        this.reactions.remove(id);
        this.routineFacadeGenerators.remove(id);
    }
}
