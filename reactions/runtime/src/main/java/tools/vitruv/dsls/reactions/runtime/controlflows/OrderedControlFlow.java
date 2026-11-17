package tools.vitruv.dsls.reactions.runtime.controlflows;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;

import tools.vitruv.change.atomic.EChange;
import tools.vitruv.dsls.reactions.runtime.reactions.AbstractReactionsChangePropagationSpecification;
import tools.vitruv.dsls.reactions.runtime.reactions.Reaction;
import tools.vitruv.dsls.reactions.runtime.state.ReactionExecutionState;

public class OrderedControlFlow extends ControlFlowBase {
    private final List<String> forcedReactionIDTriggerOrder = new ArrayList<>();

    public void executeReactionBefore(String reactionIDToExecuteBefore, String reactionIDToExecuteAfter) {
        if (forcedReactionIDTriggerOrder.contains(reactionIDToExecuteAfter)) {
            var idx = forcedReactionIDTriggerOrder.indexOf(reactionIDToExecuteAfter);
            forcedReactionIDTriggerOrder.add(idx, reactionIDToExecuteBefore);
        } else {
            forcedReactionIDTriggerOrder.add(reactionIDToExecuteBefore);
            forcedReactionIDTriggerOrder.add(reactionIDToExecuteAfter);
        }
    }

    @Override
    public void propagateChange(EChange<EObject> change, ReactionExecutionState state) {
        var reactionIDs = ((AbstractReactionsChangePropagationSpecification) state.changePropagationObservable())
                .getActionContainer()
                .getAllIdToGenMap().entrySet().stream()
                .filter(e -> e.getValue().isOfType(Reaction.class))
                .map(e -> e.getKey())
                .toList();

        var executionOrder = new ArrayList<String>();
        reactionIDs.stream().filter(id -> !forcedReactionIDTriggerOrder.contains(id)).forEachOrdered(executionOrder::add);
        executionOrder.addAll(forcedReactionIDTriggerOrder);
        // var waitingIDs = new ArrayList<>();
        // for (int i = 0, orderI = 0; i < reactionIDs.size(); i++) {
        //     var currentReactionID = reactionIDs.get(i);

        //     // Check if there is an order constraint on the currentReactionID
        //     var currentIDIdxInOrder = forcedReactionIDTriggerOrder.indexOf(currentReactionID);
        //     if (currentIDIdxInOrder > -1) {
        //         // There are order constraints on currentReactionID

        //         if (orderI == currentIDIdxInOrder) {
        //             // Current reactionID should be performed now
        //             executionOrder.add(currentReactionID);
        //             waitingIDs.remove(currentReactionID);
        //             orderI++;
        //         } else {
        //             waitingIDs.add(currentReactionID);
        //         }
        //     } else {
        //         // No order constraints on currentReactionID, just perform it
        //         executionOrder.add(currentReactionID);
        //     }
        // }

        for (var reactionID : executionOrder) {
            var reaction = ((AbstractReactionsChangePropagationSpecification) state.changePropagationObservable())
                .getActionContainer().getReaction(reactionID);
            reaction.setCallHierarchyHaving(this);
            reaction.execute(change, state);
            this.addCalledAction(reaction);
        }
    }
}
