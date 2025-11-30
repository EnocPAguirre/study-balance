package proyecto.core.csv.processor.concurrent.manager.execution;

import proyecto.core.csv.processor.common.processor.CsvProcessingContext;
import proyecto.core.csv.processor.concurrent.worker.CsvWorker;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Procesa en paralelo las partes de un archivo CSV previamente dividido.
 * <p>
 * Esta clase actúa como un orquestador de las tareas concurrentes:
 * <ul>
 *     <li>Recibe una lista de archivos de entrada (partes del CSV original).</li>
 *     <li>Crea un {@link CsvWorker} por cada parte.</li>
 *     <li>Ejecuta cada {@link CsvWorker} en un hilo virtual utilizando
 *         un {@link ExecutorService} obtenido de
 *         {@link Executors#newVirtualThreadPerTaskExecutor()}.</li>
 *     <li>Espera a que todas las tareas terminen.</li>
 *     <li>Devuelve un arreglo con las rutas de los archivos de salida parciales.</li>
 * </ul>
 * El resultado típico es un conjunto de archivos CSV parciales que luego
 * pueden ser combinados por otra fase del pipeline concurrente.
 */
public class ConcurrentPartProcessor {

    /**
     * Crea una nueva instancia de {@code ConcurrentPartProcessor}.
     * <p>
     * Actualmente no requiere configuración adicional; la instancia se utiliza
     * simplemente como contenedor del método {@link #processParts(List, CsvProcessingContext, File, Object, File, String)}.
     */
    public ConcurrentPartProcessor(){}
    /**
     * Procesa concurrentemente cada una de las partes del CSV.
     * <p>
     * Para cada archivo en {@code partFiles}:
     * <ol>
     *     <li>Se crea un archivo de salida parcial en {@code tempDir} con nombre
     *         parecido a {@code "part_i_out.csv"}.</li>
     *     <li>Se crea un {@link CsvWorker} configurado con:
     *         <ul>
     *             <li>Archivo de entrada (parte).</li>
     *             <li>Archivo de salida parcial.</li>
     *             <li>Filtro, índices seleccionados y número total de columnas
     *                 tomados de {@link CsvProcessingContext}.</li>
     *             <li>Separador de columnas.</li>
     *             <li>Archivo de log compartido y un lock de sincronización para
     *                 escribir de forma segura en el log.</li>
     *         </ul>
     *     </li>
     *     <li>Se envía el worker al {@link ExecutorService} basado en hilos
     *         virtuales.</li>
     * </ol>
     * Después de enviar todas las tareas, el método:
     * <ol>
     *     <li>Espera a que cada {@link Future} termine con {@link Future#get()}.</li>
     *     <li>Cierra el {@link ExecutorService} con {@link ExecutorService#shutdown()}.</li>
     *     <li>Devuelve el arreglo de archivos de salida parciales.</li>
     * </ol>
     *
     * @param partFiles lista de archivos de entrada, cada uno representando una parte
     *                  del CSV original ya dividido.
     * @param ctx       contexto de procesamiento que contiene:
     *                  filtro compilado (si existe), índices de columnas seleccionadas
     *                  y número total de columnas esperadas.
     * @param logFile   archivo de log compartido donde los workers registran errores
     *                  o inconsistencias en las filas.
     * @param logLock   objeto de sincronización que se usa como lock para acceder
     *                  concurrentemente al {@code logFile}.
     * @param tempDir   directorio temporal donde se crearán los archivos de salida
     *                  parciales (uno por cada parte).
     * @param separator separador de columnas del CSV (por ejemplo, {@code ","}).
     * @return un arreglo de {@link File} con los archivos de salida parciales generados,
     *         en el mismo orden que {@code partFiles}.
     * @throws Exception si ocurre un error durante la ejecución de las tareas (por ejemplo,
     *                   si alguna tarea lanza una excepción al procesar su parte).
     */
    public File[] processParts(List<File> partFiles,
                               CsvProcessingContext ctx,
                               File logFile,
                               Object logLock,
                               File tempDir,
                               String separator) throws Exception {

        File[] partialOutputs = new File[partFiles.size()];

        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        List<Future<?>> futures = new ArrayList<>(partFiles.size());

        try {
            for (int i = 0; i < partFiles.size(); i++) {
                File partInput = partFiles.get(i);
                File partOutput = new File(tempDir, "part_" + i + "_out.csv");
                partialOutputs[i] = partOutput;

                CsvWorker worker = new CsvWorker(
                        partInput,
                        partOutput,
                        ctx.filter(),
                        ctx.selectedIndexes(),
                        ctx.totalColumns(),
                        separator,
                        logFile,
                        logLock
                );

                futures.add(executor.submit(worker));
            }

            for (Future<?> future : futures) {
                future.get();
            }
        } finally {
            executor.shutdown();
        }

        return partialOutputs;
    }
}
