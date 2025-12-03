package proyecto.core.csv.processor.concurrent.memory.manager;

import proyecto.core.csv.processor.common.processor.BaseCsvProcessor;
import proyecto.core.csv.processor.common.processor.CsvProcessingContext;
import proyecto.core.csv.processor.common.util.CsvUtils;
import proyecto.core.csv.processor.common.util.SummaryReporter;
import proyecto.core.csv.processor.concurrent.memory.manager.execution.ConcurrentBatchProcessor;
import proyecto.core.csv.processor.concurrent.memory.util.BatchResult;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Gestor principal para el procesamiento CONCURRENTE EN MEMORIA
 * de archivos CSV.
 *
 * <p>
 * Esta clase:
 * </p>
 * <ul>
 *     <li>Valida archivos de entrada y salida.</li>
 *     <li>Prepara el contexto de procesamiento ({@link CsvProcessingContext}).</li>
 *     <li>Lee el CSV por lotes de líneas.</li>
 *     <li>Envía cada lote a un {@link ConcurrentBatchProcessor} respaldado por
 *         un pool de hilos virtuales.</li>
 *     <li>Recoge los resultados de cada lote ({@link BatchResult}) y escribe
 *         la salida y el log.</li>
 *     <li>Genera un resumen final mediante {@link SummaryReporter}.</li>
 * </ul>
 *
 * <p>
 * Extiende {@link BaseCsvProcessor} para reutilizar configuración común
 * (por ejemplo, el separador de columnas).
 * </p>
 */
public class CsvManagerInMemory extends BaseCsvProcessor {

    /** Tamaño del buffer de lectura (bytes). */
    private static final int READ_BUFFER_SIZE   = 1024 * 1024;

    /** Tamaño del buffer de escritura para la salida (bytes). */
    private static final int WRITE_BUFFER_SIZE  = 1024 * 1024;

    /** Tamaño del buffer de escritura para el log (bytes). */
    private static final int LOG_BUFFER_SIZE    = 64  * 1024;

    /**
     * Número de líneas por lote.
     * Ajustar según tamaño del archivo y memoria disponible.
     */
    private static final int BATCH_LINE_SIZE = 10_000;

    /** Componente que se encarga de enviar lotes al executor y registrar sus futures. */
    private final ConcurrentBatchProcessor partBatchProcessor;

    /**
     * Crea un nuevo gestor para procesamiento concurrente en memoria.
     *
     * <p>
     * Inicializa la infraestructura base definida en {@link BaseCsvProcessor}
     * y la instancia de {@link ConcurrentBatchProcessor}.
     * </p>
     */
    public CsvManagerInMemory() {
        super();
        this.partBatchProcessor = new ConcurrentBatchProcessor();
    }

    /**
     * Ejecuta todo el flujo de procesamiento concurrente EN MEMORIA
     * para un archivo CSV.
     *
     * <p>
     * El flujo general es:
     * </p>
     * <ol>
     *     <li>Validar archivo de entrada.</li>
     *     <li>Determinar número de hilos/partes a usar.</li>
     *     <li>Abrir lectores/escritores (entrada, salida, log).</li>
     *     <li>Leer cabecera, resolver columnas seleccionadas y construir cabecera filtrada.</li>
     *     <li>Leer el archivo por lotes de {@code BATCH_LINE_SIZE} líneas.</li>
     *     <li>Por cada lote, enviar un {@code BatchWorker} a través de {@link ConcurrentBatchProcessor}.</li>
     *     <li>Recoger todos los {@link Future} y consolidar salida, log y estadísticas.</li>
     *     <li>Generar un reporte final con {@link SummaryReporter}.</li>
     * </ol>
     *
     * @param inputPath        ruta del archivo CSV de entrada.
     * @param outputPath       ruta del archivo CSV de salida (se sobreescribe si existe).
     * @param columnsSpec      especificación de columnas a seleccionar
     *                         (por ejemplo, {@code "*"} o una lista {@code "1,3,5"}).
     * @param filterExpression expresión de filtro a aplicar sobre las filas
     *                         (puede ser {@code null} o vacía para no filtrar).
     * @param partsOpt         número de partes/hilos deseado por el usuario;
     *                         si es {@code null} o menor o igual que 0, se usan los CPUs disponibles.
     */
    public void processConcurrentInMemory(
            String inputPath,
            String outputPath,
            String columnsSpec,
            String filterExpression,
            Integer partsOpt
    ) {

        File inputFile = new File(inputPath);
        if (!inputFile.exists() || !inputFile.isFile()) {
            System.err.println("El archivo de entrada no existe: " + inputFile.getAbsolutePath());
            return;
        }

        File outputFile = new File(outputPath);
        File logFile    = new File(outputFile + ".log");

        // 1) Calcular CPUs y número de hilos/partes
        int numCPUs = Runtime.getRuntime().availableProcessors();
        if (numCPUs <= 0) {
            numCPUs = 1;
        }

        Integer userParts = partsOpt;
        int numParts;
        if (userParts == null || userParts <= 0) {
            numParts = numCPUs;
            System.out.println("No se indicó número de partes/hilos. Se usarán los CPUs disponibles.");
        } else {
            numParts = userParts;
            System.out.println("Número de partes/hilos indicado por el usuario: " + numParts);
        }

        System.out.println("CPUs disponibles                            : " + numCPUs);
        System.out.println("Grado de concurrencia (pool hilos virtuales): " + numParts);
        System.out.println("Tamaño de lote (líneas por lote)            : " + BATCH_LINE_SIZE);

        ExecutorService executor = Executors.newFixedThreadPool(
                numParts,
                Thread.ofVirtual().factory()
        );

        List<Future<BatchResult>> futures = new ArrayList<>();
        long totalProcessedLines = 0L;
        long totalErrorLines     = 0L;
        long start = System.nanoTime();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(inputFile), StandardCharsets.UTF_8),
                READ_BUFFER_SIZE
        );
             BufferedWriter outputWriter = new BufferedWriter(
                     new OutputStreamWriter(new FileOutputStream(outputFile), StandardCharsets.UTF_8),
                     WRITE_BUFFER_SIZE
             );
             BufferedWriter logWriter = new BufferedWriter(
                     new OutputStreamWriter(new FileOutputStream(logFile), StandardCharsets.UTF_8),
                     LOG_BUFFER_SIZE
             )) {

            // 2) Leer cabecera
            String header = reader.readLine();
            if (header == null) {
                System.err.println("El archivo de entrada está vacío: " + inputFile.getAbsolutePath());
                return;
            }

            String[] headerColumns = header.split(separator, -1);

            // 3) Columnas seleccionadas y cabecera filtrada
            int[] selectedIndexes = CsvUtils.parseColumnSelection(
                    columnsSpec,
                    headerColumns.length
            );
            String filteredHeader = CsvUtils.buildFilteredLine(
                    headerColumns,
                    selectedIndexes,
                    separator
            );

            CsvProcessingContext ctx = buildContext(filteredHeader, columnsSpec, filterExpression);

            // 4) Escribir cabecera filtrada
            outputWriter.write(filteredHeader);
            outputWriter.newLine();

            // 5) Leer el archivo por lotes y enviar Workers
            List<String> currentBatch = new ArrayList<>(BATCH_LINE_SIZE);
            int batchNumber = 0;

            String line;
            while ((line = reader.readLine()) != null) {
                currentBatch.add(line);

                if (currentBatch.size() >= BATCH_LINE_SIZE) {
                    partBatchProcessor.processBatch(
                            executor,
                            futures,
                            batchNumber,
                            ctx.filter(),
                            currentBatch,
                            selectedIndexes,
                            separator
                    );
                    currentBatch = new ArrayList<>(BATCH_LINE_SIZE);
                    batchNumber++;
                }
            }

            // Último lote (si quedó algo)
            if (!currentBatch.isEmpty()) {
                partBatchProcessor.processBatch(
                        executor,
                        futures,
                        batchNumber,
                        ctx.filter(),
                        currentBatch,
                        selectedIndexes,
                        separator
                );
            }

            // Ya no se aceptan más tareas
            executor.shutdown();

            // 6) Recoger resultados de todos los Workers
            for (Future<BatchResult> future : futures) {
                BatchResult result = future.get(); // espera a que termine la tarea

                if (result == null) {
                    continue;
                }

                // Acumular estadísticas
                totalProcessedLines += result.getProcessedLines();
                totalErrorLines     += result.getErrorLines();

                // Escribir salida del lote
                String outputText = result.getOutputText();
                if (outputText != null && !outputText.isEmpty()) {
                    outputWriter.write(outputText);
                }

                // Escribir log del lote
                String logText = result.getLogText();
                if (logText != null && !logText.isEmpty()) {
                    logWriter.write(logText);
                }
            }

            long end = System.nanoTime();

            SummaryReporter.report(
                    "CONCURRENTE EN MEMORIA",
                    inputFile,
                    outputFile,
                    logFile,
                    start,
                    end
            );

        } catch (IOException e) {
            throw new RuntimeException("Error de E/S en procesamiento concurrente en memoria: " + e.getMessage(), e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Hilo interrumpido mientras se esperaban resultados: " + e.getMessage(), e);
        } catch (ExecutionException e) {
            throw new RuntimeException("Error en una tarea de procesamiento: " + e.getCause(), e);
        } finally {
            if (executor != null && !executor.isShutdown()) {
                executor.shutdownNow();
            }
        }
    }
}
