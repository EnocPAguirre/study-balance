package proyecto.core.csv.processor.common.util;

import java.io.File;

/**
 * Utilidad para validar archivos de entrada usados en el procesamiento CSV.
 * <p>
 * Esta clase proporciona un método estático que:
 * <ul>
 *     <li>Verifica que la ruta indicada exista.</li>
 *     <li>Comprueba que corresponda a un archivo regular (no un directorio).</li>
 *     <li>En caso de error, muestra un mensaje por {@code System.err} y devuelve {@code null}.</li>
 * </ul>
 * Está pensada para ser usada antes de iniciar cualquier operación de lectura
 * sobre el archivo CSV de entrada.
 */
public final class FileValidator {

    /**
     * Constructor privado para evitar la creación de instancias.
     * <p>
     * La clase funciona únicamente como contenedor de métodos estáticos.
     */
    private FileValidator() {
    }

    /**
     * Valida que la ruta del archivo de entrada exista y sea un archivo regular.
     * <p>
     * Comportamiento:
     * <ul>
     *     <li>Crea un {@link File} a partir de {@code inputPath}.</li>
     *     <li>Comprueba que {@link File#exists()} y {@link File#isFile()} sean verdaderos.</li>
     *     <li>Si la validación falla, imprime un mensaje de error indicando la ruta absoluta
     *         y devuelve {@code null}.</li>
     *     <li>Si la validación es correcta, devuelve el objeto {@link File}.</li>
     * </ul>
     *
     * @param inputPath ruta del archivo de entrada a validar.
     * @return el {@link File} correspondiente a {@code inputPath} si es válido,
     *         o {@code null} si el archivo no existe o no es un archivo regular.
     */
    public static File validateInputFile(String inputPath) {
        File inputFile = new File(inputPath);
        if (!inputFile.exists() || !inputFile.isFile()) {
            System.err.println("El archivo de entrada no existe: " + inputFile.getAbsolutePath());
            return null;
        }
        return inputFile;
    }
}
