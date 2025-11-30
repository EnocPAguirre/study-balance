package proyecto.core.menu.steps;

import java.util.Scanner;

/**
 * Utilidad para solicitar al usuario el número de partes/hilos
 * a utilizar en el procesamiento concurrente del CSV.
 * <p>
 * Esta clase muestra un mensaje en consola explicando que:
 * <ul>
 *     <li>Si el usuario solo presiona Enter, se usarán tantos hilos
 *         como CPUs disponibles en el sistema.</li>
 *     <li>Si el usuario introduce un número entero positivo, ese valor
 *         se usará como número de partes/hilos.</li>
 *     <li>Si el usuario introduce un valor no válido (no numérico o
 *         menor o igual a cero), se ignora y se usará el valor por
 *         defecto (CPUs disponibles).</li>
 * </ul>
 * El método devuelve {@code null} cuando se desea indicar
 * "usar CPUs disponibles".
 */
public final class ConcurrencyPrompter {

    /**
     * Constructor privado para evitar la creación de instancias.
     * <p>
     * Esta clase solo contiene métodos estáticos.
     */
    private ConcurrencyPrompter() {
    }

    /**
     * Pregunta al usuario cuántas partes/hilos desea utilizar
     * para el procesamiento concurrente.
     * <p>
     * Comportamiento:
     * <ol>
     *     <li>Muestra un mensaje que indica que, si se presiona Enter
     *         sin escribir nada, se usarán tantos hilos como CPUs disponibles.</li>
     *     <li>Si el usuario introduce un valor:
     *         <ul>
     *             <li>Si es un entero positivo, se devuelve ese valor.</li>
     *             <li>Si es inválido (no numérico o ≤ 0), se informa al usuario
     *                 y se devuelve {@code null} para indicar que se usará el
     *                 valor por defecto.</li>
     *         </ul>
     *     </li>
     * </ol>
     *
     * @param scanner instancia de {@link Scanner} usada para leer la entrada
     *                del usuario desde la consola. No debe ser {@code null}.
     * @return un {@link Integer} con el número de partes/hilos deseados,
     *         o {@code null} si se deben usar las CPUs disponibles.
     */
    public static Integer askNumParts(Scanner scanner) {
        System.out.println();
        System.out.println("Procesamiento concurrente:");
        System.out.println("  Si presiona Enter, se usarán tantos hilos como CPUs disponibles.");
        System.out.print("Número de partes/hilos (opcional): ");
        String line = scanner.nextLine().trim();
        if (line.isEmpty()) {
            return null;
        }
        try {
            int value = Integer.parseInt(line);
            if (value <= 0) {
                System.out.println("Valor no válido, se usarán CPUs disponibles.");
                return null;
            }
            return value;
        } catch (NumberFormatException e) {
            System.out.println("Valor no válido, se usarán CPUs disponibles.");
            return null;
        }
    }
}
