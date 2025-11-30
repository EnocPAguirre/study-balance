package proyecto.core.csv.processor.concurrent.util;

import java.io.File;

/**
 * Utilidad para eliminar directorios temporales y todo su contenido.
 * <p>
 * Esta clase proporciona un único método estático que:
 * <ul>
 *     <li>Elimina de forma recursiva todos los archivos y subdirectorios de un directorio dado.</li>
 *     <li>Intenta eliminar también el propio directorio raíz.</li>
 *     <li>En caso de no poder borrar algún archivo o directorio, muestra un mensaje de error
 *         por {@code System.err} indicando la ruta afectada.</li>
 * </ul>
 * Está pensada principalmente para limpiar directorios temporales generados durante
 * el procesamiento concurrente de archivos CSV.
 */
public final class CleanupDirectory {

    /**
     * Constructor privado para evitar instanciación.
     * <p>
     * La clase funciona únicamente como contenedor de métodos estáticos.
     */
    private CleanupDirectory() {
        // Evitar instanciación
    }

    /**
     * Elimina un directorio y todo su contenido de forma recursiva.
     * <p>
     * Comportamiento:
     * <ol>
     *     <li>Si {@code dir} es {@code null} o no existe, el método no hace nada.</li>
     *     <li>Lista los elementos del directorio:
     *         <ul>
     *             <li>Si el elemento es un subdirectorio, se llama recursivamente
     *                 a este mismo método.</li>
     *             <li>Si el elemento es un archivo, se intenta eliminar con {@link File#delete()}.</li>
     *         </ul>
     *     </li>
     *     <li>Tras eliminar el contenido, intenta borrar el propio directorio.</li>
     *     <li>Si no se puede eliminar algún archivo o directorio, se imprime un mensaje
     *         de error por {@code System.err} indicando la ruta.</li>
     * </ol>
     *
     * @param dir directorio a eliminar junto con todo su contenido. Normalmente se trata
     *            de un directorio temporal generado durante el procesamiento.
     */
    public static void deleteTemporaryDirectory(File dir) {
        if (dir == null || !dir.exists()) {
            return;
        }
        File[] children = dir.listFiles();
        if (children != null) {
            for (File child : children) {
                if (child.isDirectory()) {
                    deleteTemporaryDirectory(child);
                } else {
                    if (!child.delete()) {
                        System.err.println("No se pudo borrar archivo temporal: " + child.getAbsolutePath());
                    }
                }
            }
        }
        if (!dir.delete()) {
            System.err.println("No se pudo borrar directorio temporal: " + dir.getAbsolutePath());
        }
    }
}
