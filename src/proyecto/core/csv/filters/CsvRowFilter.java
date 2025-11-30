package proyecto.core.csv.filters;

/**
 * Representa un filtro sobre una fila de un CSV.
 * <p>
 * Las implementaciones de esta interfaz definen una condición que se evalúa
 * sobre un arreglo de columnas (una fila del archivo CSV) para decidir si
 * dicha fila "pasa" el filtro o no.
 * <p>
 * Ejemplos de implementaciones:
 * <ul>
 *     <li>{@link CsvFilterCondition}: condición simple sobre una sola columna.</li>
 *     <li>{@link proyecto.core.csv.filters.type.CsvAndFilter}: combinación lógica AND de varios filtros.</li>
 *     <li>{@link proyecto.core.csv.filters.type.CsvOrFilter}: combinación lógica OR de varios filtros.</li>
 * </ul>
 */
public interface CsvRowFilter {

    /**
     * Evalúa si la fila indicada cumple el filtro.
     *
     * @param columns arreglo de valores que representan una fila del CSV,
     *                donde cada posición corresponde a una columna.
     * @return {@code true} si la fila cumple la condición definida por el filtro;
     *         {@code false} en caso contrario.
     */
    boolean matches(String[] columns);
}
