package proyecto.core.csv.processor.concurrent.memory.worker;

import proyecto.core.csv.filters.CsvRowFilter;
import proyecto.core.csv.processor.common.util.CsvUtils;
import proyecto.core.csv.processor.concurrent.memory.util.BatchResult;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Tarea de trabajo que procesa un lote (batch) de líneas de un archivo CSV
 * en un contexto concurrente.
 *
 * <p>
 * Cada instancia de {@code BatchWorker}:
 * </p>
 * <ul>
 *     <li>Recibe un conjunto de líneas crudas (texto CSV).</li>
 *     <li>Aplica, si existe, un filtro de filas ({@link CsvRowFilter}).</li>
 *     <li>Construye la versión filtrada/proyectada de cada línea usando
 *         los índices de columnas seleccionados.</li>
 *     <li>Acumula la salida y el log en buffers en memoria.</li>
 * </ul>
 *
 * <p>
 * Al terminar, devuelve un {@link BatchResult} con:
 * número de batch, texto de salida, log y estadísticas
 * (líneas procesadas y con error).
 * </p>
 *
 * <p>
 * Esta clase está pensada para ser ejecutada por un pool de hilos
 * (por ejemplo, usando {@code ExecutorService}) a través de la interfaz
 * {@link Callable}.
 * </p>
 */
public final class BatchWorker implements Callable<BatchResult> {

    private final int batchNumber;
    private final List<String> lines;
    private final CsvRowFilter filter;
    private final int[] selectedIndexes;
    private final String separator;

    /**
     * Crea un nuevo trabajador para procesar un lote de líneas CSV.
     *
     * @param batchNumber     número identificador del lote (batch).
     * @param lines           lista de líneas de texto CSV a procesar (sin cabecera).
     * @param filter          filtro de filas a aplicar; puede ser {@code null} si no se desea filtrar.
     * @param selectedIndexes arreglo con los índices de columnas que se deben proyectar
     *                        en la salida (basado en {@code columns[]}, 0-based).
     * @param separator       separador usado para dividir las columnas en cada línea
     *                        (por ejemplo, {@code ","}).
     */
    public BatchWorker(int batchNumber,
                       List<String> lines,
                       CsvRowFilter filter,
                       int[] selectedIndexes,
                       String separator) {
        this.batchNumber = batchNumber;
        this.lines = lines;
        this.filter = filter;
        this.selectedIndexes = selectedIndexes;
        this.separator = separator;
    }

    /**
     * Procesa el lote de líneas asociado a este trabajador.
     *
     * <p>
     * El flujo es:
     * </p>
     * <ol>
     *     <li>Recorrer cada línea del lote.</li>
     *     <li>Dividirla en columnas usando el separador.</li>
     *     <li>Evaluar el filtro (si existe).</li>
     *     <li>Si pasa el filtro, construir la línea filtrada/proyectada
     *         con {@link CsvUtils#buildFilteredLine(String[], int[], String)}.</li>
     *     <li>Acumular salida, log y estadísticas.</li>
     * </ol>
     *
     * @return un {@link BatchResult} con el texto de salida, log y contadores
     *         de líneas procesadas y con error.
     */
    @Override
    public BatchResult call() {
        StringBuilder outputBuffer = new StringBuilder();
        StringBuilder logBuffer    = new StringBuilder();

        long processedLines = 0L;
        long errorLines     = 0L;

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            try {
                String[] columns = line.split(separator, -1);

                boolean passesFilter = true;
                if (filter != null) {
                    passesFilter = filter.matches(columns);
                }

                if (passesFilter) {
                    String filteredLine = CsvUtils.buildFilteredLine(
                            columns,
                            selectedIndexes,
                            separator
                    );
                    outputBuffer.append(filteredLine).append('\n');
                    processedLines++;
                }
            } catch (Exception ex) {
                errorLines++;
                logBuffer
                        .append("Batch ")
                        .append(batchNumber)
                        .append(" - Error in line: ")
                        .append(ex.getMessage() == null ? "no message" : ex.getMessage())
                        .append(" | Content: ")
                        .append(line)
                        .append('\n');
            }
        }

        return new BatchResult(
                batchNumber,
                outputBuffer.toString(),
                logBuffer.toString(),
                processedLines,
                errorLines
        );
    }
}
