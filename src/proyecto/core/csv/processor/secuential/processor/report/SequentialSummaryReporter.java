package proyecto.core.csv.processor.secuential.processor.report;

import proyecto.core.csv.processor.common.util.TimeLogger;
import proyecto.core.csv.processor.secuential.processor.lines.RowStats;

import java.io.File;

/**
 * Genera y muestra en consola un resumen del procesamiento
 * secuencial de un archivo CSV.
 * <p>
 * Esta clase:
 * <ul>
 *     <li>Imprime el número de líneas válidas y con error.</li>
 *     <li>Muestra la ruta del archivo de log de errores.</li>
 *     <li>Delegaa en {@link TimeLogger#printTime(String, File, File, long, long)}
 *         la impresión de información de tiempos y archivos involucrados.</li>
 * </ul>
 * Es una clase utilitaria y solo expone métodos estáticos.
 */
public final class SequentialSummaryReporter {

    /**
     * Constructor privado para evitar la creación de instancias.
     */
    private SequentialSummaryReporter() {
    }

    /**
     * Imprime un resumen del procesamiento secuencial realizado.
     * <p>
     * El resumen incluye:
     * <ul>
     *     <li>Cantidad de líneas válidas.</li>
     *     <li>Cantidad de líneas con error.</li>
     *     <li>Ruta absoluta del archivo de log de errores.</li>
     *     <li>Información de tiempo y archivos procesados mediante {@link TimeLogger}.</li>
     * </ul>
     *
     * @param inputFile  archivo CSV de entrada que fue procesado.
     * @param outputFile archivo CSV de salida generado por el procesamiento.
     * @param logFile    archivo donde se registraron los errores de líneas.
     * @param stats      estadísticas de filas procesadas, incluyendo líneas válidas
     *                   y líneas con error.
     * @param start      instante de inicio del procesamiento, en nanosegundos
     *                   (por ejemplo, tomado de {@link System#nanoTime()}).
     * @param end        instante de fin del procesamiento, en nanosegundos
     *                   (por ejemplo, tomado de {@link System#nanoTime()}).
     */
    public static void report(File inputFile,
                              File outputFile,
                              File logFile,
                              RowStats stats,
                              long start,
                              long end) {

        System.out.println("Líneas válidas: " + stats.getValidLines());
        System.out.println("Líneas con error: " + stats.getErrorLines());
        System.out.println("Log de errores: " + logFile.getAbsolutePath());

        TimeLogger.printTime("SECUENCIAL", inputFile, outputFile, start, end);
    }
}
