package proyecto.core.menu.steps;

import java.util.Scanner;

/**
 * Utilidad para solicitar al usuario las rutas de archivos de entrada y salida
 * para el procesamiento de CSV.
 * <p>
 * Esta clase muestra mensajes en consola indicando una ruta por defecto y permite
 * que el usuario:
 * <ul>
 *     <li>Presione Enter para aceptar la ruta por defecto.</li>
 *     <li>Escriba una ruta personalizada para el archivo.</li>
 * </ul>
 * Se utiliza principalmente desde el flujo de menú para construir la configuración
 * de procesamiento de archivos.
 */
public final class FilePathPrompter {

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
     * Flujo:
     * <ol>
     *     <li>Muestra un mensaje indicando la posibilidad de usar una ruta por defecto.</li>
     *     <li>Lee una línea desde consola.</li>
     *     <li>Si el usuario presiona Enter sin escribir nada, se devuelve {@code defaultPath}.</li>
     *     <li>En caso contrario, se devuelve la ruta introducida por el usuario.</li>
     * </ol>
     *
     * @param scanner     instancia de {@link Scanner} utilizada para leer la entrada
     *                    del usuario desde la consola. No debe ser {@code null}.
     * @param defaultPath ruta por defecto que se utilizará si el usuario presiona Enter
     *                    sin escribir nada.
     * @return la ruta del archivo CSV de entrada seleccionada por el usuario o la ruta
     *         por defecto si no se introdujo ningún valor.
     */
    public static String askInputPath(Scanner scanner, String defaultPath) {
        System.out.println();
        System.out.println("Archivo CSV de entrada:");
        System.out.println("  (Si solo presiona Enter, se usará por defecto: " + defaultPath + ")");
        System.out.print("Ruta del archivo CSV: ");
        String line = scanner.nextLine().trim();
        if (line.isEmpty()) {
            line = defaultPath;
        }
        return line;
    }

    /**
     * Solicita al usuario la ruta del archivo CSV de salida.
     * <p>
     * Flujo:
     * <ol>
     *     <li>Muestra un mensaje indicando la posibilidad de usar una ruta por defecto.</li>
     *     <li>Lee una línea desde consola.</li>
     *     <li>Si el usuario presiona Enter sin escribir nada, se devuelve {@code defaultPath}.</li>
     *     <li>En caso contrario, se devuelve la ruta introducida por el usuario.</li>
     * </ol>
     *
     * @param scanner     instancia de {@link Scanner} utilizada para leer la entrada
     *                    del usuario desde la consola. No debe ser {@code null}.
     * @param defaultPath ruta por defecto que se utilizará si el usuario presiona Enter
     *                    sin escribir nada.
     * @return la ruta del archivo CSV de salida seleccionada por el usuario o la ruta
     *         por defecto si no se introdujo ningún valor.
     */
    public static String askOutputPath(Scanner scanner, String defaultPath) {
        System.out.println();
        System.out.println("Archivo CSV de salida:");
        System.out.println("  (Si solo presiona Enter, se usará por defecto: " + defaultPath + ")");
        System.out.print("Ruta del archivo de salida: ");
        String line = scanner.nextLine().trim();
        if (line.isEmpty()) {
            line = defaultPath;
        }
        return line;
    }
}
