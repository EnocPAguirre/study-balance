package proyecto.core.csv.processor.concurrent.manager;

import proyecto.core.csv.processor.common.processor.BaseCsvProcessor;
import proyecto.core.csv.processor.common.processor.CsvProcessingContext;
import proyecto.core.csv.processor.common.util.FileValidator;
import proyecto.core.csv.processor.concurrent.manager.execution.ConcurrentPartProcessor;
import proyecto.core.csv.processor.concurrent.manager.report.ConcurrentSummaryReporter;
import proyecto.core.csv.processor.concurrent.split.CsvSplitResult;
import proyecto.core.csv.processor.concurrent.split.CsvSplitter;
import proyecto.core.csv.processor.concurrent.manager.temp.TempDirectoryFactory;
import proyecto.core.csv.processor.concurrent.util.CleanupDirectory;
import proyecto.core.csv.processor.concurrent.util.CsvOutput;
import proyecto.core.csv.processor.concurrent.util.NumPartsResolver;

import java.io.File;
import java.util.List;

/**
 * Manager principal para el procesamiento CONCURRENTE de archivos CSV.
 * <p>
 * Esta clase orquesta todo el pipeline concurrente:
 * <ol>
 *     <li>Valida el archivo de entrada ({@link FileValidator}).</li>
 *     <li>Prepara el archivo de salida y el archivo de log.</li>
 *     <li>Crea un directorio temporal para almacenar las partes del CSV.</li>
 *     <li>Determina el número de partes/hilos a utilizar
 *         ({@link NumPartsResolver#resolveNumParts(Integer)}).</li>
 *     <li>Divide el archivo de entrada en varias partes con {@link CsvSplitter}.</li>
 *     <li>Construye el {@link CsvProcessingContext} a partir de la cabecera y la configuración
 *         (columnas seleccionadas y filtro).</li>
 *     <li>Lanza el procesamiento concurrente de cada parte mediante
 *         {@link ConcurrentPartProcessor} y {@code CsvWorker}s.</li>
 *     <li>Fusiona los resultados parciales en un archivo final con {@link CsvOutput}.</li>
 *     <li>Imprime un reporte/resumen del procesamiento concurrente
 *         con {@link ConcurrentSummaryReporter}.</li>
 *     <li>Limpia el directorio temporal con {@link CleanupDirectory}.</li>
 * </ol>
 * <p>
 * Hereda de {@link BaseCsvProcessor} para reutilizar lógica común, como la
 * construcción del contexto y el separador de columnas.
 */
public class CsvManager extends BaseCsvProcessor {

    /**
     * Componente encargado de dividir el archivo CSV original en varias partes.
     */
    private final CsvSplitter splitter;

    /**
     * Componente encargado de procesar concurrentemente cada una de las partes.
     */
    private final ConcurrentPartProcessor partProcessor;

    /**
     * Crea un nuevo {@code CsvManager} para procesamiento concurrente.
     * <p>
     * Inicializa:
     * <ul>
     *     <li>La configuración base del procesador mediante {@link BaseCsvProcessor}.</li>
     *     <li>El {@link CsvSplitter} para dividir archivos.</li>
     *     <li>El {@link ConcurrentPartProcessor} para procesar cada parte en paralelo.</li>
     * </ul>
     */
    public CsvManager() {
        super();
        this.splitter = new CsvSplitter();
        this.partProcessor = new ConcurrentPartProcessor();
    }

    /**
     * Ejecuta el procesamiento concurrente de un archivo CSV.
     * <p>
     * Flujo general:
     * <ol>
     *     <li>Valida el archivo de entrada:
     *         <ul>
     *             <li>Si no es válido, el método termina inmediatamente.</li>
     *         </ul>
     *     </li>
     *     <li>Construye las referencias a archivo de salida y archivo de log.</li>
     *     <li>Crea el directorio temporal de partes con
     *         {@link TempDirectoryFactory#createTempDir(File, String)}.</li>
     *     <li>Resuelve el número de partes/hilos a usar con
     *         {@link NumPartsResolver#resolveNumParts(Integer)}.</li>
     *     <li>Toma la marca de tiempo de inicio.</li>
     *     <li>Divide el CSV de entrada en {@code numParts} archivos mediante
     *         {@link CsvSplitter#split(File, File, int)}, obteniendo:
     *         <ul>
     *             <li>La línea de cabecera original.</li>
     *             <li>La lista de archivos de partes.</li>
     *         </ul>
     *     </li>
     *     <li>Construye el {@link CsvProcessingContext} llamando a
     *         {@link #buildContext(String, String, String)} con:
     *         <ul>
     *             <li>La cabecera original.</li>
     *             <li>La especificación de columnas {@code columnsSpec}.</li>
     *             <li>La expresión de filtro {@code filterExpression} (si existe).</li>
     *         </ul>
     *     </li>
     *     <li>Crea un lock para el log compartido y lanza el procesamiento
     *         concurrente de cada parte con
     *         {@link ConcurrentPartProcessor#processParts(List, CsvProcessingContext, File, Object, File, String)}.</li>
     *     <li>Fusiona los resultados parciales en el archivo final de salida con
     *         {@link CsvOutput#writeFinalFile(File, String[], int[], File[], String)}.</li>
     *     <li>Toma la marca de tiempo de fin.</li>
     *     <li>Genera un reporte del procesamiento concurrente mediante
     *         {@link ConcurrentSummaryReporter#report(File, File, File, File, long, long)}.</li>
     *     <li>En el bloque {@code finally}, limpia el directorio temporal con
     *         {@link CleanupDirectory#deleteTemporaryDirectory(File)}.</li>
     * </ol>
     * Si ocurre alguna excepción durante el proceso, se captura y se informa el
     * mensaje de error por la salida de error estándar.
     *
     * @param inputPath        ruta del archivo CSV de entrada a procesar.
     * @param outputPath       ruta del archivo CSV de salida final que se generará.
     * @param columnsSpec      especificación de columnas a seleccionar. Puede ser:
     *                         <ul>
     *                             <li>{@code "*"} para indicar todas las columnas.</li>
     *                             <li>Una lista separada por comas con índices de columna.</li>
     *                         </ul>
     * @param filterExpression expresión de filtro sobre las filas, utilizando nombres de
     *                         columna y operadores (por ejemplo,
     *                         {@code "GPA >= 9 AND Age >= 18"}), o {@code null}
     *                         si no se aplica ningún filtro.
     * @param partsOpt         número de partes/hilos deseados. Si es {@code null} o no es
     *                         válido, {@link NumPartsResolver} determinará un valor
     *                         adecuado (por ejemplo, basado en el número de CPUs).
     */
    public void processConcurrent(String inputPath,
                                  String outputPath,
                                  String columnsSpec,
                                  String filterExpression,
                                  Integer partsOpt) {

        File inputFile = FileValidator.validateInputFile(inputPath);
        if (inputFile == null) {
            return;
        }

        File outputFile = new File(outputPath);
        File logFile = new File(outputPath + ".log");

        File tempDir = TempDirectoryFactory.createTempDir(outputFile, "tmp_parts");
        if (tempDir == null) {
            return;
        }

        int numParts = NumPartsResolver.resolveNumParts(partsOpt);
        long start = System.nanoTime();

        try {
            CsvSplitResult splitResult = splitter.split(inputFile, tempDir, numParts);
            String headerLine = splitResult.getHeaderLine();
            List<File> partFiles = splitResult.getPartFiles();

            CsvProcessingContext ctx = buildContext(headerLine, columnsSpec, filterExpression);

            Object logLock = new Object();
            File[] partialOutputs = partProcessor.processParts(
                    partFiles,
                    ctx,
                    logFile,
                    logLock,
                    tempDir,
                    separator
            );

            CsvOutput.writeFinalFile(
                    outputFile,
                    ctx.headerCols(),
                    ctx.selectedIndexes(),
                    partialOutputs,
                    separator
            );

            long end = System.nanoTime();

            ConcurrentSummaryReporter.report(inputFile, outputFile, logFile, tempDir, start, end);

        } catch (Exception e) {
            System.err.println("Error en procesamiento concurrente: " + e.getMessage());
        } finally {
            CleanupDirectory.deleteTemporaryDirectory(tempDir);
        }
    }

}
