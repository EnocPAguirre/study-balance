package proyecto.core.csv.processor.concurrent.memory.util;

/**
 * Representa el resultado de procesar un lote (batch) de líneas en el
 * procesamiento concurrente de un archivo CSV.
 *
 * <p>
 * Esta clase es utilizada tanto por el gestor (Manager, {@code GestorConcurrente})
 * como por los trabajadores ({@code TrabajadorLote}) para intercambiar:
 * </p>
 *
 * <ul>
 *     <li>Texto de salida procesado (por ejemplo, filas filtradas o transformadas).</li>
 *     <li>Texto de log asociado al lote.</li>
 *     <li>Estadísticas básicas del lote (líneas procesadas y con error).</li>
 * </ul>
 *
 * <p>
 * Cada instancia corresponde a un lote identificado por su número de batch.
 * </p>
 *
 * <p>
 * La clase es inmutable y, por lo tanto, segura para utilizarse en contextos concurrentes.
 * </p>
 */
public final class BatchResult {

    /**
     * Número de lote (batch) procesado.
     * Sirve para identificar el orden o la posición del lote dentro del flujo.
     */
    private final int batchNumber;

    /**
     * Texto resultante del procesamiento del lote, normalmente contenido CSV
     * ya filtrado o transformado listo para escribirse en el archivo de salida.
     */
    private final String outputText;

    /**
     * Texto de log asociado al procesamiento del lote (mensajes de diagnóstico,
     * advertencias, errores descriptivos, etc.).
     */
    private final String logText;

    /**
     * Número total de líneas (filas del CSV) procesadas en este lote,
     * incluyendo las correctas y las que tuvieron error.
     */
    private final long processedLines;

    /**
     * Número de líneas (filas del CSV) dentro de este lote que presentaron
     * algún tipo de error durante el procesamiento (por ejemplo, formato inválido
     * o conversión fallida).
     */
    private final long errorLines;

    /**
     * Crea una nueva instancia que encapsula el resultado del procesamiento
     * de un lote de líneas.
     *
     * @param batchNumber    número identificador del lote.
     * @param outputText     texto de salida generado por el lote (contenido CSV listo
     *                       para escribirse). Puede ser {@code null} si no se generó salida.
     * @param logText        texto de log asociado al lote. Puede ser {@code null} si no hubo mensajes.
     * @param processedLines cantidad total de líneas procesadas en el lote.
     * @param errorLines     cantidad de líneas con error dentro del lote.
     */
    public BatchResult(int batchNumber,
                       String outputText,
                       String logText,
                       long processedLines,
                       long errorLines) {
        this.batchNumber = batchNumber;
        this.outputText = outputText;
        this.logText = logText;
        this.processedLines = processedLines;
        this.errorLines = errorLines;
    }

    /**
     * Devuelve el número identificador del lote procesado.
     *
     * @return número de batch.
     */
    public int getBatchNumber() {
        return batchNumber;
    }

    /**
     * Devuelve el texto de salida generado para este lote.
     *
     * @return texto de salida (por ejemplo, filas CSV filtradas o transformadas),
     *         o {@code null} si no se generó contenido.
     */
    public String getOutputText() {
        return outputText;
    }

    /**
     * Devuelve el texto de log asociado a este lote.
     *
     * @return texto de log (mensajes de diagnóstico, advertencias, etc.),
     *         o {@code null} si no se generaron logs.
     */
    public String getLogText() {
        return logText;
    }

    /**
     * Devuelve la cantidad total de líneas procesadas en este lote.
     *
     * @return número de líneas procesadas.
     */
    public long getProcessedLines() {
        return processedLines;
    }

    /**
     * Devuelve la cantidad de líneas que tuvieron error dentro de este lote.
     *
     * @return número de líneas con error.
     */
    public long getErrorLines() {
        return errorLines;
    }
}
