package tools.vitruv.dsls.reactions.runtime.reactions;

import java.util.List;

import tools.vitruv.dsls.reactions.runtime.controlflows.ControlFlowBase;
import tools.vitruv.dsls.reactions.runtime.routines.RoutineBase;
import tools.vitruv.dsls.reactions.runtime.structure.AbstractChangePropagationAction;

public interface ReactionsChangePropagationActionContainer extends ChangePropagationActionContainer {
	public default boolean addAbsentActionGen(String actionIdOverride, AbstractChangePropagationAction action) {
		return this.addAbsentActionGen(actionIdOverride, new ReactionsChangePropagationActionGenerator(action));
	}

	public default ChangePropagationActionGenerator overrideActionGen(String actionIdOverride,
			AbstractChangePropagationAction action) {
		return this.overrideActionGen(actionIdOverride, new ReactionsChangePropagationActionGenerator(action));
	}

	public default boolean addAbsentActionGen(AbstractChangePropagationAction action) {
		return this.addAbsentActionGen(action.getID(), new ReactionsChangePropagationActionGenerator(action));
	}

	public default ChangePropagationActionGenerator overrideActionGen(AbstractChangePropagationAction action) {
		return this.overrideActionGen(action.getID(), new ReactionsChangePropagationActionGenerator(action));
	}

	public default boolean addAbsentActionGen(ReactionsChangePropagationActionGenerator actionGen) {
		return this.addAbsentActionGen(actionGen.getDefaultActionID(), actionGen);
	}

	public default ChangePropagationActionGenerator overrideActionGen(
			ReactionsChangePropagationActionGenerator actionGen) {
		return this.overrideActionGen(actionGen.getDefaultActionID(), actionGen);
	}

	/**
	 * Note that this method has to return the ReactionBase instances in the
	 * insertion-order of their action generators. It is important, since the base
	 * version of Vitruvius always executes Reactions in the order they were added.
	 * 
	 * @return All ReactionBases that the added action generators generate, in the
	 *         order the action generators were inserted
	 */
	public default List<ReactionBase> getAllReactions() {
		return this.getAllIdToGenMap().values().stream().filter(gen -> gen.isOfType(ReactionBase.class))
				.map(gen -> (ReactionBase) gen.generateActionInstance()).toList();
	}

	public default List<RoutineBase> getAllRoutines() {
		return this.getAllIdToGenMap().values().stream().filter(gen -> gen.isOfType(RoutineBase.class))
				.map(gen -> (RoutineBase) gen.generateActionInstance()).toList();
	}

	public default List<ControlFlowBase> getAllControlFlows() {
		return this.getAllIdToGenMap().values().stream().filter(gen -> gen.isOfType(ControlFlowBase.class))
				.map(gen -> (ControlFlowBase) gen.generateActionInstance()).toList();
	}

	public default ReactionBase getReaction(String reactionID) {
		return (ReactionBase) getAction(reactionID);
	}

	public default RoutineBase getRoutine(String routineID) {
		return (RoutineBase) getAction(routineID);
	}

	public default ControlFlowBase getControlFlow(String controlFlowID) {
		return (ControlFlowBase) getAction(controlFlowID);
	}
}
