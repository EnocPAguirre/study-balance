package proyecto.core.menu.steps;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * Utilidad para solicitar al usuario las rutas de archivos de entrada y salida
 * para el procesamiento de CSV.
 * <p>
 * Reglas generales:
 * <ul>
 *     <li>Los archivos de ENTRADA se buscarán SIEMPRE dentro de la carpeta {@code data}.</li>
 *     <li>Los archivos de SALIDA se escribirán SIEMPRE dentro de la carpeta {@code output}.</li>
 *     <li>Solo se permiten archivos con extensión <b>.csv</b>.</li>
 *     <li>El usuario solo debe escribir el NOMBRE del archivo, sin carpetas.</li>
 * </ul>
 */
public final class FilePathPrompter {

    /**
     * Carpeta base donde deben residir todos los archivos CSV de ENTRADA.
     */
    private static final Path INPUT_DIR = Paths.get("data").toAbsolutePath().normalize();

    /**
     * Carpeta base donde se escribirán todos los archivos CSV de SALIDA.
     */
    private static final Path OUTPUT_DIR = Paths.get("output").toAbsolutePath().normalize();

    /**
     * Nombre de archivo por defecto cuando no se especifica ninguno.
     */
    private static final String DEFAULT_FILE_NAME = "students.csv";

    static {
        // Nos aseguramos de que las carpetas existan
        try {
            Files.createDirectories(INPUT_DIR);
            Files.createDirectories(OUTPUT_DIR);
        } catch (Exception e) {
            System.err.println("⚠ No se pudieron crear las carpetas base 'data' u 'output': " + e.getMessage());
        }
    }

    /**
     * Constructor privado para evitar la creación de instancias.
     * <p>
     * Esta clase solo expone métodos estáticos.
     */
    private FilePathPrompter() {
        // Evita la instanciación
    }

    /**
     * Solicita al usuario la ruta del archivo CSV de entrada.
     * <p>
     * Reglas:
     * <ul>
     *     <li>Si el usuario presiona Enter, se toma {@code defaultPath}
     *         (o {@code students.csv} si viene vacío).</li>
     *     <li>El usuario solo debe escribir un NOMBRE de archivo, sin carpetas.</li>
     *     <li>Internamente siempre se fuerza a la carpeta {@code data}.</li>
     *     <li>El archivo debe terminar en {@code .csv}; de lo contrario, se mostrará
     *         un mensaje de error y se volverá a pedir la entrada.</li>
     * </ul>
     *
     * @param scanner     instancia de {@link Scanner} utilizada para leer la entrada
     *                    del usuario desde la consola. No debe ser {@code null}.
     * @param defaultPath ruta por defecto que se utilizará si el usuario presiona Enter
     *                    sin escribir nada. Si es {@code null} o vacía, se usará {@code students.csv}.
     * @return la ruta ABSOLUTA del archivo CSV de entrada, siempre dentro de la carpeta {@code data}.
     */
    public static String askInputPath(Scanner scanner, String defaultPath) {
        if (defaultPath == null || defaultPath.trim().isEmpty()) {
            defaultPath = DEFAULT_FILE_NAME;
        }

        Path defaultPathInData = forceIntoInputDirectory(defaultPath);

        while (true) {
            System.out.println();
            System.out.println("Archivo CSV de entrada (.csv):");
            System.out.println("  Los archivos de ENTRADA se buscarán SIEMPRE dentro de la carpeta 'data'.");
            System.out.println("  Solo escriba el NOMBRE del archivo, sin carpetas.");
            System.out.println("  (Si solo presiona Enter, se usará por defecto: " + defaultPathInData.getFileName() + ")");
            System.out.print("Nombre del archivo CSV de entrada: ");

            String line = scanner.nextLine().trim();

            // No permitimos que el usuario meta carpetas
            if (!line.isEmpty() && (line.contains("/") || line.contains("\\"))) {
                System.out.println();
                System.out.println("⚠ Solo se permite el NOMBRE del archivo, sin rutas ni carpetas.");
                System.out.println("  Ejemplo: students.csv");
                continue;
            }

            Path finalPath;

            if (line.isEmpty()) {
                finalPath = defaultPathInData;
            } else {
                finalPath = forceIntoInputDirectory(line);
            }

            String fileName = finalPath.getFileName().toString();
            if (!fileName.toLowerCase().endsWith(".csv")) {
                System.out.println();
                System.out.println("⚠ Solo se permiten archivos con extensión .csv");
                System.out.println("  Ejemplo de nombre válido: students.csv");
                continue;
            }

            return finalPath.toString();
        }
    }

    /**
     * Solicita al usuario la ruta del archivo CSV de salida.
     * <p>
     * Reglas:
     * <ul>
     *     <li>Si el usuario presiona Enter, se toma {@code defaultPath}
     *         (o {@code students.csv} si viene vacío).</li>
     *     <li>El usuario solo debe escribir un NOMBRE de archivo, sin carpetas.</li>
     *     <li>Internamente siempre se fuerza a la carpeta {@code output}.</li>
     *     <li>El archivo debe terminar en {@code .csv}; de lo contrario, se mostrará
     *         un mensaje de error y se volverá a pedir la entrada.</li>
     * </ul>
     *
     * @param scanner     instancia de {@link Scanner} utilizada para leer la entrada
     *                    del usuario desde la consola. No debe ser {@code null}.
     * @param defaultPath ruta por defecto que se utilizará si el usuario presiona Enter
     *                    sin escribir nada. Si es {@code null} o vacía, se usará {@code students.csv}.
     * @return la ruta ABSOLUTA del archivo CSV de salida, siempre dentro de la carpeta {@code output}.
     */
    public static String askOutputPath(Scanner scanner, String defaultPath) {
        if (defaultPath == null || defaultPath.trim().isEmpty()) {
            defaultPath = DEFAULT_FILE_NAME;
        }

        Path defaultPathInOutput = forceIntoOutputDirectory(defaultPath);

        while (true) {
            System.out.println();
            System.out.println("Archivo CSV de salida (.csv):");
            System.out.println("  Los archivos de SALIDA se escribirán SIEMPRE dentro de la carpeta 'output'.");
            System.out.println("  Solo escriba el NOMBRE del archivo, sin carpetas.");
            System.out.println("  (Si solo presiona Enter, se usará por defecto: " + defaultPathInOutput.getFileName() + ")");
            System.out.print("Nombre del archivo CSV de salida: ");

            String line = scanner.nextLine().trim();

            // No permitimos que el usuario meta carpetas (caso perro/sarnoso.csv)
            if (!line.isEmpty() && (line.contains("/") || line.contains("\\"))) {
                System.out.println();
                System.out.println("⚠ Solo se permite el NOMBRE del archivo, sin rutas ni carpetas.");
                System.out.println("  Ejemplo: resultado.csv");
                continue;
            }

            Path finalPath;

            if (line.isEmpty()) {
                finalPath = defaultPathInOutput;
            } else {
                finalPath = forceIntoOutputDirectory(line);
            }

            String fileName = finalPath.getFileName().toString();
            if (!fileName.toLowerCase().endsWith(".csv")) {
                System.out.println();
                System.out.println("⚠ Solo se permiten archivos con extensión .csv");
                System.out.println("  Ejemplo de nombre válido: students.csv");
                continue;
            }

            return finalPath.toString();
        }
    }

    /**
     * Fuerza cualquier nombre de ENTRADA a ubicarse dentro de la carpeta {@code data},
     * tomando únicamente el nombre de archivo.
     */
    private static Path forceIntoInputDirectory(String rawPath) {
        Path original = Paths.get(rawPath);
        String fileName = original.getFileName().toString();
        return INPUT_DIR.resolve(fileName).toAbsolutePath().normalize();
    }

    /**
     * Fuerza cualquier nombre de SALIDA a ubicarse dentro de la carpeta {@code output},
     * tomando únicamente el nombre de archivo.
     */
    private static Path forceIntoOutputDirectory(String rawPath) {
        Path original = Paths.get(rawPath);
        String fileName = original.getFileName().toString();
        return OUTPUT_DIR.resolve(fileName).toAbsolutePath().normalize();
    }
}
