package proyecto.core.csv.processor.secuential.processor.lines;

/**
 * Representa estadísticas simples sobre el procesamiento de filas
 * en el modo secuencial.
 * <p>
 * Esta clase es un contenedor inmutable que almacena:
 * <ul>
 *     <li>El número de líneas válidas procesadas correctamente.</li>
 *     <li>El número de líneas que produjeron algún error.</li>
 * </ul>
 * Suele utilizarse para resumir el resultado de un procesamiento
 * de un bloque de filas o de todo un archivo.
 */
public class RowStats {

    /**
     * Número de líneas procesadas correctamente.
     */
    private final int validLines;

    /**
     * Número de líneas que generaron error durante el procesamiento.
     */
    private final int errorLines;

    /**
     * Crea un nuevo objeto de estadísticas de filas.
     *
     * @param validLines número de líneas válidas procesadas.
     * @param errorLines número de líneas con error.
     */
    public RowStats(int validLines, int errorLines) {
        this.validLines = validLines;
        this.errorLines = errorLines;
    }

    /**
     * Devuelve el número de líneas procesadas correctamente.
     *
     * @return cantidad de líneas válidas.
     */
    public int getValidLines() {
        return validLines;
    }

    /**
     * Devuelve el número de líneas que produjeron error.
     *
     * @return cantidad de líneas con error.
     */
    public int getErrorLines() {
        return errorLines;
    }
}
