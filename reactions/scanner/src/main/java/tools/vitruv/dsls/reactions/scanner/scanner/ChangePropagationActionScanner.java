package tools.vitruv.dsls.reactions.scanner.scanner;

import java.util.Set;

import tools.vitruv.change.propagation.ChangePropagationSpecification;
import tools.vitruv.dsls.reactions.runtime.reactions.Reaction;
import tools.vitruv.dsls.reactions.runtime.routines.Routine;
import tools.vitruv.dsls.reactions.runtime.routines.RoutinesFacade;
import tools.vitruv.dsls.reactions.runtime.routines.RoutinesFacadesProvider;

public interface ChangePropagationActionScanner {
    public void init();

    public Set<Class<? extends ChangePropagationSpecification>> getAllCPSClasses();
    public Set<Class<? extends Routine>> getAllRoutineClasses();
    public Set<Class<? extends Reaction>> getAllReactionClasses();
    public Set<Class<? extends RoutinesFacade>> getAllRoutinesFacadeClasses();
    public Set<Class<? extends RoutinesFacadesProvider>> getAllRoutinesFacadesProviderClasses();
}