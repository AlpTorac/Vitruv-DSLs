package tools.vitruv.dsls.reactions.scanner.scanner;

public class JarBasedChangePropagationActionScanner {
/*
TODO: Try to implement JarFile variant of scanning and point it to the generated JAR file (which should contain the Reaction / Routine classes)

No dependency approach for Reaction / Routine scanning

try (JarFile jar = new JarFile(Path.of(jarFilePath))) {
    List<String> reactionClassNames = new ArrayList<>();
    for (JarEntry entry : Collections.list(jar.entries())) {
        String name = entry.getName();
        if (name.endsWith(".class") && name.startsWith("mir/reactions/")) {
            String className = name.substring(0, name.length() - ".class".length())
                                   .replace('/', '.');
            reactionClassNames.add(className);
        }
    }
}
*/
}
