package proyecto.core.menu;

import proyecto.core.csv.processor.common.config.CsvProcessorConfig;
import proyecto.core.csv.processor.concurrent.parts.CsvConcurrentProcessorInParts;
import proyecto.core.csv.processor.secuential.CsvSequentialProcessor;
import proyecto.core.csv.processor.common.util.CsvUtils;
import proyecto.core.menu.util.CsvOperationRunner;

import java.util.Scanner;

/**
 * Menú principal de la aplicación de procesamiento de archivos CSV.
 * <p>
 * Esta clase:
 * <ul>
 *     <li>Muestra el título de la aplicación.</li>
 *     <li>Ejecuta el flujo de procesamiento una vez por iteración
 *         mediante {@link CsvOperationRunner#runOnce(Scanner)}.</li>
 *     <li>Pregunta al usuario si desea continuar usando
 *     <li>Finaliza cuando el usuario decide no continuar.</li>
 * </ul>
 * Aunque importa varias clases de procesamiento
 * ({@link CsvSequentialProcessor}, {@link CsvConcurrentProcessorInParts},
 * {@link CsvProcessorConfig}, {@link CsvUtils}, etc.), la lógica
 * concreta de procesamiento se delega a las clases utilitarias del
 * paquete {@code proyecto.core.menu.util}.
 */
public class CsvMenu {

    /**
     * Separador por defecto para los archivos CSV manejados por el sistema.
     * <p>
     * Actualmente no se usa directamente en esta clase, pero se mantiene
     * como constante de referencia para futuros ajustes de menú.
     */
    private static final String SEPARATOR = ",";

    /**
     * Lector utilizado para obtener la entrada del usuario desde la consola.
     */
    private final Scanner scanner;

    /**
     * Crea una nueva instancia del menú principal.
     *
     * @param scanner instancia de {@link Scanner} usada para leer la entrada
     *                del usuario desde la consola. No debe ser {@code null}
     *                y su ciclo de vida es gestionado por la clase que
     *                invoca al menú (por ejemplo, {@code MainApp}).
     */
    public CsvMenu(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Inicia el bucle principal del menú.
     * <p>
     * El flujo es:
     * <ol>
     *     <li>Muestra el encabezado de la aplicación.</li>
     *     <li>Mientras el usuario desee continuar:
     *     <ol>
     *         <li>Ejecuta una operación de procesamiento de CSV
     *             con {@link CsvOperationRunner#runOnce(Scanner)}.</li>
     *
     *     </ol>
     *     </li>
     *     <li>Muestra un mensaje de despedida.</li>
     * </ol>
     */
    public void run() {
        System.out.println("========================================");
        System.out.println("     Procesador de CSV (Proyecto Final) ");
        System.out.println("========================================");
        System.out.println();

        CsvOperationRunner.runOnce(scanner);


        System.out.println("Saliendo de la aplicación. ¡Hasta luego!");
    }

}
