package tools.vitruv.dsls.reactions.scanner.scanner;

import java.io.IOException;

import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.JarFile;

public class JarBasedChangePropagationActionScanner implements ChangePropagationActionScanner {
    private final static String CLASS_SUFFIX = ".class";
    private final static String INNER_CLASS_SEPARATOR = "$";

    // private ClassLoader classLoader = ClassLoader.getSystemClassLoader();
    private Path jarFilePath;

    private Set<Class<?>> cpsClss = new LinkedHashSet<>();
    private Set<Class<?>> reactionClss = new LinkedHashSet<>();
    private Set<Class<?>> providerClss = new LinkedHashSet<>();
    private Set<Class<?>> facadeClss = new LinkedHashSet<>();
    private Set<Class<?>> routineClss = new LinkedHashSet<>();

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

    private Class<?> loadClassForName(String clsName) {
        Class<?> cls;
        try {
            // cls = Class.forName(clsName, false, classLoader);
            cls = Class.forName(clsName);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Could not get the class object for: " + clsName, e);
        }
        return cls;
    }
}
