package tools.vitruv.dsls.reactions.scanner.scanner;

import java.io.IOException;

import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarFile;

import tools.vitruv.change.propagation.ChangePropagationSpecification;
import tools.vitruv.dsls.reactions.runtime.reactions.Reaction;
import tools.vitruv.dsls.reactions.runtime.routines.Routine;
import tools.vitruv.dsls.reactions.runtime.routines.RoutinesFacade;
import tools.vitruv.dsls.reactions.runtime.routines.RoutinesFacadesProvider;

public class JarBasedChangePropagationActionScanner implements ChangePropagationActionScanner {
    private final static String CLASS_SUFFIX = ".class";
    private final static String INNER_CLASS_SEPARATOR = "$";

    private Path jarFilePath;

    private Set<Class<? extends ChangePropagationSpecification>> cpsClss = new LinkedHashSet<>();
    private Set<Class<? extends Reaction>> reactionClss = new LinkedHashSet<>();
    private Set<Class<? extends RoutinesFacadesProvider>> providerClss = new LinkedHashSet<>();
    private Set<Class<? extends RoutinesFacade>> facadeClss = new LinkedHashSet<>();
    private Set<Class<? extends Routine>> routineClss = new LinkedHashSet<>();

    public Path getJarFilePath() {
        return this.jarFilePath;
    }

    public void setJarFilePath(Path jarFilePath) {
        this.jarFilePath = jarFilePath;
    }

    @Override
    public void init() {
        var jarPath = getJarFilePath();
        try (JarFile jar = new JarFile(jarPath.toFile())) {
            var entryIt = jar.entries();

            // Filter out non-class files
            while (entryIt.hasMoreElements()) {
                var entry = entryIt.nextElement();
                String fullyQualifiedName = entry.getName();

                // Skip all entries that cannot be inside the "mir" folder
                if (!fullyQualifiedName.startsWith(MirFolderConstants.MIR))
                    continue;

                // Skip non-class files
                if (!fullyQualifiedName.endsWith(CLASS_SUFFIX))
                    continue;

                // Skip inner classes
                if (fullyQualifiedName.contains(INNER_CLASS_SEPARATOR))
                    continue;

                // Trim the ".class" extension
                fullyQualifiedName = fullyQualifiedName.substring(0,
                        fullyQualifiedName.length() - CLASS_SUFFIX.length());

                // Replace package separators with dots
                fullyQualifiedName = fullyQualifiedName.replaceAll("/", ".");

                if (fullyQualifiedName.endsWith(MirFolderConstants.ROUTINE_SUFFIX)) {
                    routineClss.add(loadClassForName(fullyQualifiedName));
                } else if (fullyQualifiedName.endsWith(MirFolderConstants.REACTION_SUFFIX)) {
                    reactionClss.add(loadClassForName(fullyQualifiedName));
                } else if (fullyQualifiedName.endsWith(MirFolderConstants.ROUTINE_FACADE_SUFFIX)) {
                    facadeClss.add(loadClassForName(fullyQualifiedName));
                } else if (fullyQualifiedName.endsWith(MirFolderConstants.ROUTINE_FACADE_PROVIDER_SUFFIX)) {
                    providerClss.add(loadClassForName(fullyQualifiedName));
                } else if (fullyQualifiedName.endsWith(MirFolderConstants.CPS_SUFFIX)) {
                    cpsClss.add(loadClassForName(fullyQualifiedName));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new IllegalStateException("Could not parse the JAR file at: " + jarPath.toString(), e);
        }
    }

    @SuppressWarnings("unchecked")
    private <T> Class<? extends T> loadClassForName(String clsName) {
        Class<? extends T> cls;
        try {
            // cls = Class.forName(clsName, false, classLoader);
            cls = (Class<? extends T>) Class.forName(clsName);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Could not get the class object for: " + clsName, e);
        }
        return cls;
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
