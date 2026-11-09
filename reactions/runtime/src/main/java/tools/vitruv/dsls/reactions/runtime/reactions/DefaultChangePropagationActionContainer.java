package tools.vitruv.dsls.reactions.runtime.reactions;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.eclipse.emf.ecore.EObject;

import tools.vitruv.change.atomic.EChange;
import tools.vitruv.dsls.reactions.runtime.controlflows.AbstractReactionsChangePropagationControlFlow;
import tools.vitruv.dsls.reactions.runtime.controlflows.ChangePropagationControlFlow;
import tools.vitruv.dsls.reactions.runtime.routines.RoutinesFacade;
import tools.vitruv.dsls.reactions.runtime.state.ReactionExecutionState;

public class DefaultChangePropagationActionContainer implements ReactionsChangePropagationActionContainer {
    private Map<String, AbstractReactionsChangePropagationControlFlow> controlFlows;
    private Map<String, Function<ReactionExecutionState, RoutinesFacade>> routineFacadeGenerators;
    private Map<String, AbstractReaction> reactions;

    public DefaultChangePropagationActionContainer() {
        this.controlFlows = new LinkedHashMap<>();
        this.routineFacadeGenerators = new LinkedHashMap<>();
        this.reactions = new LinkedHashMap<>();
    }

    @Override
    public void addReaction(String reactionID, AbstractReaction reaction) {
        this.reactions.putIfAbsent(reactionID, reaction);
    }

    @Override
    public AbstractReaction overrideExistingReaction(String reactionID, AbstractReaction newReaction) {
        return this.reactions.put(reactionID, newReaction);
    }

    @Override
    public AbstractReaction removeReaction(String reactionID) {
        return this.reactions.remove(reactionID);
    }

    @Override
    public AbstractReaction getReaction(String reactionID) {
        return this.reactions.getOrDefault(reactionID, null);
    }

    @Override
    public void clear() {
        this.reactions.clear();
        this.routineFacadeGenerators.clear();
        this.controlFlows.clear();
    }

    @Override
    public List<AbstractReaction> getAllReactions() {
        return List.copyOf(reactions.values());
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
    public Object performRoutine(ReactionExecutionState state, String facadeGenID, String routineID,
            Class<?>[] paramClss,
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
        return new LinkedHashMap<>(this.routineFacadeGenerators);
    }

    @Override
    public Map<String, AbstractReaction> getAllIdToReactionMappings() {
        return new LinkedHashMap<>(this.reactions);
    }

    @Override
    public Function<ReactionExecutionState, RoutinesFacade> removeRoutinesFacadesGenerator(String facadeID) {
        return this.routineFacadeGenerators.remove(facadeID);
    }

    @Override
    public void removeID(String id) {
        this.reactions.remove(id);
        this.routineFacadeGenerators.remove(id);
        this.controlFlows.remove(id);
    }

    @Override
    public void addControlFlow(String controlFlowID, AbstractReactionsChangePropagationControlFlow controlFlow) {
        this.controlFlows.putIfAbsent(controlFlowID, controlFlow);
    }

    @Override
    public AbstractReactionsChangePropagationControlFlow removeControlFlow(String controlFlowID) {
        return this.controlFlows.remove(controlFlowID);
    }

    @Override
    public List<AbstractReactionsChangePropagationControlFlow> getAllControlFlows() {
        return List.copyOf(this.controlFlows.values());
    }

    @Override
    public Map<String, AbstractReactionsChangePropagationControlFlow> getAllIdToControlFlowMappings() {
        return new LinkedHashMap<>(this.controlFlows);
    }

    @Override
    public boolean hasControlFlow(String controlFlowID) {
        return this.controlFlows.containsKey(controlFlowID);
    }

    @Override
    public Object performControlFlow(EChange<EObject> change, ReactionExecutionState state, String controlFlowID) {
        return this.controlFlows.get(controlFlowID).executeAction(change, state);
    }
}
