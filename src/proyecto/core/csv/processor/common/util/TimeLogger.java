package proyecto.core.csv.processor.common.util;

import java.io.File;

/**
 * Utilidad para registrar el tiempo de procesamiento de archivos CSV.
 * <p>
 * Esta clase proporciona un método estático que calcula la duración de una
 * operación (a partir de marcas de tiempo en nanosegundos) y la muestra en
 * consola junto con información básica de los archivos involucrados.
 */
public final class TimeLogger {

    /**
     * Constructor privado para evitar la creación de instancias.
     * <p>
     * La clase funciona únicamente como contenedor de métodos estáticos.
     */
    private TimeLogger() {
        // Evitar instanciación
    }

    /**
     * Imprime en consola el tiempo de procesamiento asociado a una etiqueta y a un par
     * de archivos (entrada y salida).
     * <p>
     * El formato de salida es similar a:
     * <pre>
     * [SECUENCIAL] Entrada: input.csv | Salida: output.csv | Tiempo: 123.45 ms (0.12 s)
     * </pre>
     *
     * @param label      etiqueta que identifica el tipo de procesamiento
     *                   (por ejemplo, {@code "SECUENCIAL"} o {@code "CONCURRENTE"}).
     * @param inputFile  archivo de entrada que fue procesado.
     * @param outputFile archivo de salida generado.
     * @param start      instante de inicio del procesamiento, en nanosegundos
     *                   (normalmente obtenido con {@link System#nanoTime()}).
     * @param end        instante de fin del procesamiento, en nanosegundos
     *                   (normalmente obtenido con {@link System#nanoTime()}).
     */
    public static void printTime(String label, File inputFile, File outputFile, long start, long end) {
        long durationNs = end - start;
        double ms = durationNs / 1_000_000.0;
        double s = ms / 1000.0;
        System.out.println("[" + label + "] Entrada: " + inputFile.getName()
                + " | Salida: " + outputFile.getName()
                + " | Tiempo: " + String.format("%.2f ms (%.2f s)", ms, s));
    }
}
