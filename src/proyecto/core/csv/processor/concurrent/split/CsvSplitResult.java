package proyecto.core.csv.processor.concurrent.split;

import java.io.File;
import java.util.List;

/**
 * Resultado de dividir un archivo CSV en varias partes para su procesamiento concurrente.
 * <p>
 * Esta clase es un contenedor inmutable que agrupa:
 * <ul>
 *     <li>La línea de cabecera original del CSV.</li>
 *     <li>El número total de columnas detectadas en la cabecera.</li>
 *     <li>La lista de archivos generados, cada uno correspondiente a una parte
 *         del CSV original (normalmente sin cabecera, solo datos).</li>
 * </ul>
 * Es producida típicamente por {@link CsvSplitter} y consumida por el manager
 * concurrente para lanzar los workers sobre cada parte.
 */
public final class CsvSplitResult {

    /**
     * Línea de cabecera original del archivo CSV.
     */
    private final String headerLine;

    /**
     * Número total de columnas detectadas en la cabecera.
     */
    private final int totalColumns;

    /**
     * Archivos que contienen las partes en las que se dividió el CSV original.
     */
    private final List<File> partFiles;

    /**
     * Crea un nuevo resultado de división de CSV.
     *
     * @param headerLine   línea de cabecera original leída del CSV.
     * @param totalColumns número total de columnas detectadas en la cabecera.
     * @param partFiles    lista de archivos que representan las partes del CSV
     *                     original divididas para procesamiento concurrente.
     */
    public CsvSplitResult(String headerLine, int totalColumns, List<File> partFiles) {
        this.headerLine = headerLine;
        this.totalColumns = totalColumns;
        this.partFiles = partFiles;
    }

    /**
     * Devuelve la línea de cabecera original del CSV.
     *
     * @return la cabecera del CSV como una cadena.
     */
    public String getHeaderLine() {
        return headerLine;
    }

    /**
     * Devuelve el número total de columnas detectadas en la cabecera.
     *
     * @return cantidad de columnas en la cabecera.
     */
    public int getTotalColumns() {
        return totalColumns;
    }

    /**
     * Devuelve la lista de archivos que contienen las partes en las que se
     * dividió el CSV original.
     *
     * @return lista de archivos con las partes del CSV.
     */
    public List<File> getPartFiles() {
        return partFiles;
    }
}
