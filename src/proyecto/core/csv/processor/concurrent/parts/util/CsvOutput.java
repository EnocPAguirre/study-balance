package proyecto.core.csv.processor.concurrent.parts.util;

import proyecto.core.csv.processor.common.util.CsvUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Utilidad para generar el archivo CSV final a partir de las salidas parciales
 * producidas por el procesamiento concurrente.
 * <p>
 * Esta clase:
 * <ul>
 *     <li>Reconstruye la cabecera filtrada usando las columnas seleccionadas.</li>
 *     <li>Concatena el contenido de todos los archivos parciales en un solo archivo de salida.</li>
 *     <li>Ignora líneas vacías en los archivos parciales.</li>
 * </ul>
 */
public final class CsvOutput {

    /**
     * Constructor privado para evitar instanciación.
     * <p>
     * La clase funciona únicamente como contenedor de métodos estáticos.
     */
    private CsvOutput() {
        // Evitar instanciación
    }

    /**
     * Escribe el archivo CSV final a partir de las salidas parciales generadas
     * por los workers concurrentes.
     * <p>
     * Flujo:
     * <ol>
     *     <li>Abre un {@link BufferedWriter} sobre {@code outputFile}.</li>
     *     <li>Construye la cabecera filtrada usando
     *         {@link CsvUtils#buildFilteredLine(String[], int[], String)} con:
     *         <ul>
     *             <li>{@code headerCols}: nombres de columnas originales.</li>
     *             <li>{@code selectedIndexes}: índices de columnas seleccionadas.</li>
     *             <li>{@code separator}: separador del CSV.</li>
     *         </ul>
     *     </li>
     *     <li>Escribe dicha cabecera en el archivo de salida.</li>
     *     <li>Para cada archivo en {@code partialOutputs}:
     *         <ul>
     *             <li>Abre un {@link BufferedReader} para leer línea por línea.</li>
     *             <li>Ignora líneas vacías o de solo espacios.</li>
     *             <li>Escribe las demás líneas tal cual en el archivo de salida.</li>
     *         </ul>
     *     </li>
     * </ol>
     *
     * @param outputFile      archivo CSV final a generar (se sobrescribe si existe).
     * @param headerCols      arreglo con los nombres de las columnas originales del CSV.
     * @param selectedIndexes índices de las columnas que deben incluirse en el CSV final.
     * @param partialOutputs  arreglo de archivos CSV parciales generados previamente
     *                        por el procesamiento concurrente.
     * @param separator       separador de columnas del CSV (por ejemplo, {@code ","}).
     * @throws IOException si ocurre un error de E/S al leer las partes o escribir el archivo final.
     */
    public static void writeFinalFile(File outputFile,
                                      String[] headerCols,
                                      int[] selectedIndexes,
                                      File[] partialOutputs,
                                      String separator) throws IOException {

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))) {

            String headerFiltered =
                    CsvUtils.buildFilteredLine(headerCols, selectedIndexes, separator);
            bw.write(headerFiltered);
            bw.newLine();

            for (File part : partialOutputs) {
                try (BufferedReader br = new BufferedReader(new FileReader(part))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        if (line.trim().isEmpty()) {
                            continue;
                        }
                        bw.write(line);
                        bw.newLine();
                    }
                }
            }
        }
    }
}
