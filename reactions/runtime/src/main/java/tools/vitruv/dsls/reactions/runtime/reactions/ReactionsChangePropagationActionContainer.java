package tools.vitruv.dsls.reactions.runtime.reactions;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.eclipse.emf.ecore.EObject;

import tools.vitruv.change.atomic.EChange;
import tools.vitruv.dsls.reactions.runtime.controlflows.AbstractReactionsChangePropagationControlFlow;
import tools.vitruv.dsls.reactions.runtime.controlflows.ChangePropagationControlFlow;
import tools.vitruv.dsls.reactions.runtime.routines.RoutinesFacade;
import tools.vitruv.dsls.reactions.runtime.state.ReactionExecutionState;

public interface ReactionsChangePropagationActionContainer {
	public boolean hasReaction(String reactionID);

	public boolean hasRoutinesFacadesGenerator(String routineID);

	public Object performReaction(EChange<EObject> change, ReactionExecutionState state, String reactionID);

	public Object performRoutine(ReactionExecutionState state, String facadeGenID, String routineName,
			Class<?>[] paramClss,
			Object[] params);

	public default Object performRoutine(ReactionExecutionState state, String facadeGenID, String routineName) {
		return performRoutine(state, facadeGenID, routineName, null, null);
	}

	public default Object performRoutine(ReactionExecutionState state, String facadeGenID, String routineName,
			Object[] params) {
		return performRoutine(state, facadeGenID, routineName,
				List.of(params).stream().map(p -> p.getClass()).toArray(Class[]::new),
				params);
	}

	public void setRoutinesFacadesGenerator(String facadeGenID,
			Function<ReactionExecutionState, RoutinesFacade> routinesFacadeGenerator);

	public Function<ReactionExecutionState, RoutinesFacade> getRoutinesFacadeGenerator(String id);

	public Map<String, Function<ReactionExecutionState, RoutinesFacade>> getAllIdToRoutinesFacadesGeneratorMappings();

	public default void addReaction(AbstractReaction reaction) {
		this.addReaction(reaction.getID(), reaction);
	}

	public void addReaction(String reactionID, AbstractReaction reaction);

	public default void addControlFlow(AbstractReactionsChangePropagationControlFlow controlFlow) {
		this.addControlFlow(controlFlow.getID(), controlFlow);
	}
	public void addControlFlow(String controlFlowID, AbstractReactionsChangePropagationControlFlow controlFlow);

	public AbstractReaction removeReaction(String reactionID);

	public default AbstractReaction overrideExistingReaction(AbstractReaction newReaction) {
		return this.overrideExistingReaction(newReaction.getID(), newReaction);
	}

	public AbstractReaction overrideExistingReaction(String reactionID, AbstractReaction newReaction);

	public Function<ReactionExecutionState, RoutinesFacade> removeRoutinesFacadesGenerator(String facadeID);

	public ChangePropagationControlFlow removeControlFlow(String controlFlowID);

	public void removeID(String id);

	public AbstractReaction getReaction(String reactionID);
	
	public List<AbstractReaction> getAllReactions();
	
	public Map<String, AbstractReaction> getAllIdToReactionMappings();
	
	public AbstractReactionsChangePropagationControlFlow getControlFlow(String controlFlowID);
	public List<AbstractReactionsChangePropagationControlFlow> getAllControlFlows();
	public Map<String, AbstractReactionsChangePropagationControlFlow> getAllIdToControlFlowMappings();

	public void clear();

	public default void copyTo(ReactionsChangePropagationActionContainer otherContainer) {
		this.getAllIdToRoutinesFacadesGeneratorMappings()
				.forEach((id, gen) -> otherContainer.setRoutinesFacadesGenerator(id, gen));
		this.getAllIdToReactionMappings().forEach((id, reaction) -> otherContainer.addReaction(id, reaction));
		this.getAllIdToControlFlowMappings().forEach((id, cf) -> otherContainer.addControlFlow(id, cf));
	}
}
