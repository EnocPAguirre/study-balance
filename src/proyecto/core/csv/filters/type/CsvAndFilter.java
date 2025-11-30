package proyecto.core.csv.filters.type;

import proyecto.core.csv.filters.CsvRowFilter;

import java.util.List;

/**
 * Implementación de {@link CsvRowFilter} que combina varios filtros
 * mediante una operación lógica AND.
 * <p>
 * Una fila (arreglo de columnas) solo se considera que cumple el filtro
 * si <strong>todos</strong> los filtros internos lo cumplen.
 *
 * Ejemplo de uso típico:
 * <pre>
 * CsvRowFilter f1 = ...; // filtro por columna GPA
 * CsvRowFilter f2 = ...; // filtro por columna Age
 * CsvRowFilter andFilter = new CsvAndFilter(List.of(f1, f2));
 * // matches(columns) será true solo si f1.matches(columns) y f2.matches(columns) son true
 * </pre>
 */
public final class CsvAndFilter implements CsvRowFilter {

    /**
     * Lista de filtros que deben cumplirse todos (operación lógica AND).
     */
    private final List<CsvRowFilter> filters;

    /**
     * Crea un filtro compuesto que aplica AND sobre la lista de filtros dada.
     *
     * @param filters lista de filtros a combinar. Se asume que no es {@code null};
     *                si la lista está vacía, el comportamiento de {@link #matches(String[])}
     *                será devolver siempre {@code true}.
     */
    public CsvAndFilter(List<CsvRowFilter> filters) {
        this.filters = filters;
    }

    /**
     * Evalúa si la fila (arreglo de columnas) cumple TODOS los filtros internos.
     * <p>
     * Recorre cada filtro de la lista y llama a {@link CsvRowFilter#matches(String[])}:
     * si alguno devuelve {@code false}, el resultado global es {@code false}.
     * Si todos devuelven {@code true}, el resultado es {@code true}.
     *
     * @param columns arreglo de valores de la fila actual del CSV.
     * @return {@code true} si todos los filtros internos aceptan la fila;
     *         {@code false} si al menos uno la rechaza.
     */
    @Override
    public boolean matches(String[] columns) {
        for (CsvRowFilter f : filters) {
            if (!f.matches(columns)) {
                return false;
            }
        }
        return true;
    }
}
