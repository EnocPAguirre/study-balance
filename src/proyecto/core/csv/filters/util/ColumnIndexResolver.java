package proyecto.core.csv.filters.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Resuelve índices de columnas (0-based) a partir de la cabecera del CSV.
 * <p>
 * Esta clase permite trabajar con <strong>nombres de columna</strong> en lugar de
 * usar índices numéricos "mágicos". A partir del arreglo de cabecera, construye
 * un mapa nombre → índice (0-based) que después puede usarse para:
 * <ul>
 *     <li>Validar que una columna existe en el header.</li>
 *     <li>Recuperar su índice 0-based para acceder al arreglo de valores de una fila.</li>
 * </ul>
 *
 * Ejemplo de uso:
 * <pre>
 * String[] header = {"GPA", "Age", "Stress_Level"};
 * ColumnIndexResolver resolver = new ColumnIndexResolver(header);
 * int idxGpa  = resolver.indexOf("GPA");          // 0
 * int idxAge  = resolver.indexOf("Age");          // 1
 * boolean hasAnxiety = resolver.hasColumn("Has_Anxiety"); // false
 * </pre>
 */
public final class ColumnIndexResolver {

    /**
     * Mapa de nombre de columna → índice 0-based.
     */
    private final Map<String, Integer> indexByName = new HashMap<>();

    /**
     * Construye el resolver a partir del arreglo de nombres de columna.
     * <p>
     * Para cada entrada del arreglo:
     * <ul>
     *     <li>Si el nombre no es {@code null}, se recorta con {@code trim()}.</li>
     *     <li>Se almacena en el mapa con su posición como índice 0-based.</li>
     * </ul>
     * Nombres repetidos sobrescribirán el valor previo, quedándose con el último índice.
     *
     * @param headerCols arreglo de nombres de columna tal como vienen del CSV
     *                   (normalmente resultado de dividir la línea de cabecera).
     */
    public ColumnIndexResolver(String[] headerCols) {
        if (headerCols != null) {
            for (int i = 0; i < headerCols.length; i++) {
                String name = headerCols[i];
                if (name != null) {
                    indexByName.put(name.trim(), i); // índice 0-based
                }
            }
        }
    }

    /**
     * Devuelve el índice 0-based de la columna con el nombre indicado.
     * <p>
     * Si la columna no existe en el mapa construido a partir del header,
     * lanza una {@link IllegalArgumentException}.
     *
     * @param columnName nombre de la columna tal como aparece en la cabecera
     *                   (puede incluir espacios, que serán recortados).
     * @return índice 0-based de la columna.
     * @throws IllegalArgumentException si {@code columnName} es {@code null} o
     *                                  si la columna no se encuentra en el header.
     */
    public int indexOf(String columnName) {
        if (columnName == null) {
            throw new IllegalArgumentException("Nombre de columna null");
        }
        Integer idx = indexByName.get(columnName.trim());
        if (idx == null) {
            throw new IllegalArgumentException("Columna no encontrada en header: " + columnName);
        }
        return idx;
    }

    /**
     * Indica si el header contiene una columna con ese nombre.
     *
     * @param columnName nombre de la columna a verificar (se usará {@code trim()}).
     * @return {@code true} si la columna existe en el mapa; {@code false} en caso contrario
     *         o si {@code columnName} es {@code null}.
     */
    public boolean hasColumn(String columnName) {
        if (columnName == null) {
            return false;
        }
        return indexByName.containsKey(columnName.trim());
    }

    /**
     * Devuelve el número total de columnas mapeadas.
     *
     * @return cantidad de claves (nombres de columna) registradas en el mapa.
     */
    public int size() {
        return indexByName.size();
    }
}
