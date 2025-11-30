/**
 * Procesamiento concurrente de archivos CSV usando el patrón Manager-Worker.
 * <p>
 * Este paquete contiene la fachada {@link proyecto.core.csv.processor.concurrent.CsvConcurrentProcessor}
 * y coordina:
 * <ul>
 *     <li>División del archivo de entrada en partes ({@link proyecto.core.csv.processor.concurrent.split.CsvSplitter}).</li>
 *     <li>Ejecución concurrente de workers ({@link proyecto.core.csv.processor.concurrent.worker.CsvWorker}).</li>
 *     <li>Unión de resultados parciales ({@link proyecto.core.csv.processor.concurrent.util.CsvOutput}).</li>
 * </ul>
 */
package proyecto.core.csv.processor.concurrent;
