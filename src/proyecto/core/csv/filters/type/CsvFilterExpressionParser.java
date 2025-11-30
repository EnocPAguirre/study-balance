package proyecto.core.csv.filters.type;

import proyecto.core.csv.filters.CsvRowFilter;
import proyecto.core.csv.filters.util.ColumnIndexResolver;
import proyecto.core.csv.filters.util.CsvAndGroupBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Parser de expresiones de filtro para filas CSV.
 * <p>
 * Convierte una expresión textual (usando nombres de columna, operadores
 * de comparación y conectores lógicos AND/OR) en una estructura de
 * {@link CsvRowFilter} compuesta.
 *
 * <h2>Formato de la expresión</h2>
 * La expresión admite:
 * <ul>
 *     <li>Conectores lógicos:
 *         <ul>
 *             <li>{@code AND}</li>
 *             <li>{@code OR}</li>
 *         </ul>
 *         (no sensibles a mayúsculas/minúsculas).</li>
 *     <li>Operadores de comparación:
 *         <ul>
 *             <li>{@code <}, {@code >}, {@code =}, {@code !=}, {@code <=}, {@code >=}</li>
 *         </ul>
 *     </li>
 *     <li>Nombres de columna tal como aparecen en la cabecera del CSV.</li>
 * </ul>
 *
 * <h3>Ejemplos de expresiones válidas</h3>
 * <pre>
 * GPA >= 9
 * Age &gt;= 18 AND Stress_Level &gt;= 7
 * Has_Anxiety = "Yes" OR Borough_MX = "Coyoacan"
 * </pre>
 *
 * <h2>Árbol lógico construido</h2>
 * <ul>
 *     <li>Primero se parte la expresión por {@code OR}, construyendo un conjunto
 *         de grupos AND.</li>
 *     <li>Cada grupo se delega a {@link CsvAndGroupBuilder#buildAndGroup(String, ColumnIndexResolver)}
 *         para construir un filtro AND interno.</li>
 *     <li>Si hay un solo grupo, se devuelve directamente su filtro.</li>
 *     <li>Si hay varios grupos, se combinan en un {@link CsvOrFilter}.</li>
 * </ul>
 */
public final class CsvFilterExpressionParser {

    /**
     * Operadores soportados por el parser (no se usan directamente en este fragmento,
     * pero pueden emplearse en builders auxiliares).
     */
    private static final String[] OPERATORS = {"<=", ">=", "!=", "=", "<", ">"};

    /**
     * Constructor privado para evitar instanciación.
     */
    private CsvFilterExpressionParser() {}

    /**
     * Parsea una expresión de filtro y la convierte en un {@link CsvRowFilter}.
     * <p>
     * Flujo:
     * <ol>
     *     <li>Si {@code expression} es {@code null} o está vacía, devuelve {@code null}.</li>
     *     <li>Divide la expresión en partes separadas por {@code OR} (ignorando mayúsculas/minúsculas).</li>
     *     <li>Para cada parte:
     *         <ul>
     *             <li>Llama a {@link CsvAndGroupBuilder#buildAndGroup(String, ColumnIndexResolver)}
     *                 para construir un filtro AND.</li>
     *             <li>Si el resultado no es {@code null}, lo agrega a la lista de filtros OR.</li>
     *         </ul>
     *     </li>
     *     <li>Si no se construyó ningún filtro, devuelve {@code null}.</li>
     *     <li>Si solo hay un filtro, lo devuelve directamente.</li>
     *     <li>Si hay más de uno, devuelve un {@link CsvOrFilter} que representa la
     *         disyunción lógica de todos ellos.</li>
     * </ol>
     *
     * @param expression expresión de filtro en texto plano (por ejemplo,
     *                   {@code "GPA >= 9 AND Age >= 18 OR Borough_MX = \"Coyoacan\""}).
     * @param resolver   utilidad para resolver nombres de columna a índices dentro
     *                   del arreglo de columnas.
     * @return una implementación de {@link CsvRowFilter} que representa la expresión
     *         lógica indicada, o {@code null} si la expresión es nula, vacía o no se
     *         logró construir ningún filtro.
     */
    public static CsvRowFilter parse(String expression,
                                     ColumnIndexResolver resolver) {
        if (expression == null) {
            return null;
        }
        String expr = expression.trim();
        if (expr.isEmpty()) {
            return null;
        }

        // Separar por OR (case-insensitive) para crear grupos AND
        String[] orParts = expr.split("(?i)\\s+OR\\s+");
        List<CsvRowFilter> orFilters = new ArrayList<>();

        for (String orPart : orParts) {
            CsvRowFilter andFilter = CsvAndGroupBuilder.buildAndGroup(orPart.trim(), resolver);
            if (andFilter != null) {
                orFilters.add(andFilter);
            }
        }

        if (orFilters.isEmpty()) {
            return null;
        }
        if (orFilters.size() == 1) {
            return orFilters.get(0);
        }
        return new CsvOrFilter(orFilters);
    }
}
