package proyecto.core.csv.filters.util;

import proyecto.core.csv.filters.type.CsvAndFilter;
import proyecto.core.csv.filters.CsvFilterCondition;
import proyecto.core.csv.filters.CsvRowFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * Construye un {@link CsvRowFilter} que representa la conjunción (AND)
 * de varias condiciones simples separadas por la palabra clave {@code AND}.
 * <p>
 * Ejemplo de expresión soportada:
 * <pre>
 *   "GPA >= 9 AND Age >= 18 AND Stress_Level >= 7"
 * </pre>
 * Cada fragmento entre {@code AND} se interpreta como una condición simple,
 * que se parsea usando {@link CsvFilterConditionFactory#parseCondition(String, ColumnIndexResolver)}.
 */
public final class CsvAndGroupBuilder {

    /**
     * Constructor privado para evitar la instanciación.
     * Esta es una clase de utilería con solo métodos estáticos.
     */
    private CsvAndGroupBuilder() {
        // utility
    }

    /**
     * Parsea una expresión que contiene condiciones unidas por {@code AND}
     * y construye el filtro correspondiente.
     * <p>
     * Comportamiento:
     * <ul>
     *     <li>Si {@code expr} es {@code null} o está vacía, devuelve {@code null}.</li>
     *     <li>Separa la expresión por {@code AND} (ignorando mayúsculas/minúsculas).</li>
     *     <li>Cada fragmento se intenta convertir en una {@link CsvFilterCondition}
     *         mediante {@link CsvFilterConditionFactory#parseCondition(String, ColumnIndexResolver)}.</li>
     *     <li>Se descartan las condiciones {@code null} (no parseables).</li>
     *     <li>Si no se obtuvo ninguna condición válida, devuelve {@code null}.</li>
     *     <li>Si solo hay una condición, se devuelve directamente esa condición.</li>
     *     <li>Si hay varias condiciones, se devuelve un {@link CsvAndFilter}
     *         que representa la conjunción lógica de todas ellas.</li>
     * </ul>
     *
     * @param expr      expresión de filtro que contiene condiciones separadas por {@code AND},
     *                  por ejemplo: {@code "GPA >= 9 AND Age >= 18"}.
     * @param resolver  resolvedor de índices de columna que permite mapear nombres de columna
     *                  a posiciones dentro del arreglo de valores.
     * @return un {@link CsvRowFilter} que representa la conjunción de las condiciones
     *         parseadas, o {@code null} si no se pudo construir ninguna.
     */
    public static CsvRowFilter buildAndGroup(String expr,
                                             ColumnIndexResolver resolver) {
        if (expr == null) {
            return null;
        }
        expr = expr.trim();
        if (expr.isEmpty()) {
            return null;
        }

        String[] andParts = expr.split("(?i)\\s+AND\\s+");
        List<CsvRowFilter> andFilters = new ArrayList<>();

        for (String andPart : andParts) {
            CsvFilterCondition cond =
                    CsvFilterConditionFactory.parseCondition(andPart.trim(), resolver);
            if (cond != null) {
                andFilters.add(cond);
            }
        }

        if (andFilters.isEmpty()) {
            return null;
        }
        if (andFilters.size() == 1) {
            return andFilters.get(0);
        }
        return new CsvAndFilter(andFilters);
    }
}
