package proyecto.core.csv.processor.concurrent.parts;

import proyecto.core.csv.processor.common.processor.BaseCsvProcessor;
import proyecto.core.csv.processor.common.config.CsvProcessorConfig;
import proyecto.core.csv.processor.concurrent.parts.manager.CsvManager;

/**
 * Fachada que expone el procesamiento CONCURRENTE de archivos CSV.
 * <p>
 * Esta clase ofrece una API sencilla hacia el exterior y delega toda la
 * lógica de orquestación concurrente al {@link CsvManager}. De esta forma,
 * otras capas (por ejemplo, el menú) solo necesitan trabajar con
 * {@link CsvProcessorConfig} y no conocer los detalles internos
 * de splitting, workers, merge, etc.
 */
public class CsvConcurrentProcessorInParts extends BaseCsvProcessor {

    /**
     * Componente manager que coordina el pipeline de procesamiento concurrente.
     */
    private final CsvManager manager;

    /**
     * Crea una nueva instancia del procesador concurrente.
     * <p>
     * Invoca el constructor de {@link BaseCsvProcessor} para inicializar
     * la configuración común (por ejemplo, el separador por defecto) y
     * crea internamente un {@link CsvManager} para manejar todo el flujo
     * concurrente.
     */
    public CsvConcurrentProcessorInParts() {
        super();
        this.manager = new CsvManager();
    }

    /**
     * Ejecuta el procesamiento concurrente utilizando una configuración de alto nivel.
     * <p>
     * Este método extrae del {@link CsvProcessorConfig} todos los parámetros
     * necesarios y los delega a
     * {@link CsvManager#processConcurrent(String, String, String, String, Integer)}:
     * <ul>
     *     <li>Ruta del archivo de entrada.</li>
     *     <li>Ruta del archivo de salida.</li>
     *     <li>Especificación de columnas a seleccionar.</li>
     *     <li>Expresión de filtro sobre las filas (opcional).</li>
     *     <li>Número de partes/hilos (opcional, puede ser {@code null}).</li>
     * </ul>
     *
     * @param config configuración del procesamiento CSV, que incluye ruta de entrada,
     *               ruta de salida, columnas, filtro y, opcionalmente, número de partes.
     */
    public void process(CsvProcessorConfig config) {
        manager.processConcurrent(
                config.getInputPath(),
                config.getOutputPath(),
                config.getColumnsSpec(),
                config.getFilterExpression(),
                config.getParts()
        );
    }
}
