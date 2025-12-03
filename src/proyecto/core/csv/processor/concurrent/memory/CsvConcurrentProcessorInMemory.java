package proyecto.core.csv.processor.concurrent.memory;

import proyecto.core.csv.processor.common.config.CsvProcessorConfig;
import proyecto.core.csv.processor.common.processor.BaseCsvProcessor;
import proyecto.core.csv.processor.concurrent.memory.manager.CsvManagerInMemory;

/**
 * Fachada de alto nivel para el procesamiento CONCURRENTE EN MEMORIA
 * de archivos CSV.
 *
 * <p>
 * Esta clase expone un método sencillo {@link #process(CsvProcessorConfig)}
 * que recibe una configuración de alto nivel ({@link CsvProcessorConfig}) y
 * delega el trabajo real al gestor {@link CsvManagerInMemory}, el cual:
 * </p>
 * <ul>
 *     <li>Valida archivos de entrada y salida.</li>
 *     <li>Prepara el contexto de procesamiento.</li>
 *     <li>Divide el CSV en lotes de líneas.</li>
 *     <li>Lanza tareas concurrentes para cada lote.</li>
 *     <li>Consolida la salida y genera log y resumen.</li>
 * </ul>
 *
 * <p>
 * Extiende {@link BaseCsvProcessor} para reutilizar la configuración común,
 * como el separador de columnas.
 * </p>
 */
public final class CsvConcurrentProcessorInMemory extends BaseCsvProcessor {

    /**
     * Gestor interno que implementa el flujo completo de procesamiento
     * concurrente en memoria.
     */
    private final CsvManagerInMemory manager;

    /**
     * Crea una nueva instancia del procesador concurrente en memoria.
     *
     * <p>
     * Inicializa la infraestructura base definida en {@link BaseCsvProcessor}
     * y crea la instancia de {@link CsvManagerInMemory} que realizará
     * el trabajo pesado.
     * </p>
     */
    public CsvConcurrentProcessorInMemory() {
        super();
        this.manager = new CsvManagerInMemory();
    }

    /**
     * Ejecuta el procesamiento concurrente EN MEMORIA usando la
     * configuración proporcionada.
     *
     * <p>
     * A partir de la {@link CsvProcessorConfig} se obtienen:
     * ruta de entrada, ruta de salida, especificación de columnas,
     * expresión de filtro y número de partes/hilos deseado.
     * Estos parámetros se delegan al {@link CsvManagerInMemory},
     * que orquesta todo el flujo concurrente.
     * </p>
     *
     * @param config configuración de alto nivel del procesamiento CSV;
     *               no debe ser {@code null}.
     * @throws IllegalArgumentException si {@code config} es {@code null}.
     */
    public void process(CsvProcessorConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("config must not be null");
        }

        manager.processConcurrentInMemory(
                config.getInputPath(),
                config.getOutputPath(),
                config.getColumnsSpec(),
                config.getFilterExpression(),
                config.getParts()
        );
    }
}
