package proyecto.core.csv.processor.secuential.util;

import proyecto.core.csv.processor.common.processor.CsvProcessingContext;
import proyecto.core.csv.processor.common.util.CsvUtils;

import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Utilidad para escribir la cabecera filtrada de un archivo CSV
 * en el procesamiento secuencial.
 * <p>
 * A partir de la información contenida en {@link CsvProcessingContext},
 * esta clase construye una nueva línea de cabecera con solo las columnas
 * seleccionadas y la escribe en el {@link BufferedWriter} de salida.
 */
public final class CsvHeaderWriter {

    /**
     * Constructor privado para evitar la creación de instancias.
     * <p>
     * Esta clase solo expone métodos estáticos.
     */
    private CsvHeaderWriter() {}

    /**
     * Escribe en el archivo de salida la cabecera filtrada según el contexto.
     * <p>
     * El método:
     * <ol>
     *     <li>Toma los nombres de columna originales desde
     *         {@link CsvProcessingContext#headerCols()}.</li>
     *     <li>Utiliza los índices de columnas seleccionados desde
     *         {@link CsvProcessingContext#selectedIndexes()}.</li>
     *     <li>Construye una nueva línea de cabecera utilizando el separador indicado,
     *         mediante {@link CsvUtils#buildFilteredLine(String[], int[], String)}.</li>
     *     <li>Escribe esa línea en el {@link BufferedWriter} seguido de un salto de línea.</li>
     * </ol>
     *
     * @param bw         escritor de salida donde se escribirá la cabecera filtrada.
     * @param ctx        contexto de procesamiento que contiene la cabecera original y
     *                   los índices de columnas seleccionadas.
     * @param separator  separador de columnas del CSV (por ejemplo, {@code ","}).
     * @throws IOException si ocurre un error de E/S al escribir en el {@link BufferedWriter}.
     */
    public static void writeFilteredHeader(BufferedWriter bw,
                                           CsvProcessingContext ctx,
                                           String separator) throws IOException {

        String headerFiltered = CsvUtils.buildFilteredLine(
                ctx.headerCols(),
                ctx.selectedIndexes(),
                separator
        );
        bw.write(headerFiltered);
        bw.newLine();
    }
}
