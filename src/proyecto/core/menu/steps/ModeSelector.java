package proyecto.core.menu.steps;

import java.util.Scanner;

/**
 * Utilidad para seleccionar el modo de procesamiento del CSV.
 * <p>
 * Esta clase muestra un pequeño menú en consola donde el usuario puede elegir
 * entre procesamiento secuencial o concurrente (Manager-Worker con hilos).
 * El resultado se devuelve como un entero:
 * <ul>
 *     <li>{@code 1} para procesamiento secuencial.</li>
 *     <li>{@code 2} para procesamiento concurrente.</li>
 * </ul>
 */
public final class ModeSelector {

    /**
     * Constructor privado para evitar la creación de instancias.
     * <p>
     * Esta clase solo expone métodos estáticos.
     */
    private ModeSelector() {
        // Evita la instanciación
    }

    /**
     * Pregunta al usuario qué tipo de procesamiento desea utilizar:
     * secuencial o concurrente.
     * <p>
     * El método muestra un menú en consola y no termina hasta que
     * el usuario introduzca una opción válida ({@code "1"} o {@code "2"}).
     * <p>
     * Retornos:
     * <ul>
     *     <li>{@code 1} si se elige "Secuencial".</li>
     *     <li>{@code 2} si se elige "Concurrente (hilos, Manager-Worker)".</li>
     * </ul>
     *
     * @param scanner instancia de {@link Scanner} usada para leer la entrada
     *                del usuario desde la consola. No debe ser {@code null}.
     * @return {@code 1} para procesamiento secuencial o {@code 2} para
     *         procesamiento concurrente.
     */
    public static int askMode(Scanner scanner) {
        while (true) {
            System.out.println("Seleccione el tipo de procesamiento:");
            System.out.println("  1) Secuencial");
            System.out.println("  2) Concurrente (hilos, Manager-Worker)");
            System.out.println("  3) Concurrente RAPIDO (hilos, Manager-Worker)");
            System.out.print("Opción (1/2/3): ");
            String line = scanner.nextLine().trim();
            switch (line) {
                case "1" -> {
                    return 1;
                }
                case "2" -> {
                    return 2;
                }
                case "3" -> {
                    return 3;
                }
            }
            System.out.println("Opción inválida. Intente de nuevo.");
        }
    }
}
