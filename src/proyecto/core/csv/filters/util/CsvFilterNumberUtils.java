package proyecto.core.csv.filters.util;

/**
 * Utilidades numéricas para filtros CSV.
 * <p>
 * Esta clase proporciona métodos auxiliares para decidir cómo
 * interpretar valores de las condiciones de filtro, en particular
 * para distinguir entre valores numéricos y valores de texto.
 * <p>
 * Es una clase de utilería y no debe ser instanciada.
 */
public final class CsvFilterNumberUtils {

    /**
     * Constructor privado para evitar la instanciación.
     * <p>
     * Toda la funcionalidad se ofrece a través de métodos estáticos.
     */
    private CsvFilterNumberUtils() {
        // utility
    }

    /**
     * Determina si una cadena "parece" numérica para efectos de filtrado.
     * <p>
     * Reglas:
     * <ul>
     *     <li>Si la cadena es {@code null}, devuelve {@code false}.</li>
     *     <li>Se recortan espacios al inicio y al final.</li>
     *     <li>Si la cadena está entre comillas dobles (por ejemplo, {@code "\"123\""}),
     *         se considera texto y se devuelve {@code false}.</li>
     *     <li>En otro caso, se intenta hacer un {@link Double#parseDouble(String)}:
     *         <ul>
     *             <li>Si parsea correctamente, devuelve {@code true}.</li>
     *             <li>Si lanza {@link NumberFormatException}, devuelve {@code false}.</li>
     *         </ul>
     *     </li>
     * </ul>
     *
     * Ejemplos:
     * <pre>
     * looksNumeric("123")       -> true
     * looksNumeric("  3.14 ")   -> true
     * looksNumeric("\"123\"")   -> false
     * looksNumeric("ABC")       -> false
     * looksNumeric(null)        -> false
     * </pre>
     *
     * @param s cadena a evaluar.
     * @return {@code true} si la cadena se interpreta como numérica para el filtrado,
     *         {@code false} en caso contrario.
     */
    public static boolean looksNumeric(String s) {
        if (s == null) return false;
        s = s.trim();

        if (s.startsWith("\"") && s.endsWith("\"") && s.length() >= 2) {
            return false;
        }
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
