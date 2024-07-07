package info.kgeorgiy.ja.gavriliuk.implementor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.jar.Attributes;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import info.kgeorgiy.java.advanced.implementor.ImplerException;
import info.kgeorgiy.java.advanced.implementor.JarImpler;

/**
 * Implementation of {@link JarImpler} interface
 *
 * @author Vitaliy Gavrilyuk (gmail@bestimplementator.ru)
 */
public class Implementor implements JarImpler {
    /**
     * Suffix appended to the name of the implemented class.
     */
    private static final String IMPL_SUFFIX = "Impl";

    /**
     * The default constructor for the Implementer class.
     */
    public Implementor() {
    }

    /**
     * Entry point for running the Implementor either in implementation mode or in JAR generation mode.
     * <p>
     * This method parses the command line arguments and delegates the implementation or JAR generation tasks
     * accordingly. If JAR generation mode is selected, the generated JAR file will contain the compiled
     * implementation of the specified interface.
     * <p>
     * Usage: java Implementor [-jar] &lt;class name&gt; &lt;target path&gt;
     *
     * @param args command line arguments:
     *             <ul>
     *             <li>[-jar] - optional flag indicating JAR generation mode;</li>
     *             <li>&lt;class name&gt; - fully-qualified name of the class or interface to implement;</li>
     *             <li>&lt;target path&gt; - target directory for implementation or JAR file.</li>
     *             </ul>
     */
    public static void main(String[] args) {
        if (!checkArgs(args)) {
            System.err.printf("Usage: java %s [-jar] <class name> <target path>", Implementor.class.getSimpleName());
            System.err.println();
            return;
        }

        try {
            Class<?> token = Class.forName(args[0]);
            Path root = Paths.get("");
            if (args.length == 3) {
                new Implementor().implementJar(token, root);
            } else {
                new Implementor().implement(token, root);
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Class not found: " + args[0]);
        } catch (ImplerException e) {
            System.err.println("Error generating implementation: " + e.getMessage());
        }
    }

    // :NOTE: do not override docs

    /**
     * {@inheritDoc}
     */
    @Override
    public void implement(Class<?> token, Path root) throws ImplerException {
        if (!token.isInterface()) {
            throw new ImplerException("Only interfaces are supported.");
        }

        if (isClassPrivate(token)) {
            throw new ImplerException("Interface must be not private.");
        }

        Path packagePath = root.resolve(token.getPackageName().replace(".", File.separator));
        Path filePath = packagePath.resolve(token.getSimpleName() + IMPL_SUFFIX + ".java");

        try {
            Files.createDirectories(packagePath);
            generateImplementation(token, filePath);
        } catch (IOException e) {
            throw new ImplerException("Error writing to file: " + filePath, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void implementJar(Class<?> token, Path jarFile) throws ImplerException {
        Path tempDirectory;
        try {
            tempDirectory = prepareTemporaryDirectory(jarFile.getParent());
        } catch (IOException e) {
            throw new ImplerException("Error creating temporary directory.", e);
        }

        try {
            generateSourceCode(token, tempDirectory);
            compileSourceCode(token, tempDirectory);
            buildJarFile(token, jarFile, tempDirectory);
        } finally {
            cleanUpTemporaryDirectory(tempDirectory);
        }
    }

    /**
     * Prepares a temporary directory for generating the implementation source code.
     *
     * @param jarFilePath the path to the JAR file for which the directory is being prepared.
     * @return the path to the created temporary directory.
     * @throws IOException if an I/O error occurs while creating the directory.
     */
    private Path prepareTemporaryDirectory(Path jarFilePath) throws IOException {
        Path parentDir = Optional.ofNullable(jarFilePath.toAbsolutePath().getParent())
                .orElseThrow(() -> new IOException("Jar file parent directory is null."));
        return Files.createTempDirectory(parentDir, "impl-temp");
    }

    /**
     * Generates the source code for the specified interface into the given directory.
     *
     * @param token     the class token of the interface to implement.
     * @param outputDir the directory where the source code should be generated.
     * @throws ImplerException if an error occurs during source code generation.
     */
    private void generateSourceCode(Class<?> token, Path outputDir) throws ImplerException {
        implement(token, outputDir);
    }

    /**
     * Compiles the generated source code of the specified interface.
     *
     * @param token     the class token of the interface whose source code was generated.
     * @param sourceDir the directory containing the generated source code.
     * @throws ImplerException if a compilation error occurs.
     */
    private void compileSourceCode(Class<?> token, Path sourceDir) throws ImplerException {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new ImplerException("Java compiler is not available.");
        }

        String classpath;
        try {
            classpath = Path.of(token.getProtectionDomain().getCodeSource().getLocation().toURI()).toString();
        } catch (URISyntaxException e) {
            throw new ImplerException("Error obtaining classpath.", e);
        }

        Path sourceFile = sourceDir.resolve(token.getPackageName().replace('.', File.separatorChar))
                .resolve(token.getSimpleName() + IMPL_SUFFIX + ".java");

        int compilationResult = compiler.run(null, null, null, "-cp", classpath, sourceFile.toString());
        if (compilationResult != 0) {
            throw new ImplerException("Compilation failed.");
        }
    }

    /**
     * Builds a JAR file containing the compiled implementation of the specified interface.
     *
     * @param token       the class token of the interface to implement.
     * @param jarFile     the path to the JAR file to be created.
     * @param compiledDir the directory containing the compiled classes.
     * @throws ImplerException if an error occurs during JAR file creation.
     */
    private void buildJarFile(Class<?> token, Path jarFile, Path compiledDir) throws ImplerException {
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");

        // :NOTE: jstyle
        String entryPath = token.getPackageName().replace(
                '.', '/') + "/" + token.getSimpleName() + IMPL_SUFFIX + ".class";
        Path compiledClassFile = compiledDir.resolve(entryPath);

        try (JarOutputStream jos = new JarOutputStream(Files.newOutputStream(jarFile), manifest)) {
            jos.putNextEntry(new ZipEntry(entryPath));
            Files.copy(compiledClassFile, jos);
        } catch (IOException e) {
            throw new ImplerException("Error writing JAR file.", e);
        }
    }

    /**
     * Cleans up the temporary directory used for generating the implementation.
     *
     * @param tempDir the temporary directory to clean up.
     */
    private void cleanUpTemporaryDirectory(Path tempDir) {
        try {
            Files.walk(tempDir)
                    .sorted(Comparator.reverseOrder())
                    .forEach(path -> {
                        try {
                            Files.delete(path);
                        } catch (IOException e) {
                            System.err.println("Failed to delete temporary file or directory: " + path);
                        }
                    });
        } catch (IOException e) {
            System.err.println("Error cleaning up temporary directory: " + tempDir);
        }
    }

    /**
     * Checks if the provided arguments are valid for the operation.
     *
     * @param args the command line arguments to check.
     * @return {@code true} if the arguments are valid, {@code false} otherwise.
     */
    private static boolean checkArgs(final String[] args) {
        if (args == null) {
            return false;
        }
        if (args.length != 2 && args.length != 3) {
            return false;
        }
        if (Arrays.stream(args).anyMatch(Objects::isNull)) {
            return false;
        }

        return args.length != 3 || "-jar".equals(args[0]);
    }

    /**
     * Determines if the specified class token represents a private class.
     *
     * @param token the class token to check.
     * @return {@code true} if the class is private, {@code false} otherwise.
     */
    private static boolean isClassPrivate(Class<?> token) {
        return (token.getModifiers() & Modifier.PRIVATE) == Modifier.PRIVATE;
    }

    /**
     * Generates an implementation class for the specified interface and writes it to a file.
     *
     * @param token    the class token of the interface to implement.
     * @param filePath the path to the file where the implementation should be written.
     * @throws IOException if an I/O error occurs while writing to the file.
     */
    private void generateImplementation(Class<?> token, Path filePath) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            writer.write("package " + convertToUnicode(token.getPackageName()) + ";" + System.lineSeparator()
                         + System.lineSeparator());

            String classHeader = "public class " + convertToUnicode(token.getSimpleName()) + "Impl implements "
                                 + convertToUnicode(token.getCanonicalName()) + " {" + System.lineSeparator();
            writer.write(convertToUnicode(classHeader));

            for (Method method : token.getMethods()) {
                if (Modifier.isAbstract(method.getModifiers())) {
                    String methodImplementation = generateMethodImplementation(method);
                    writer.write(convertToUnicode(methodImplementation));
                }
            }
            writer.write("}" + System.lineSeparator());
        }
    }

    /**
     * Generates the implementation for a given method.
     *
     * @param method the method for which the implementation is to be generated.
     * @return a string containing the source code of the method's implementation.
     */
    private String generateMethodImplementation(Method method) {
        StringBuilder builder = new StringBuilder();
        builder.append("    @Override")
                .append(System.lineSeparator());
        String returnType = method.getReturnType().getCanonicalName();
        builder.append("    public ")
                .append(returnType)
                .append(" ")
                .append(method.getName())
                .append("(");

        Class<?>[] parameters = method.getParameterTypes();
        StringJoiner joiner = new StringJoiner(", ");
        for (int i = 0; i < parameters.length; i++) {
            joiner.add(parameters[i].getCanonicalName() + " param" + i);
        }
        String joined = joiner.toString();
        builder.append(joined);

        builder.append(") {").append(System.lineSeparator());
        if (!method.getReturnType().equals(Void.TYPE)) {
            builder.append("        return ")
                    .append(getDefaultValue(method.getReturnType()))
                    .append(";").append(System.lineSeparator());
        }
        builder.append("    }").append(System.lineSeparator()).append(System.lineSeparator());
        return builder.toString();
    }

    /**
     * Converts a string to Unicode by replacing characters with codes greater than 127 with Unicode escape sequences.
     *
     * @param input the input string to convert
     * @return the string with converted Unicode characters
     */
    private String convertToUnicode(final String input) {
        final StringBuilder res = new StringBuilder();
        for (final char c : input.toCharArray()) {
            if (c >= 128) {
                res.append(String.format("\\u%04X", (int) c));
            } else {
                res.append(c);
            }
        }
        return res.toString();
    }

    /**
     * Returns the default value for the given return type.
     *
     * @param returnType the return type for which the default value is needed.
     * @return a string representation of the default value for the given type.
     */
    private String getDefaultValue(Class<?> returnType) {
        if (!returnType.isPrimitive()) {
            return "null";
        } else if (returnType.equals(boolean.class)) {
            return "false";
        } else {
            return "0";
        }
    }
}
