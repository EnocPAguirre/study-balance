package proyecto.core.csv.filters.util;

import proyecto.core.csv.filters.CsvFilterCondition;

/**
 * Construye instancias de {@link CsvFilterCondition}
 * a partir de una condición simple en texto (sin conectores lógicos AND/OR).
 *
 * <p>Ejemplos de condiciones simples soportadas:</p>
 * <pre>
 *   GPA >= 9
 *   Age  &lt; 25
 *   Has_Anxiety = "Yes"
 * </pre>
 *
 * La responsabilidad de esta clase es:
 * <ul>
 *     <li>Detectar el operador de comparación presente en la condición.</li>
 *     <li>Separar el nombre de la columna (lado izquierdo) del valor (lado derecho).</li>
 *     <li>Resolver el índice de la columna usando {@link ColumnIndexResolver}.</li>
 *     <li>Determinar si el valor derecho “parece” numérico mediante
 *         {@link CsvFilterNumberUtils#looksNumeric(String)}.</li>
 *     <li>Construir un {@link CsvFilterCondition} listo para ser evaluado sobre filas de CSV.</li>
 * </ul>
 */
public final class CsvFilterConditionFactory {

    /**
     * Operadores soportados por la condición simple.
     * <p>
     * El orden es importante para asegurar que operadores de dos caracteres
     * (como {@code <=} y {@code >=}) se detecten correctamente antes que
     * los de un solo carácter.
     */
    private static final String[] OPERATORS = {"<=", ">=", "!=", "=", "<", ">"};

    /**
     * Constructor privado para evitar instanciación.
     * <p>
     * Esta es una clase de utilería que solo expone métodos estáticos.
     */
    private CsvFilterConditionFactory() {
        // utility
    }

    /**
     * Parsea una condición simple en texto y devuelve un {@link CsvFilterCondition},
     * o {@code null} si no se puede interpretar.
     * <p>
     * Flujo de trabajo:
     * <ol>
     *     <li>Si {@code c} es {@code null} o está vacía, devuelve {@code null}.</li>
     *     <li>Busca el primer operador de la lista {@link #OPERATORS} que aparezca en la cadena.</li>
     *     <li>Si no encuentra ningún operador, imprime un mensaje de error y devuelve {@code null}.</li>
     *     <li>Separa:
     *         <ul>
     *             <li>Lado izquierdo: nombre de la columna.</li>
     *             <li>Lado derecho: valor literal como texto (se conserva para evaluación posterior).</li>
     *         </ul>
     *     </li>
     *     <li>Resuelve el índice de la columna con {@link ColumnIndexResolver#indexOf(String)}.
     *         Si la columna no existe, imprime el mensaje de la excepción y devuelve {@code null}.</li>
     *     <li>Determina si el valor derecho es numérico con
     *         {@link CsvFilterNumberUtils#looksNumeric(String)}.</li>
     *     <li>Crea y devuelve un {@link CsvFilterCondition} parametrizado con:
     *         índice de columna, operador, valor derecho y bandera de tipo numérico.</li>
     * </ol>
     *
     * @param c         condición simple en texto (sin AND/OR), por ejemplo
     *                  {@code "GPA >= 9"} o {@code "Has_Anxiety = \"Yes\""}.
     * @param resolver  resolvedor de índices de columna, utilizado para traducir
     *                  el nombre de columna del lado izquierdo a un índice 0-based.
     * @return un {@link CsvFilterCondition} listo para usarse en filtros de filas,
     *         o {@code null} si la condición no es válida o no se puede parsear.
     */
    public static CsvFilterCondition parseCondition(String c,
                                                    ColumnIndexResolver resolver) {
        if (c == null) {
            return null;
        }
        String trimmed = c.trim();
        if (trimmed.isEmpty()) {
            return null;
        }

        String operadorEncontrado = null;
        int indexOp = -1;

        // Buscar el primer operador que aparezca en la condición
        for (String op : OPERATORS) {
            indexOp = trimmed.indexOf(op);
            if (indexOp >= 0) {
                operadorEncontrado = op;
                break;
            }
        }

        if (operadorEncontrado == null) {
            System.err.println("Condición no reconocida: " + c);
            return null;
        }

        // Lado izquierdo: nombre de columna
        String left = trimmed.substring(0, indexOp).trim();
        // Lado derecho: valor literal
        String right = trimmed.substring(indexOp + operadorEncontrado.length()).trim();

        int columnIndex;
        try {
            columnIndex = resolver.indexOf(left);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            return null;
        }

        boolean numeric = CsvFilterNumberUtils.looksNumeric(right);

        return new CsvFilterCondition(columnIndex, operadorEncontrado, right, numeric);
    }
}
