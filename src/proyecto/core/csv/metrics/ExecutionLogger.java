package proyecto.core.csv.metrics;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utilidad para registrar métricas de ejecución en un archivo CSV.
 *
 * <p>
 * Esta clase escribe un historial de ejecuciones en el archivo
 * {@code metrics/historial-ejecuciones.csv}, incluyendo:
 * </p>
 * <ul>
 *     <li>Fecha y hora de la ejecución.</li>
 *     <li>Modo de ejecución (por ejemplo, secuencial o concurrente).</li>
 *     <li>Ruta del archivo de entrada.</li>
 *     <li>Ruta del archivo de salida.</li>
 *     <li>Tiempo de ejecución en milisegundos.</li>
 *     <li>Tiempo de ejecución en segundos.</li>
 * </ul>
 *
 * <p>
 * Es una clase de utilidad no instanciable; todo el acceso se realiza
 * mediante el método estático {@link #log(String, String, String, double)}.
 * </p>
 */
public final class ExecutionLogger {

    /** Nombre del directorio donde se almacenan las métricas. */
    private static final String DIR_METRICAS = "metrics";

    /** Nombre del archivo CSV de historial de ejecuciones. */
    private static final String NOMBRE_ARCHIVO = "historial-ejecuciones.csv";

    /** Formato de fecha y hora utilizado en el registro. */
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Constructor privado para evitar la instanciación.
     *
     * <p>
     * Esta clase está diseñada únicamente como utilidad estática.
     * </p>
     */
    private ExecutionLogger() {
        // Utilidad, no instanciable
    }

    /**
     * Registra una ejecución en el archivo CSV de historial.
     *
     * <p>
     * Si el archivo no existe, se crea junto con el directorio
     * {@value DIR_METRICAS} y se escribe una línea de encabezado.
     * A partir de entonces, cada llamada añade una nueva línea
     * con los datos de la ejecución.
     * </p>
     *
     * @param modo     cadena descriptiva del modo de ejecución,
     *                 por ejemplo {@code "[SECUENCIAL]"} o {@code "[CONCURRENTE]"}.
     * @param entrada  ruta del archivo de entrada procesado.
     * @param salida   ruta del archivo de salida generado.
     * @param tiempoMs tiempo de ejecución en milisegundos.
     */
    public static void log(String modo,
                           String entrada,
                           String salida,
                           double tiempoMs) {

        try {
            Path dir = Paths.get(DIR_METRICAS);
            Files.createDirectories(dir); // Crea la carpeta si no existe

            Path archivo = dir.resolve(NOMBRE_ARCHIVO);
            boolean existe = Files.exists(archivo);

            try (BufferedWriter writer = Files.newBufferedWriter(
                    archivo,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            )) {
                // Si es la primera vez, escribe encabezado
                if (!existe) {
                    writer.write("fecha_hora,modo,entrada,salida,tiempo_ms,tiempo_s");
                    writer.newLine();
                }

                String fecha = LocalDateTime.now().format(FORMATTER);
                double tiempoSeg = tiempoMs / 1000.0;

                String linea = String.format(
                        "%s,%s,%s,%s,%.2f,%.2f",
                        fecha,
                        modo,
                        entrada,
                        salida,
                        tiempoMs,
                        tiempoSeg
                );
                writer.write(linea);
                writer.newLine();
            }

        } catch (IOException e) {
            System.err.println("No se pudo escribir el historial de ejecuciones: " + e.getMessage());
        }
    }
}
