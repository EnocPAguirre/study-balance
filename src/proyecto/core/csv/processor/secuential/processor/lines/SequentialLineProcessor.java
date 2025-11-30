package proyecto.core.csv.processor.secuential.processor.lines;

import proyecto.core.csv.processor.common.processor.CsvProcessingContext;
import proyecto.core.csv.processor.common.util.CsvUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

/**
 * Procesa línea por línea un archivo CSV en modo secuencial.
 * <p>
 * Aplica el filtro configurado (si existe), valida el número de columnas,
 * registra errores en el log y escribe las filas válidas ya filtradas
 * al archivo de salida.
 */
public class SequentialLineProcessor {

    /**
     * Crea una nueva instancia de {@code SequentialLineProcessor}.
     * <p>
     * Actualmente no requiere configuración adicional; la instancia se usa
     * solo como contenedor del método {@link #processLines(BufferedReader, BufferedWriter, BufferedWriter, CsvProcessingContext, String)}.
     */
    public SequentialLineProcessor() {
        // Constructor explícito para satisfacer doclint / javadoc
    }

    /**
     * Procesa todas las líneas de datos de un CSV de manera secuencial.
     * <p>
     * Comportamiento:
     * <ul>
     *     <li>Lee cada línea del {@link BufferedReader} a partir de donde se quedó
     *         (normalmente después de la cabecera).</li>
     *     <li>Ignora líneas vacías.</li>
     *     <li>Valida que el número de columnas coincida con {@code ctx.totalColumns()}.
     *         Si no coincide, registra un mensaje en el log y cuenta la línea como error.</li>
     *     <li>Si existe un filtro en {@code ctx.filter()}, solo procesa las filas que lo cumplen.</li>
     *     <li>Construye la línea filtrada (selección de columnas) y la escribe en {@code bw}.</li>
     * </ul>
     *
     * @param br         lector desde el cual se leen las líneas de datos del CSV.
     * @param bw         escritor donde se guardan las líneas válidas ya filtradas y con columnas seleccionadas.
     * @param log        escritor de log donde se registran líneas con errores de formato (número de columnas inválido).
     * @param ctx        contexto de procesamiento con cabecera, total de columnas, índices seleccionados y filtro.
     * @param separator  separador de columnas utilizado en el CSV (por ejemplo, {@code ","}).
     * @return un {@link RowStats} con el conteo de líneas válidas y líneas con error.
     * @throws IOException si ocurre algún problema de entrada/salida al leer o escribir.
     */
    public RowStats processLines(BufferedReader br,
                                 BufferedWriter bw,
                                 BufferedWriter log,
                                 CsvProcessingContext ctx,
                                 String separator) throws IOException {

        String line;
        int lineNumber = 1;
        int validLines = 0;
        int errorLines = 0;

        while ((line = br.readLine()) != null) {
            lineNumber++;

            if (line.trim().isEmpty()) {
                continue;
            }

            String[] cols = CsvUtils.splitColumns(line, separator);

            if (cols.length != ctx.totalColumns()) {
                log.write("Línea " + lineNumber + " columnas inválidas: "
                        + cols.length + " (esperadas " + ctx.totalColumns() + ")");
                log.newLine();
                errorLines++;
                continue;
            }

            if (ctx.filter() != null && !ctx.filter().matches(cols)) {
                continue;
            }

            String outLine = CsvUtils.buildFilteredLine(
                    cols,
                    ctx.selectedIndexes(),
                    separator
            );
            bw.write(outLine);
            bw.newLine();
            validLines++;
        }

        return new RowStats(validLines, errorLines);
    }
}
