package tools.vitruv.dsls.reactions.scanner.scanner;

import tools.vitruv.change.propagation.ChangePropagationSpecification;
import tools.vitruv.dsls.reactions.codegen.helper.ClassNamesGenerators;
import tools.vitruv.dsls.reactions.runtime.reactions.Reaction;
import tools.vitruv.dsls.reactions.runtime.routines.Routine;
import tools.vitruv.dsls.reactions.runtime.routines.RoutinesFacade;
import tools.vitruv.dsls.reactions.runtime.routines.RoutinesFacadesProvider;

public final class MirFolderConstants {
    public final static String MIR = ClassNamesGenerators.getBasicMirPackageQualifiedName();
    public final static String MIR_REACTIONS = ClassNamesGenerators.getBasicReactionsPackageQualifiedName();
    public final static String MIR_ROUTINES = ClassNamesGenerators.getBasicRoutinesPackageQualifiedName();

    // Use class names, since these constants are not visible in ClassNamesGenerators
    public final static String CPS_SUFFIX = ChangePropagationSpecification.class.getSimpleName();
    public final static String REACTION_SUFFIX = Reaction.class.getSimpleName();
    public final static String ROUTINE_SUFFIX = Routine.class.getSimpleName();
    public final static String ROUTINE_FACADE_SUFFIX = RoutinesFacade.class.getSimpleName();
    public final static String ROUTINE_FACADE_PROVIDER_SUFFIX = RoutinesFacadesProvider.class.getSimpleName();
}
