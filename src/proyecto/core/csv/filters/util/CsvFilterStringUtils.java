package proyecto.core.csv.filters.util;

/**
 * Utilidades de cadena para filtros CSV.
 * <p>
 * Esta clase contiene funciones de ayuda para tratar valores de texto
 * que aparecen en expresiones de filtro, especialmente aquellos que
 * vienen entre comillas dobles.
 * <p>
 * Es una clase de utilería y no debe ser instanciada.
 */
public final class CsvFilterStringUtils {

    /**
     * Constructor privado para evitar instanciación.
     * <p>
     * Toda la funcionalidad se expone mediante métodos estáticos.
     */
    private CsvFilterStringUtils() {
        // Evitar instanciación
    }


    /**
     * Elimina comillas dobles al inicio y al final de la cadena, si existen.
     * <p>
     * Comportamiento:
     * <ul>
     *     <li>Si {@code s} es {@code null}, devuelve {@code null}.</li>
     *     <li>Recorta espacios al inicio y al final con {@code trim()}.</li>
     *     <li>Si la cadena resultante tiene longitud ≥ 2 y comienza y termina
     *         con comillas dobles ({@code '"'}), devuelve el contenido interno
     *         sin esas comillas.</li>
     *     <li>En caso contrario, devuelve la cadena recortada tal cual.</li>
     * </ul>
     *
     * <p><strong>Ejemplos:</strong></p>
     * <pre>
     * stripQuotes("\"Hola\"")   -> "Hola"
     * stripQuotes("  \"Hi\" ")  -> "Hi"
     * stripQuotes("Hola")       -> "Hola"
     * stripQuotes(null)         -> null
     * </pre>
     *
     * @param s cadena de entrada, posiblemente con comillas dobles externas.
     * @return la cadena sin comillas externas si las tenía, o la misma cadena
     *         recortada si no las tenía; {@code null} si la entrada era {@code null}.
     */
    public static String stripQuotes(String s) {
        if (s == null) return null;
        s = s.trim();
        if (s.length() >= 2 && s.startsWith("\"") && s.endsWith("\"")) {
            return s.substring(1, s.length() - 1);
        }
        return s;
    }
}
