package proyecto.core.menu.util;



import proyecto.core.csv.processor.common.config.CsvProcessorConfig;
import proyecto.core.csv.processor.common.util.CsvUtils;
import proyecto.core.csv.processor.concurrent.memory.CsvConcurrentProcessorInMemory;
import proyecto.core.csv.processor.concurrent.parts.CsvConcurrentProcessorInParts;
import proyecto.core.csv.processor.secuential.CsvSequentialProcessor;
import proyecto.core.menu.steps.*;

import java.util.Scanner;

/**
 * Ejecuta una operación completa de procesamiento CSV:
 * seleccionar modo, archivo, columnas, filtro y lanzar el procesador.
 */
public final class CsvOperationRunner {

    private static final String SEPARATOR = ",";

    private CsvOperationRunner() {
    }
    /**
     * Ejecuta una corrida completa del flujo de procesamiento del CSV.
     * <p>
     * Este método:
     * <ol>
     *     <li>Pide al usuario el modo de procesamiento (secuencial o concurrente).</li>
     *     <li>Solicita la ruta del archivo de entrada y lee la cabecera.</li>
     *     <li>Muestra las columnas disponibles y permite elegir cuáles usar.</li>
     *     <li>Opcionalmente construye una expresión de filtro sobre las filas.</li>
     *     <li>Pregunta la ruta de salida y, en modo concurrente, el número de partes/hilos.</li>
     *     <li>Construye un {@code CsvProcessorConfig} y delega el procesamiento
     *         al procesador correspondiente.</li>
     * </ol>
     *
     * @param scanner {@link java.util.Scanner} utilizado para leer la interacción
     *                del usuario desde la entrada estándar.
     */
    public static void runOnce(Scanner scanner) {
        int mode = ModeSelector.askMode(scanner);

        String inputPath = FilePathPrompter.askInputPath(scanner, "students.csv");

        String headerLine = CsvHeaderReader.readHeaderLine(inputPath);
        if (headerLine == null) {
            System.out.println("No se pudo leer la cabecera del archivo. Volviendo al inicio.");
            return;
        }

        String[] headerCols = CsvUtils.splitColumns(headerLine, SEPARATOR);
        HeaderPrinter.printHeaderColumns(headerCols);

        String columnsSpec = ColumnSelectionPrompter.askColumnsSpec(scanner, headerCols.length);

        String filterExpression = FilterPrompter.askFilterExpression(scanner, headerCols);

        String outputPath = FilePathPrompter.askOutputPath(scanner, "output.csv");

        Integer parts = null;
        if (mode == 2 || mode == 3) {
            parts = ConcurrencyPrompter.askNumParts(scanner);
        }

        CsvProcessorConfig config =
                new CsvProcessorConfig(inputPath, outputPath, columnsSpec, filterExpression, parts);

        if (mode == 1) {
            System.out.println();
            System.out.println("=== Procesamiento SECUENCIAL ===");
            new CsvSequentialProcessor().process(config);
        } else if (mode == 2) {
            System.out.println();
            System.out.println("=== Procesamiento CONCURRENTE (Manager-Worker) ===");
            new CsvConcurrentProcessorInParts().process(config);
        } else {
            System.out.println();
            System.out.println("=== Procesamiento CONCURRENTE RAPIDO (Manager-Worker) ===");
            new CsvConcurrentProcessorInMemory().process(config);
        }

        System.out.println();
        System.out.println(">>> Operación terminada. El archivo de salida ya está listo.");
    }
}
