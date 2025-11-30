package proyecto.core.csv.processor.secuential;

import proyecto.core.csv.processor.common.processor.BaseCsvProcessor;
import proyecto.core.csv.processor.common.config.CsvProcessorConfig;
import proyecto.core.csv.processor.secuential.processor.CsvSequentialProcessorImpl;

/**
 * Fachada que expone el procesamiento SECUENCIAL de archivos CSV.
 * <p>
 * Esta clase actúa como un punto de entrada simple para el procesamiento
 * secuencial, delegando toda la lógica real a
 * {@link CsvSequentialProcessorImpl}. De esta forma, el resto de la
 * aplicación puede depender de una API más sencilla sin conocer los
 * detalles internos de implementación.
 */
public class CsvSequentialProcessor extends BaseCsvProcessor {

    /**
     * Implementación interna que realiza el procesamiento secuencial real.
     */
    private final CsvSequentialProcessorImpl impl;

    /**
     * Crea una nueva instancia de la fachada para procesamiento secuencial.
     * <p>
     * Invoca al constructor de {@link BaseCsvProcessor} para inicializar
     * cualquier configuración común (por ejemplo, separador por defecto)
     * y crea la instancia interna de {@link CsvSequentialProcessorImpl}.
     */
    public CsvSequentialProcessor() {
        super(); // si BaseCsvProcessor inicializa cosas comunes
        this.impl = new CsvSequentialProcessorImpl();
    }

    /**
     * Ejecuta el procesamiento secuencial usando una configuración de alto nivel.
     * <p>
     * Este método extrae de {@link CsvProcessorConfig} los parámetros necesarios
     * (ruta de entrada, ruta de salida, especificación de columnas y expresión
     * de filtro) y los delega a {@link CsvSequentialProcessorImpl#process(String, String, String, String)}.
     *
     * @param config configuración del procesamiento CSV, que incluye:
     *               <ul>
     *                   <li>Ruta del archivo de entrada.</li>
     *                   <li>Ruta del archivo de salida.</li>
     *                   <li>Especificación de columnas a seleccionar.</li>
     *                   <li>Expresión de filtro sobre filas (opcional).</li>
     *               </ul>
     */
    public void process(CsvProcessorConfig config) {
        impl.process(
                config.getInputPath(),
                config.getOutputPath(),
                config.getColumnsSpec(),
                config.getFilterExpression()
        );
    }

}
