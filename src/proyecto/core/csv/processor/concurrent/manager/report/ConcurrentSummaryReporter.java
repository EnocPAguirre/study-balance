package proyecto.core.csv.processor.concurrent.manager.report;

import proyecto.core.csv.processor.common.util.TimeLogger;

import java.io.File;

/**
 * Genera y muestra en consola un resumen del procesamiento
 * CONCURRENTE de un archivo CSV.
 * <p>
 * Esta clase es una utilidad de solo métodos estáticos que:
 * <ul>
 *     <li>Imprime información de tiempos mediante {@link TimeLogger}.</li>
 *     <li>Muestra la ruta del archivo de salida final.</li>
 *     <li>Muestra la ruta del archivo de log de errores.</li>
 *     <li>Muestra la ruta del directorio temporal donde se almacenan las partes procesadas.</li>
 * </ul>
 */
public final class ConcurrentSummaryReporter {

    /**
     * Constructor privado para evitar la creación de instancias.
     * <p>
     * La clase funciona únicamente como contenedor de métodos estáticos.
     */
    private ConcurrentSummaryReporter() {
    }

    /**
     * Imprime en consola un resumen del procesamiento concurrente realizado.
     * <p>
     * El resumen incluye:
     * <ul>
     *     <li>Información de tiempo y archivos procesados mediante
     *         {@link TimeLogger#printTime(String, File, File, long, long)}, usando
     *         la etiqueta {@code "CONCURRENTE"}.</li>
     *     <li>Ruta absoluta del archivo de salida final.</li>
     *     <li>Ruta absoluta del archivo de log de errores.</li>
     *     <li>Ruta absoluta del directorio temporal donde se encuentran las partes
     *         intermedias generadas durante el procesamiento concurrente.</li>
     * </ul>
     *
     * @param inputFile  archivo CSV de entrada que fue procesado.
     * @param outputFile archivo CSV de salida final generado por el procesamiento concurrente.
     * @param logFile    archivo donde se registraron los errores durante el procesamiento.
     * @param tempDir    directorio temporal donde se guardaron las partes parciales del CSV.
     * @param start      instante de inicio del procesamiento, en nanosegundos
     *                   (por ejemplo, valor devuelto por {@link System#nanoTime()}).
     * @param end        instante de fin del procesamiento, en nanosegundos
     *                   (por ejemplo, valor devuelto por {@link System#nanoTime()}).
     */
    public static void report(File inputFile,
                              File outputFile,
                              File logFile,
                              File tempDir,
                              long start,
                              long end) {

        TimeLogger.printTime("CONCURRENTE", inputFile, outputFile, start, end);

        System.out.println("Archivo de salida: " + outputFile.getAbsolutePath());
        System.out.println("Log de errores: " + logFile.getAbsolutePath());
        System.out.println("Partes temporales: " + tempDir.getAbsolutePath());
    }
}
