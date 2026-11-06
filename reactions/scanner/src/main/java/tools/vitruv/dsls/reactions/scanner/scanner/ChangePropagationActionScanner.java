package tools.vitruv.dsls.reactions.scanner.scanner;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfoList;
import io.github.classgraph.ScanResult;
import tools.vitruv.dsls.reactions.codegen.helper.ClassNamesGenerators;
import tools.vitruv.dsls.reactions.runtime.reactions.AbstractReactionsChangePropagationSpecification;
import tools.vitruv.dsls.reactions.runtime.reactions.Reaction;
import tools.vitruv.dsls.reactions.runtime.routines.AbstractRoutinesFacade;
import tools.vitruv.dsls.reactions.runtime.routines.AbstractRoutinesFacadesProvider;
import tools.vitruv.dsls.reactions.runtime.routines.Routine;

public class ChangePropagationActionScanner {
    private final static String MIR = ClassNamesGenerators.getBasicMirPackageQualifiedName();
    private final static String MIR_REACTIONS = ClassNamesGenerators.getBasicReactionsPackageQualifiedName();
    private final static String MIR_ROUTINES = ClassNamesGenerators.getBasicRoutinesPackageQualifiedName();

    private ClassLoader myClassLoader = ClassLoader.getSystemClassLoader();

    private ClassInfoList cpsInfos;
    private ClassInfoList reactionInfos;
    private ClassInfoList providerInfos;
    private ClassInfoList facadeInfos;
    private ClassInfoList routineInfos;

    private Set<Class<?>> cpsClss;
    private Set<Class<?>> reactionClss;
    private Set<Class<?>> providerClss;
    private Set<Class<?>> facadeClss;
    private Set<Class<?>> routineClss;

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
                    .getAllSubclasses(AbstractReactionsChangePropagationSpecification.class)
                    .filter(ci -> ci.getPackageName().startsWith(MIR_REACTIONS)
                            && !ci.isAbstract() && !ci.isInterface());

            // Concrete Reactions: implement the Reaction interface, in mir.reactions.*
            reactionInfos = scanResult
                    .getAllClassesImplementing(Reaction.class)
                    .filter(ci -> ci.getPackageName().startsWith(MIR_REACTIONS)
                            && !ci.isAbstract() && !ci.isInterface());

            // RoutinesFacadesProviders
            providerInfos = scanResult
                    .getAllSubclasses(AbstractRoutinesFacadesProvider.class)
                    .filter(ci -> ci.getPackageName().startsWith(MIR_ROUTINES));

            // RoutinesFacades
            facadeInfos = scanResult
                    .getAllSubclasses(AbstractRoutinesFacade.class)
                    .filter(ci -> ci.getPackageName().startsWith(MIR_ROUTINES));

            // Routines
            routineInfos = scanResult
                    .getAllClassesImplementing(Routine.class)
                    .filter(ci -> ci.getPackageName().startsWith(MIR_ROUTINES)
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
    
    private Set<Class<?>> loadClassesFor(ClassInfoList info) {
        var names = info.getNames();
        var clsSet = new HashSet<Class<?>>();
        for (var name : names) {
            Class<?> cls;
            try {
                cls = Class.forName(name, false, myClassLoader);
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException("Could not get the class object for: " + name);
            }
            clsSet.add(cls);
        }
        return clsSet;
    }
}
