package proyecto.core.csv.processor.common.util;

import java.io.File;

/**
 * Utilidad para generar y mostrar en consola un resumen del procesamiento
 * de un archivo CSV (ya sea secuencial, concurrente u otra variante).
 *
 * <p>
 * Esta clase ofrece un único método estático {@link #report(String, File, File, File, long, long)}
 * que:
 * </p>
 * <ul>
 *     <li>Imprime información de tiempos e información básica de archivos
 *         mediante {@link TimeLogger}.</li>
 *     <li>Muestra la ruta del archivo de salida final.</li>
 *     <li>Muestra la ruta del archivo de log de errores.</li>
 * </ul>
 *
 * <p>
 * Es una clase de solo utilidades y no debe instanciarse.
 * </p>
 */
public final class SummaryReporter {

    /**
     * Constructor privado para evitar la creación de instancias.
     *
     * <p>
     * La clase funciona únicamente como contenedor de métodos estáticos.
     * </p>
     */
    private SummaryReporter() {
        // No instanciable
    }

    /**
     * Imprime en consola un resumen del procesamiento realizado.
     *
     * <p>
     * El resumen incluye:
     * </p>
     * <ul>
     *     <li>Información de tiempo y archivos procesados mediante
     *         {@link TimeLogger#printTime(String, File, File, long, long)}, usando
     *         la etiqueta proporcionada en {@code label}.</li>
     *     <li>Ruta absoluta del archivo de salida final.</li>
     *     <li>Ruta absoluta del archivo de log de errores.</li>
     * </ul>
     *
     * @param label      etiqueta descriptiva del modo de procesamiento
     *                   (por ejemplo, {@code "SECUENCIAL"} o {@code "CONCURRENTE EN MEMORIA"}).
     * @param inputFile  archivo CSV de entrada que fue procesado.
     * @param outputFile archivo CSV de salida final generado por el procesamiento.
     * @param logFile    archivo donde se registraron los errores durante el procesamiento.
     * @param start      instante de inicio del procesamiento, en nanosegundos
     *                   (por ejemplo, valor devuelto por {@link System#nanoTime()}).
     * @param end        instante de fin del procesamiento, en nanosegundos
     *                   (por ejemplo, valor devuelto por {@link System#nanoTime()}).
     */
    public static void report(
            String label,
            File inputFile,
            File outputFile,
            File logFile,
            long start,
            long end) {

        TimeLogger.printTime(label, inputFile, outputFile, start, end);

        System.out.println("Archivo de salida: " + outputFile.getAbsolutePath());
        System.out.println("Log de errores: " + logFile.getAbsolutePath());
    }
}
