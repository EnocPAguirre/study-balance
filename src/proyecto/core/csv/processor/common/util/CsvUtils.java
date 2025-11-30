package proyecto.core.csv.processor.common.util;

/**
 * Utilidades generales para trabajar con archivos CSV.
 * <p>
 * Esta clase agrupa operaciones comunes como:
 * <ul>
 *     <li>Parsear una especificación de columnas a índices 0-based.</li>
 *     <li>Construir una línea con solo ciertas columnas seleccionadas.</li>
 *     <li>Contar el número de columnas de una cabecera.</li>
 *     <li>Dividir una línea en columnas usando un separador dado.</li>
 * </ul>
 * Es una clase de utilería y no debe ser instanciada.
 */
public final class CsvUtils {

    /**
     * Constructor privado para evitar la creación de instancias.
     * <p>
     * Toda la funcionalidad se expone mediante métodos estáticos.
     */
    private CsvUtils() {
        // Clase de utilería, no instanciable
    }

    /**
     * Convierte una especificación de columnas (en notación 1-based)
     * a un arreglo de índices 0-based.
     * <p>
     * Reglas:
     * <ul>
     *     <li>Si {@code columnas} es {@code null}, está vacía o es {@code "*"},
     *         se devuelven todos los índices desde {@code 0} hasta {@code totalColumns - 1}.</li>
     *     <li>En caso contrario, se espera una lista separada por comas con números de columna
     *         en base 1 (por ejemplo, {@code "2,7,13"}), que se convierten a índices 0-based
     *         (por ejemplo, {@code [1,6,12]}).</li>
     *     <li>Si alguna columna está fuera de rango (menor que 1 o mayor que {@code totalColumns}),
     *         se lanza una {@link IllegalArgumentException}.</li>
     * </ul>
     *
     * Ejemplos:
     * <pre>
     * parseColumnSelection("*", 4)      -> [0,1,2,3]
     * parseColumnSelection("2,7,13", n) -> [1,6,12]
     * </pre>
     *
     * @param columnas     especificación de columnas en notación 1-based
     *                     (por ejemplo, {@code "*"} o {@code "2,7,13"}).
     * @param totalColumns número total de columnas disponibles en el CSV.
     * @return un arreglo de índices 0-based que representan las columnas seleccionadas.
     * @throws IllegalArgumentException si se especifica una columna fuera de rango.
     * @throws NumberFormatException   si alguna de las partes no es un número entero válido.
     */
    public static int[] parseColumnSelection(String columnas, int totalColumns) {
        if (columnas == null || columnas.trim().isEmpty() || columnas.trim().equals("*")) {
            int[] all = new int[totalColumns];
            for (int i = 0; i < totalColumns; i++) {
                all[i] = i;
            }
            return all;
        }
        String[] partes = columnas.split(",");
        int[] idx = new int[partes.length];
        for (int i = 0; i < partes.length; i++) {
            String p = partes[i].trim();
            int colNum = Integer.parseInt(p); // columnas en 1-based
            idx[i] = colNum - 1;
            if (idx[i] < 0 || idx[i] >= totalColumns) {
                throw new IllegalArgumentException("Columna fuera de rango: " + colNum);
            }
        }
        return idx;
    }

    /**
     * Construye una línea CSV a partir de un arreglo de columnas,
     * tomando únicamente los índices indicados en {@code selectedIndexes}.
     * <p>
     * Comportamiento:
     * <ul>
     *     <li>Recorre {@code selectedIndexes} en orden.</li>
     *     <li>Por cada índice, agrega el valor correspondiente en {@code cols}
     *         separado por {@code separator}.</li>
     *     <li>Si un índice está fuera de rango en {@code cols}, se agrega
     *         una cadena vacía en su lugar.</li>
     * </ul>
     *
     * @param cols            arreglo con los valores de todas las columnas de una fila.
     * @param selectedIndexes índices de las columnas que se deben incluir en la línea resultante.
     * @param separator       separador a utilizar entre columnas (por ejemplo, {@code ","}).
     * @return una cadena que representa la línea CSV filtrada, conteniendo solo las columnas seleccionadas.
     */
    public static String buildFilteredLine(String[] cols, int[] selectedIndexes, String separator) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < selectedIndexes.length; i++) {
            if (i > 0) {
                sb.append(separator);
            }
            int idx = selectedIndexes[i];
            if (idx >= 0 && idx < cols.length) {
                sb.append(cols[idx]);
            } else {
                sb.append("");
            }
        }
        return sb.toString();
    }

    /**
     * Cuenta el número de columnas a partir de una línea de cabecera.
     * <p>
     * La cuenta se realiza partiendo la línea con {@link String#split(String, int)}
     * usando el separador indicado y límite {@code -1}, para conservar columnas vacías.
     *
     * @param headerLine línea de cabecera del CSV (normalmente la primera línea del archivo).
     * @param separator  separador de columnas (por ejemplo, {@code ","}).
     * @return número de columnas detectadas. Devuelve {@code 0} si {@code headerLine} es {@code null}.
     */
    public static int countColumns(String headerLine, String separator) {
        if (headerLine == null) {
            return 0;
        }
        return headerLine.split(separator, -1).length;
    }

    /**
     * Divide una línea en columnas usando el separador dado.
     * <p>
     * Se utiliza {@link String#split(String, int)} con límite {@code -1}
     * para conservar columnas vacías al final de la línea.
     *
     * @param line      línea a dividir (por ejemplo, una fila del CSV).
     * @param separator separador de columnas (por ejemplo, {@code ","}).
     * @return un arreglo de cadenas con las columnas de la línea; si {@code line} es {@code null},
     *         se devuelve un arreglo vacío.
     */
    public static String[] splitColumns(String line, String separator) {
        if (line == null) {
            return new String[0];
        }
        return line.split(separator, -1);
    }
}
