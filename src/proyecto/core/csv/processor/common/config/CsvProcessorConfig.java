package proyecto.core.csv.processor.common.config;

/**
 * Configuración de alto nivel para procesar un archivo CSV.
 * <p>
 * Esta clase es un contenedor inmutable que agrupa todos los parámetros
 * necesarios para lanzar un procesamiento (secuencial o concurrente),
 * evitando tener que pasar múltiples argumentos sueltos.
 * <p>
 * Parámetros típicos:
 * <ul>
 *     <li>{@code inputPath}: ruta del archivo CSV de entrada.</li>
 *     <li>{@code outputPath}: ruta del archivo CSV de salida.</li>
 *     <li>{@code columnsSpec}: especificación de columnas a seleccionar
 *         (por ejemplo, {@code "*"} o {@code "2,7,13"}).</li>
 *     <li>{@code filterExpression}: expresión de filtro sobre filas
 *         (por ejemplo, {@code "GPA >= 9 AND Age >= 18"}), o {@code null}
 *         si no se aplica filtro.</li>
 *     <li>{@code parts}: número de partes/hilos para procesamiento concurrente;
 *         puede ser {@code null} si se quiere usar el número de CPUs disponibles.</li>
 * </ul>
 */
public final class CsvProcessorConfig {

    /**
     * Ruta del archivo CSV de entrada.
     */
    private final String inputPath;

    /**
     * Ruta del archivo CSV de salida.
     */
    private final String outputPath;

    /**
     * Especificación de columnas a seleccionar.
     * <p>
     * Puede tomar valores como:
     * <ul>
     *     <li>{@code "*"} para indicar todas las columnas.</li>
     *     <li>Una lista separada por comas con índices de columna
     *         (por ejemplo, {@code "2,7,13"}).</li>
     * </ul>
     */
    private final String columnsSpec;

    /**
     * Expresión de filtro sobre las filas, usando nombres de columna
     * y operadores lógicos/comparación (por ejemplo,
     * {@code "GPA >= 9 AND Age >= 18"}).
     * Puede ser {@code null} si no se aplicará ningún filtro.
     */
    private final String filterExpression;

    /**
     * Número de partes/hilos a usar para el procesamiento concurrente.
     * <p>
     * Si es {@code null} (o interpretado como no definido), el sistema
     * puede optar por usar el número de CPUs disponibles. No se utiliza
     * en el modo secuencial.
     */
    private final Integer parts;

    /**
     * Crea una nueva configuración de procesamiento de CSV.
     *
     * @param inputPath        ruta del archivo CSV de entrada.
     * @param outputPath       ruta del archivo CSV de salida.
     * @param columnsSpec      especificación de columnas a seleccionar
     *                         (por ejemplo, {@code "*"} o {@code "2,7,13"}).
     * @param filterExpression expresión de filtro sobre filas (por ejemplo,
     *                         {@code "GPA >= 9"}), o {@code null} si no se aplica filtro.
     * @param parts            número de partes/hilos para procesamiento concurrente;
     *                         puede ser {@code null} para usar una política por defecto.
     */
    public CsvProcessorConfig(String inputPath,
                              String outputPath,
                              String columnsSpec,
                              String filterExpression,
                              Integer parts) {
        this.inputPath = inputPath;
        this.outputPath = outputPath;
        this.columnsSpec = columnsSpec;
        this.filterExpression = filterExpression;
        this.parts = parts;
    }

    /**
     * Devuelve la ruta del archivo CSV de entrada.
     *
     * @return ruta del archivo de entrada.
     */
    public String getInputPath() {
        return inputPath;
    }

    /**
     * Devuelve la ruta del archivo CSV de salida.
     *
     * @return ruta del archivo de salida.
     */
    public String getOutputPath() {
        return outputPath;
    }

    /**
     * Devuelve la especificación de columnas a seleccionar.
     *
     * @return cadena que representa la selección de columnas
     *         (por ejemplo, {@code "*"} o {@code "2,7,13"}).
     */
    public String getColumnsSpec() {
        return columnsSpec;
    }

    /**
     * Devuelve la expresión de filtro sobre las filas.
     *
     * @return expresión de filtro (por ejemplo,
     *         {@code "GPA >= 9 AND Age >= 18"}), o {@code null}
     *         si no hay filtro definido.
     */
    public String getFilterExpression() {
        return filterExpression;
    }

    /**
     * Devuelve el número de partes/hilos configurado para el
     * procesamiento concurrente.
     *
     * @return número de partes, o {@code null} si se debe usar
     *         una política por defecto (por ejemplo, CPUs disponibles).
     */
    public Integer getParts() {
        return parts;
    }
}
