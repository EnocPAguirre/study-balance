package proyecto.core.menu.util;

import java.util.Scanner;

/**
 * Utilidades para interacción básica por consola.
 */
public final class ConsolePrompts {

    private ConsolePrompts() {
        // evitar instanciación
    }
    /**
     * Pregunta al usuario si desea continuar usando la aplicación.
     * <p>
     * Normalmente se invoca al final de cada ejecución del menú principal para decidir
     * si se procesa otro archivo o si se sale del programa.
     *
     * @param scanner {@link Scanner} utilizado para leer la respuesta del usuario
     *                desde la entrada estándar.
     * @return {@code true} si el usuario indica que desea continuar;
     *         {@code false} si elige salir.
     */
    public static boolean askContinue(Scanner scanner) {
        while (true) {
            System.out.println();
            System.out.print("¿Desea procesar otro archivo? (S/N): ");
            String line = scanner.nextLine().trim();
            if (line.equalsIgnoreCase("S")) {
                return true;
            }
            if (line.equalsIgnoreCase("N")) {
                return false;
            }
            System.out.println("Respuesta inválida. Escriba 'S' o 'N'.");
        }
    }
}
