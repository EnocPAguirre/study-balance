package proyecto.core.csv.filters;

import proyecto.core.csv.filters.util.CsvFilterStringUtils;

/**
 * Representa una condición simple sobre una columna de un CSV:
 * <pre>
 *   colIndex OP literal
 * </pre>
 * donde:
 * <ul>
 *     <li>{@code colIndex} es el índice 0-based de la columna.</li>
 *     <li>{@code OP} es uno de: {@code =}, {@code !=}, {@code <}, {@code >}, {@code <=}, {@code >=}.</li>
 *     <li>{@code literal} es el valor contra el que se compara.</li>
 * </ul>
 * <p>
 * Esta condición puede operar en modo numérico o de texto, dependiendo del
 * valor del flag {@code numeric}. Si es numérico, se parsean ambos valores
 * como {@code double}. Si es de texto, se comparan cadenas, eliminando
 * comillas externas cuando aplica.
 */
public final class CsvFilterCondition implements CsvRowFilter {

    /**
     * Índice 0-based de la columna sobre la que se aplica la condición.
     */
    private final int columnIndex;  // 0-based

    /**
     * Operador de comparación utilizado (por ejemplo, {@code "="}, {@code "!="}, {@code "<"}, etc.).
     */
    private final String operator;

    /**
     * Literal (como texto) contra el que se compara el valor de la columna.
     * Puede ser numérico o textual, según el flag {@code numeric}.
     */
    private final String literal;

    /**
     * Indica si la comparación debe tratar el valor como numérico ({@code true})
     * o como texto ({@code false}).
     */
    private final boolean numeric;

    /**
     * Crea una condición simple sobre una columna.
     *
     * @param columnIndex índice 0-based de la columna sobre la que se aplica la condición.
     * @param operator    operador de comparación ({@code =}, {@code !=}, {@code <}, {@code >}, {@code <=}, {@code >=}).
     * @param literal     valor literal con el que se compara el contenido de la columna.
     * @param numeric     {@code true} si la comparación debe realizarse como numérica;
     *                    {@code false} si la comparación es de texto.
     */
    public CsvFilterCondition(int columnIndex, String operator, String literal, boolean numeric) {
        this.columnIndex = columnIndex;
        this.operator = operator;
        this.literal = literal;
        this.numeric = numeric;
    }

    /**
     * Evalúa si la fila cumple esta condición.
     * <p>
     * Flujo:
     * <ol>
     *     <li>Si {@code columnIndex} está fuera del rango de {@code columns}, devuelve {@code false}.</li>
     *     <li>Si {@code numeric} es {@code true}:
     *         <ul>
     *             <li>Intenta parsear el valor de la columna y el literal a {@code double}.</li>
     *             <li>Aplica el operador numérico correspondiente.</li>
     *             <li>Si no se puede parsear alguno, devuelve {@code false}.</li>
     *         </ul>
     *     </li>
     *     <li>Si {@code numeric} es {@code false}:
     *         <ul>
     *             <li>Elimina comillas externas de ambos valores usando
     *                 {@link CsvFilterStringUtils#stripQuotes(String)}.</li>
     *             <li>Compara cadenas con {@code =} o {@code !=}.</li>
     *         </ul>
     *     </li>
     * </ol>
     *
     * @param columns arreglo de valores de la fila actual del CSV.
     * @return {@code true} si la fila cumple la condición; {@code false} en caso contrario.
     */
    @Override
    public boolean matches(String[] columns) {
        if (columnIndex < 0 || columnIndex >= columns.length) {
            return false;
        }
        String value = columns[columnIndex];

        if (numeric) {
            try {
                double v = Double.parseDouble(value);
                double target = Double.parseDouble(literal);
                if ("=".equals(operator))  return v == target;
                if ("!=".equals(operator)) return v != target;
                if ("<".equals(operator))  return v < target;
                if (">".equals(operator))  return v > target;
                if ("<=".equals(operator)) return v <= target;
                if (">=".equals(operator)) return v >= target;
                return false;
            } catch (NumberFormatException e) {
                return false;
            }
        } else {
            String val = CsvFilterStringUtils.stripQuotes(value);
            String lit = CsvFilterStringUtils.stripQuotes(literal);
            if ("=".equals(operator))  return val.equals(lit);
            if ("!=".equals(operator)) return !val.equals(lit);
            return false;
        }
    }

}
