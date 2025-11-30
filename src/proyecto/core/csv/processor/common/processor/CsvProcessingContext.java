package proyecto.core.csv.processor.common.processor;

import proyecto.core.csv.filters.CsvFilterCondition;
import proyecto.core.csv.filters.CsvRowFilter;

/**
 * <p>
 * Este {@code record} agrupa toda la información derivada de la cabecera y
 * la configuración de filtrado/selección de columnas, para que pueda ser
 * reutilizada por los distintos componentes (procesamiento secuencial,
 * concurrente, workers, etc.).
 * <p>
 * Contiene:
 * <ul>
 *     <li>{@code headerCols}: arreglo con los nombres de las columnas tal como aparecen en la cabecera.</li>
 *     <li>{@code totalColumns}: número total de columnas del CSV.</li>
 *     <li>{@code selectedIndexes}: índices de las columnas seleccionadas que se deben incluir en la salida.</li>
 *     <li>{@code filter}: filtro de filas compilado a partir de la expresión de filtro
 *         (puede ser {@code null} si no se aplica filtrado).</li>
 * </ul>
 *
 * @param headerCols     nombres de las columnas del CSV, en el orden en que aparecen en la cabecera.
 * @param totalColumns   número total de columnas del CSV.
 * @param selectedIndexes índices de las columnas que se deben incluir en el resultado final.
 * @param filter         filtro de filas a aplicar durante el procesamiento (puede ser {@code null}
 *                       si no se desea aplicar ningún filtro).
 */
public record CsvProcessingContext(
        String[] headerCols,
        int totalColumns,
        int[] selectedIndexes,
        CsvRowFilter filter
) {
}
