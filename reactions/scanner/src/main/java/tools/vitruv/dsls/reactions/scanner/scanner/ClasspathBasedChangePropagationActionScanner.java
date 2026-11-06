package tools.vitruv.dsls.reactions.scanner.scanner;

import java.util.HashSet;
import java.util.Set;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import tools.vitruv.change.propagation.ChangePropagationSpecification;
import tools.vitruv.dsls.reactions.codegen.helper.ClassNamesGenerators;
import tools.vitruv.dsls.reactions.runtime.reactions.Reaction;
import tools.vitruv.dsls.reactions.runtime.routines.Routine;
import tools.vitruv.dsls.reactions.runtime.routines.RoutinesFacade;
import tools.vitruv.dsls.reactions.runtime.routines.RoutinesFacadesProvider;

public class ClasspathBasedChangePropagationActionScanner implements ChangePropagationActionScanner {
    private ClassInfoList cpsInfos;
    private ClassInfoList reactionInfos;
    private ClassInfoList providerInfos;
    private ClassInfoList facadeInfos;
    private ClassInfoList routineInfos;

    private Set<Class<? extends ChangePropagationSpecification>> cpsClss;
    private Set<Class<? extends Reaction>> reactionClss;
    private Set<Class<? extends RoutinesFacadesProvider>> providerClss;
    private Set<Class<? extends RoutinesFacade>> facadeClss;
    private Set<Class<? extends Routine>> routineClss;

    @Override
    public void init() {
        scan();
        loadAllClasses();

        System.out.println("ran");
    }

    private void scan() {
        ClassNamesGenerators.getBasicMirPackageQualifiedName();
        try (ScanResult scanResult = new ClassGraph()
                .enableClasspath()
                .enableClassInfo()
                .scan()) {

            // ChangePropagationSpecifications
            cpsInfos = scanResult
                    .getAllClassesImplementing(ChangePropagationSpecification.class)
                    .filter(ci -> ci.getPackageName().startsWith(MirFolderConstants.MIR_REACTIONS)
                            && !ci.isAbstract() && !ci.isInterface());

            // Reactions
            reactionInfos = scanResult
                    .getAllClassesImplementing(Reaction.class)
                    .filter(ci -> ci.getPackageName().startsWith(MirFolderConstants.MIR_REACTIONS)
                            && !ci.isAbstract() && !ci.isInterface());

            // RoutinesFacadesProviders
            providerInfos = scanResult
                    .getAllClassesImplementing(RoutinesFacadesProvider.class)
                    .filter(ci -> ci.getPackageName().startsWith(MirFolderConstants.MIR_ROUTINES) && !ci.isAbstract()
                            && !ci.isInterface());

            // RoutinesFacades
            facadeInfos = scanResult
                    .getAllClassesImplementing(RoutinesFacade.class)
                    .filter(ci -> ci.getPackageName().startsWith(MirFolderConstants.MIR_ROUTINES) && !ci.isAbstract()
                            && !ci.isInterface());

            // Routines
            routineInfos = scanResult
                    .getAllClassesImplementing(Routine.class)
                    .filter(ci -> ci.getPackageName().startsWith(MirFolderConstants.MIR_ROUTINES)
                            && !ci.isAbstract() && !ci.isInterface());

        }
    }

    private void loadAllClasses() {
        cpsClss = loadClassesFor(cpsInfos);
        reactionClss = loadClassesFor(reactionInfos);
        providerClss = loadClassesFor(providerInfos);
        facadeClss = loadClassesFor(facadeInfos);
        routineClss = loadClassesFor(routineInfos);
    }

    @SuppressWarnings("unchecked")
    private <T> Set<Class<? extends T>> loadClassesFor(ClassInfoList info) {
        var names = info.getNames();
        var clsSet = new HashSet<Class<? extends T>>();
        for (var name : names) {
            Class<? extends T> cls;
            try {
                cls = (Class<? extends T>) Class.forName(name);
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException("Could not get the class object for: " + name, e);
            }
            clsSet.add(cls);
        }
        return clsSet;
    }

    @Override
    public Set<Class<? extends ChangePropagationSpecification>> getAllCPSClasses() {
        return this.cpsClss;
    }

    @Override
    public Set<Class<? extends Routine>> getAllRoutineClasses() {
        return this.routineClss;
    }

    @Override
    public Set<Class<? extends Reaction>> getAllReactionClasses() {
        return this.reactionClss;
    }

    @Override
    public Set<Class<? extends RoutinesFacade>> getAllRoutinesFacadeClasses() {
        return this.facadeClss;
    }

    @Override
    public Set<Class<? extends RoutinesFacadesProvider>> getAllRoutinesFacadesProviderClasses() {
        return this.providerClss;
    }
}
