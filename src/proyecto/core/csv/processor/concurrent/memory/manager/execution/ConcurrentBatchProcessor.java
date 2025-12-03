package proyecto.core.csv.processor.concurrent.memory.manager.execution;

import proyecto.core.csv.filters.CsvRowFilter;
import proyecto.core.csv.processor.concurrent.memory.util.BatchResult;
import proyecto.core.csv.processor.concurrent.memory.worker.BatchWorker;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Componente responsable de orquestar la ejecución concurrente de lotes
 * de líneas CSV.
 *
 * <p>
 * Esta clase se encarga de:
 * crear un {@link BatchWorker} para cada lote y enviarlo a un
 * {@link ExecutorService}, almacenando el {@link Future} resultante en
 * una lista para su posterior consulta y consolidación.
 * </p>
 */
public class ConcurrentBatchProcessor {

    /**
     * Crea una nueva instancia de {@code ConcurrentBatchProcessor}.
     * <p>
     * Actualmente no requiere configuración adicional, pero se deja el
     * constructor explícito para futuras extensiones.
     * </p>
     */
    public ConcurrentBatchProcessor() {
        // Constructor intencionalmente vacío.
    }

    /**
     * Envía un lote de líneas CSV a procesamiento concurrente.
     *
     * @param executor        servicio de ejecución que gestionará el hilo o tarea
     *                        donde correrá el {@link BatchWorker}.
     * @param futures         lista donde se añadirá el {@link Future} asociado
     *                        al lote enviado.
     * @param batchNumber     número identificador del lote (batch).
     * @param filter          filtro de filas a aplicar; puede ser {@code null} si no se desea filtrar.
     * @param batchLines      líneas de texto CSV que conforman el lote (sin cabecera).
     * @param selectedIndexes índices de las columnas a proyectar en la salida
     *                        (arreglo basado en {@code columns[]}, 0-based).
     * @param separator       separador usado para dividir las columnas en cada línea
     *                        (por ejemplo, {@code ","}).
     */
    public void processBatch(ExecutorService executor,
                             List<Future<BatchResult>> futures,
                             int batchNumber,
                             CsvRowFilter filter,
                             List<String> batchLines,
                             int[] selectedIndexes,
                             String separator) {

        BatchWorker worker = new BatchWorker(
                batchNumber,
                batchLines,
                filter,
                selectedIndexes,
                separator
        );
        Future<BatchResult> future = executor.submit(worker);
        futures.add(future);
    }
}
