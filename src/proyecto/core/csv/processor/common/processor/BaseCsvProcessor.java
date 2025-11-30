package proyecto.core.csv.processor.common.processor;

import proyecto.core.csv.filters.CsvRowFilter;
import proyecto.core.csv.filters.type.CsvFilterExpressionParser;
import proyecto.core.csv.filters.util.ColumnIndexResolver;
import proyecto.core.csv.processor.common.util.CsvUtils;

/**
 * Clase base para procesadores de archivos CSV.
 * <p>
 * Proporciona funcionalidad común a las implementaciones concretas
 * (secuencial y concurrente), como:
 * <ul>
 *     <li>Definir el separador por defecto de columnas.</li>
 *     <li>Construir el {@link CsvProcessingContext} a partir de la cabecera,
 *         la selección de columnas y la expresión de filtro.</li>
 * </ul>
 * Las clases derivadas (por ejemplo, {@code CsvSequentialProcessorImpl},
 * {@code CsvManager}, etc.) reutilizan esta lógica para evitar duplicación.
 */
public abstract class BaseCsvProcessor {

    /**
     * Separador de columnas utilizado en el CSV (por ejemplo, {@code ","}).
     */
    protected final String separator;

    /**
     * Crea un procesador base con el separador por defecto.
     * <p>
     * Actualmente el separador se fija a {@code ","}, pero este constructor
     * puede ampliarse en el futuro para soportar otros separadores.
     */
    protected BaseCsvProcessor() {
        this.separator = ","; // o lo que uses
    }

    /**
     * Construye el contexto de procesamiento a partir de la línea de cabecera.
     * <p>
     * Este método realiza los siguientes pasos:
     * <ol>
     *     <li>Divide la cabecera en columnas usando {@link CsvUtils#splitColumns(String, String)}.</li>
     *     <li>Calcula el número total de columnas.</li>
     *     <li>Resuelve la selección de columnas a índices usando
     *         {@link CsvUtils#parseColumnSelection(String, int)}.</li>
     *     <li>Crea un {@link ColumnIndexResolver} para mapear nombres de columna a índices.</li>
     *     <li>Parsea la expresión de filtro (si existe) a un {@link CsvRowFilter} usando
     *         {@link CsvFilterExpressionParser#parse(String, ColumnIndexResolver)}.</li>
     *     <li>Construye y devuelve un {@link CsvProcessingContext} con:
     *         <ul>
     *             <li>La cabecera como arreglo de cadenas.</li>
     *             <li>El número total de columnas.</li>
     *             <li>Los índices de columnas seleccionadas.</li>
     *             <li>El filtro compilado (o {@code null} si no hay filtro).</li>
     *         </ul>
     *     </li>
     * </ol>
     *
     * @param headerLine       línea de cabecera del CSV (normalmente la primera línea del archivo).
     * @param columnsSpec      especificación de columnas seleccionadas (por ejemplo,
     *                         {@code "*"} o {@code "2,7,13"}), interpretada por
     *                         {@link CsvUtils#parseColumnSelection(String, int)}.
     * @param filterExpression expresión textual del filtro sobre filas (por ejemplo,
     *                         {@code "GPA >= 9 AND Age >= 18"}), o {@code null}
     *                         si no se aplica filtro.
     * @return un {@link CsvProcessingContext} que encapsula cabecera, número total
     *         de columnas, índices seleccionados y filtro compilado.
     */
    protected CsvProcessingContext buildContext(String headerLine,
                                                String columnsSpec,
                                                String filterExpression) {

        String[] headerCols = CsvUtils.splitColumns(headerLine, separator);
        int totalColumns = headerCols.length;
        int[] selectedIndexes = CsvUtils.parseColumnSelection(columnsSpec, totalColumns);

        ColumnIndexResolver resolver = new ColumnIndexResolver(headerCols);

        CsvRowFilter filter = CsvFilterExpressionParser.parse(filterExpression, resolver);

        return new CsvProcessingContext(headerCols, totalColumns, selectedIndexes, filter);
    }
}
