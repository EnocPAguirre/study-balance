package proyecto.core.csv.processor.concurrent.worker;

import proyecto.core.csv.filters.CsvRowFilter;
import proyecto.core.csv.processor.common.util.CsvUtils;
import proyecto.core.csv.processor.concurrent.util.CsvLog;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Worker que procesa un subarchivo CSV en el contexto de procesamiento concurrente.
 * <p>
 * Cada instancia de esta clase:
 * <ul>
 *     <li>Lee un archivo de entrada que contiene solo filas de datos (sin cabecera).</li>
 *     <li>Valida que cada fila tenga el número correcto de columnas.</li>
 *     <li>Aplica un filtro opcional {@link CsvRowFilter} sobre las columnas.</li>
 *     <li>Construye una línea con solo las columnas seleccionadas y la escribe
 *         en un archivo de salida parcial.</li>
 *     <li>Registra en un archivo de log los errores de formato o fallos de I/O,
 *         usando {@link CsvLog} de forma segura para hilos.</li>
 * </ul>
 * No escribe cabecera en el archivo de salida, únicamente filas filtradas.
 */
public class CsvWorker implements Runnable {

    /**
     * Archivo de entrada que representa una parte del CSV original (solo datos).
     */
    private final File inputPartFile;

    /**
     * Archivo de salida parcial donde se escribirán las filas válidas y filtradas.
     */
    private final File outputPartFile;

    /**
     * Filtro aplicado a cada fila. Si es {@code null}, no se aplica filtro.
     */
    private final CsvRowFilter filter;

    /**
     * Índices de las columnas que deben incluirse en la salida.
     */
    private final int[] selectedIndexes;

    /**
     * Número total de columnas esperado por fila (tomado de la cabecera original).
     */
    private final int totalColumns;

    /**
     * Separador de columnas del CSV (por ejemplo, {@code ","}).
     */
    private final String separator;

    /**
     * Archivo de log compartido donde se registran errores.
     */
    private final File logFile;

    /**
     * Objeto lock usado para sincronizar el acceso concurrente al archivo de log.
     */
    private final Object logLock;

    /**
     * Crea un nuevo worker para procesar un subarchivo CSV.
     *
     * @param inputPartFile  archivo de entrada con las filas de datos de una parte del CSV.
     * @param outputPartFile archivo de salida parcial donde se escribirán las filas válidas/filtradas.
     * @param filter         filtro de filas a aplicar. Puede ser {@code null} si no se desea filtrar.
     * @param selectedIndexes índices de columnas que deben incluirse en la salida.
     * @param totalColumns   número total de columnas esperadas por fila.
     * @param separator      separador de columnas del CSV.
     * @param logFile        archivo de log compartido para registrar errores.
     * @param logLock        objeto de sincronización para acceso seguro al log desde varios hilos.
     */
    public CsvWorker(File inputPartFile,
                     File outputPartFile,
                     CsvRowFilter filter,
                     int[] selectedIndexes,
                     int totalColumns,
                     String separator,
                     File logFile,
                     Object logLock) {
        this.inputPartFile = inputPartFile;
        this.outputPartFile = outputPartFile;
        this.filter = filter;
        this.selectedIndexes = selectedIndexes;
        this.totalColumns = totalColumns;
        this.separator = separator;
        this.logFile = logFile;
        this.logLock = logLock;
    }

    /**
     * Ejecuta el procesamiento de la parte del CSV.
     * <p>
     * Flujo:
     * <ol>
     *     <li>Abre el archivo de entrada ({@code inputPartFile}) para lectura.</li>
     *     <li>Abre el archivo de salida ({@code outputPartFile}) para escritura (sobrescribiendo si existe).</li>
     *     <li>Lee cada línea:
     *         <ul>
     *             <li>Ignora líneas vacías.</li>
     *             <li>Divide la línea en columnas usando {@link CsvUtils#splitColumns(String, String)}.</li>
     *             <li>Si el número de columnas difiere de {@code totalColumns}, registra un error en el log y continúa.</li>
     *             <li>Si existe un {@code filter} y la fila no lo cumple, se omite.</li>
     *             <li>Construye una línea con las columnas seleccionadas mediante
     *                 {@link CsvUtils#buildFilteredLine(String[], int[], String)} y la escribe en la salida.</li>
     *         </ul>
     *     </li>
     *     <li>Si ocurre alguna {@link IOException}, se registra un mensaje de error en el log
     *         usando {@link CsvLog#logError(File, Object, String)}.</li>
     * </ol>
     */
    @Override
    public void run() {
        try (BufferedReader br = new BufferedReader(new FileReader(inputPartFile));
             BufferedWriter bw = new BufferedWriter(new FileWriter(outputPartFile))) {

            String line;
            int lineNumber = 0;
            while ((line = br.readLine()) != null) {
                lineNumber++;
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] cols = CsvUtils.splitColumns(line, separator);
                if (cols.length != totalColumns) {
                    CsvLog.logError(
                            logFile,
                            logLock,
                            "Archivo: " + inputPartFile.getName()
                                    + " | Línea " + lineNumber
                                    + " columnas inválidas: " + cols.length
                                    + " (esperadas " + totalColumns + ")"
                    );
                    continue;
                }
                if (filter != null && !filter.matches(cols)) {
                    continue;
                }
                String outLine = CsvUtils.buildFilteredLine(cols, selectedIndexes, separator);
                bw.write(outLine);
                bw.newLine();
            }
        } catch (IOException e) {
            CsvLog.logError(
                    logFile,
                    logLock,
                    "Error en worker con archivo " + inputPartFile.getName() + ": " + e.getMessage()
            );
        }
    }
}
