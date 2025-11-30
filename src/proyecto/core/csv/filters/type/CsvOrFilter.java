package proyecto.core.csv.filters.type;

import proyecto.core.csv.filters.CsvRowFilter;

import java.util.List;

/**
 * Implementación de {@link CsvRowFilter} que combina varios filtros
 * mediante una operación lógica OR.
 * <p>
 * Una fila (arreglo de columnas) se considera que cumple el filtro
 * si <strong>al menos uno</strong> de los filtros internos lo cumple.
 *
 * Ejemplo de uso:
 * <pre>
 * CsvRowFilter f1 = ...; // filtro por columna GPA
 * CsvRowFilter f2 = ...; // filtro por columna Age
 * CsvRowFilter orFilter = new CsvOrFilter(List.of(f1, f2));
 * // matches(columns) será true si f1.matches(columns) o f2.matches(columns) es true
 * </pre>
 */
public final class CsvOrFilter implements CsvRowFilter {

    /**
     * Lista de filtros entre los que basta que uno se cumpla (operación lógica OR).
     */
    private final List<CsvRowFilter> filters;

    /**
     * Crea un filtro compuesto que aplica OR sobre la lista de filtros dada.
     *
     * @param filters lista de filtros a combinar. Se asume que no es {@code null};
     *                si la lista está vacía, el comportamiento de {@link #matches(String[])}
     *                será devolver siempre {@code false}.
     */
    public CsvOrFilter(List<CsvRowFilter> filters) {
        this.filters = filters;
    }

    /**
     * Evalúa si la fila (arreglo de columnas) cumple ALGUNO de los filtros internos.
     * <p>
     * Recorre cada filtro de la lista y llama a {@link CsvRowFilter#matches(String[])}:
     * si alguno devuelve {@code true}, el resultado global es {@code true}.
     * Si todos devuelven {@code false}, el resultado es {@code false}.
     *
     * @param columns arreglo de valores de la fila actual del CSV.
     * @return {@code true} si al menos uno de los filtros internos acepta la fila;
     *         {@code false} si todos la rechazan.
     */
    @Override
    public boolean matches(String[] columns) {
        for (CsvRowFilter f : filters) {
            if (f.matches(columns)) {
                return true;
            }
        }
        return false;
    }
}
