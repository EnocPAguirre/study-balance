package proyecto.app;

import proyecto.core.menu.CsvMenu;
import java.util.Scanner;

/**
 * Punto de entrada de la aplicación de consola para el procesamiento de archivos CSV.
 * <p>
 * Esta clase inicializa el {@link CsvMenu} y delega en él todo el flujo
 * de interacción con el usuario.
 */
public class MainApp {

    /**
     * Constructor privado para evitar la instanciación de esta clase.
     * <p>
     * {@code MainApp} solo se utiliza como contenedor del método
     * {@link #main(String[])} y no debe ser instanciada.
     */
    private MainApp() {
    }

    /**
     * Método principal de la aplicación.
     * <p>
     * Crea un {@link Scanner} para leer desde la entrada estándar,
     * inicializa el {@link CsvMenu} y ejecuta el ciclo principal del menú.
     *
     * @param args argumentos de línea de comandos (no utilizados actualmente).
     */
    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            CsvMenu menu = new CsvMenu(scanner);
            menu.run();
        }
    }
}
