package proyecto.core.csv.processor.secuential.processor;

import proyecto.core.csv.processor.common.processor.BaseCsvProcessor;
import proyecto.core.csv.processor.common.processor.CsvProcessingContext;
import proyecto.core.csv.processor.common.util.FileValidator;
import proyecto.core.csv.processor.secuential.processor.lines.SequentialLineProcessor;
import proyecto.core.csv.processor.secuential.processor.lines.RowStats;
import proyecto.core.csv.processor.secuential.processor.report.SequentialSummaryReporter;
import proyecto.core.csv.processor.secuential.util.CsvHeaderWriter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Procesador SECUENCIAL de archivos CSV.
 * <p>
 * Esta clase es una implementación concreta que, a partir de las utilidades
 * y contexto proporcionados por {@link BaseCsvProcessor}, realiza el flujo
 * completo de procesamiento secuencial:
 * <ol>
 *     <li>Valida el archivo de entrada ({@link FileValidator}).</li>
 *     <li>Abre los recursos de lectura/escritura (entrada, salida y log).</li>
 *     <li>Lee la cabecera del CSV y construye el {@link CsvProcessingContext}.</li>
 *     <li>Escribe la cabecera filtrada en el archivo de salida
 *         mediante {@link CsvHeaderWriter}.</li>
 *     <li>Procesa todas las líneas de datos delegando en
 *         {@link SequentialLineProcessor}.</li>
 *     <li>Calcula estadísticas de procesamiento ({@link RowStats}).</li>
 *     <li>Imprime un reporte/resumen con {@link SequentialSummaryReporter}.</li>
 * </ol>
 * El separador de columnas se toma de la clase base ({@link BaseCsvProcessor}),
 * por defecto {@code ","}.
 */
public class CsvSequentialProcessorImpl extends BaseCsvProcessor {

    /**
     * Componente responsable de procesar las líneas de datos una por una.
     */
    private final SequentialLineProcessor lineProcessor;

    /**
     * Crea un procesador secuencial utilizando el separador por defecto
     * definido en {@link BaseCsvProcessor} (normalmente {@code ","}) e
     * inicializa el {@link SequentialLineProcessor}.
     */
    public CsvSequentialProcessorImpl() {
        super(); // usa separador por defecto ","
        this.lineProcessor = new SequentialLineProcessor();
    }

    /**
     * Ejecuta el procesamiento secuencial de un archivo CSV.
     * <p>
     * Flujo principal:
     * <ol>
     *     <li>Valida el archivo de entrada usando
     *         {@link FileValidator#validateInputFile(String)}.</li>
     *     <li>Crea el archivo de salida y el archivo de log.</li>
     *     <li>Toma una marca de tiempo de inicio con {@link System#nanoTime()}.</li>
     *     <li>Abre:
     *         <ul>
     *             <li>Un {@link BufferedReader} para leer el CSV de entrada.</li>
     *             <li>Un {@link BufferedWriter} para escribir el CSV de salida.</li>
     *             <li>Un {@link BufferedWriter} para el archivo de log de errores.</li>
     *         </ul>
     *     </li>
     *     <li>Lee la cabecera:
     *         <ul>
     *             <li>Si no existe (archivo vacío), se informa por {@code stderr} y se termina.</li>
     *         </ul>
     *     </li>
     *     <li>Construye el contexto de procesamiento
     *         ({@link CsvProcessingContext}) llamando a
     *         {@link #buildContext(String, String, String)} de la clase base.</li>
     *     <li>Escribe la cabecera filtrada en el archivo de salida con
     *         {@link CsvHeaderWriter#writeFilteredHeader(BufferedWriter, CsvProcessingContext, String)}.</li>
     *     <li>Procesa todas las líneas de datos con
     *         {@link SequentialLineProcessor#processLines(BufferedReader, BufferedWriter, BufferedWriter, CsvProcessingContext, String)},
     *         obteniendo un {@link RowStats} con las estadísticas.</li>
     *     <li>Toma la marca de tiempo de fin.</li>
     *     <li>Genera el reporte final con
     *         {@link SequentialSummaryReporter#report(File, File, File, RowStats, long, long)}.</li>
     * </ol>
     * Si ocurre un {@link IOException} en cualquier parte del procesamiento,
     * se informa el mensaje de error por la salida de error estándar.
     *
     * @param inputPath        ruta del archivo CSV de entrada.
     * @param outputPath       ruta del archivo CSV de salida donde se escribirá
     *                         la versión filtrada/seleccionada.
     * @param columnsSpec      especificación de columnas a seleccionar. Puede ser:
     *                         <ul>
     *                             <li>{@code "*"} para indicar todas las columnas.</li>
     *                             <li>Una lista separada por comas con índices de columna
     *                                 (por ejemplo, {@code "1,3,5"}), según lo interprete
     *                                 {@link BaseCsvProcessor#buildContext(String, String, String)}.</li>
     *                         </ul>
     * @param filterExpression expresión textual que representa el filtro sobre las filas
     *                         (por ejemplo, {@code "GPA >= 9 AND Age >= 18"}), o
     *                         {@code null} si no se aplica ningún filtro.
     */
    public void process(String inputPath,
                        String outputPath,
                        String columnsSpec,
                        String filterExpression) {

        File inputFile = FileValidator.validateInputFile(inputPath);
        if (inputFile == null) {
            return;
        }

        File outputFile = new File(outputPath);
        File logFile = new File(outputPath + ".log");

        long start = System.nanoTime();

        try (BufferedReader br = new BufferedReader(new FileReader(inputFile));
             BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile));
             BufferedWriter log = new BufferedWriter(new FileWriter(logFile))) {

            String header = br.readLine();
            if (header == null) {
                System.err.println("Archivo vacío: " + inputFile.getAbsolutePath());
                return;
            }

            // 1) Construir contexto
            CsvProcessingContext ctx = buildContext(header, columnsSpec, filterExpression);

            // 2) Escribir cabecera filtrada
            CsvHeaderWriter.writeFilteredHeader(bw, ctx, separator);

            // 3) Procesar líneas (delegado a otra clase)
            RowStats stats = lineProcessor.processLines(
                    br,
                    bw,
                    log,
                    ctx,
                    separator
            );

            long end = System.nanoTime();

            // 4) Reporte final (delegado)
            SequentialSummaryReporter.report(
                    inputFile,
                    outputFile,
                    logFile,
                    stats,
                    start,
                    end
            );

        } catch (IOException e) {
            System.err.println("Error al procesar archivo: " + e.getMessage());
        }
    }

}
