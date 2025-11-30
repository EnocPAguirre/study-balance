package proyecto.core.csv.processor.concurrent.manager.temp;

import java.io.File;
/**
 * Crea directorios temporales asociados
 * a un archivo de salida.
 * <p>
 * Esta clase se encarga de crear (si no existe) un directorio temporal
 * como subdirectorio del directorio padre del archivo de salida.
 * Si el archivo de salida no tiene directorio padre, se utiliza
 * el directorio actual ({@code "."}).
 */
public final class TempDirectoryFactory {

    /**
     * Constructor privado para evitar la creación de instancias.
     * <p>
     * La clase funciona únicamente como contenedor de métodos estáticos.
     */
    private TempDirectoryFactory() {
        // Evitar instanciación
    }

    /**
     * Crea (si es necesario) un directorio temporal como subdirectorio
     * del directorio padre del archivo de salida.
     * <p>
     * Comportamiento:
     * <ol>
     *     <li>Obtiene el directorio padre de {@code outputFile}.</li>
     *     <li>Si el padre es {@code null}, se usa el directorio actual ({@code "."}).</li>
     *     <li>Construye un nuevo {@link File} combinando el padre con {@code dirName}.</li>
     *     <li>Si el directorio no existe, intenta crearlo con {@link File#mkdirs()}.</li>
     *     <li>Si no se puede crear el directorio, se imprime un mensaje de error por
     *         {@code System.err} y se devuelve {@code null}.</li>
     *     <li>Si todo va bien, se devuelve el {@link File} del directorio temporal.</li>
     * </ol>
     *
     * @param outputFile archivo de salida cuyo directorio padre se utilizará como base
     *                   para crear el directorio temporal.
     * @param dirName    nombre del subdirectorio temporal a crear (por ejemplo,
     *                   {@code "temp_parts"}).
     * @return un objeto {@link File} apuntando al directorio temporal creado o existente,
     *         o {@code null} si no se pudo crear.
     */
    public static File createTempDir(File outputFile, String dirName) {
        File parent = outputFile.getParentFile();
        if (parent == null) {
            parent = new File("."); // directorio actual
        }

        File tempDir = new File(parent, dirName);
        if (!tempDir.exists() && !tempDir.mkdirs()) {
            System.err.println("No se pudo crear directorio temporal: " + tempDir.getAbsolutePath());
            return null;
        }
        return tempDir;
    }
}
