package proyecto.core.menu.steps;

import java.util.Scanner;

/**
 * Utilidad para solicitar al usuario qué columnas del CSV desea procesar.
 * <p>
 * Esta clase presenta un pequeño menú en consola donde el usuario puede elegir:
 * <ul>
 *     <li>Usar todas las columnas del archivo CSV.</li>
 *     <li>Especificar un subconjunto de columnas mediante sus índices.</li>
 * </ul>
 * El resultado se devuelve como una cadena:
 * <ul>
 *     <li>{@code "*"} si se usan todas las columnas.</li>
 *     <li>Una lista separada por comas con los índices de las columnas seleccionadas
 *         (por ejemplo: {@code "2,7,13"}).</li>
 * </ul>
 */
public final class ColumnSelectionPrompter {

    /**
     * Constructor privado para evitar la creación de instancias.
     * <p>
     * Esta clase solo expone métodos estáticos.
     */
    private ColumnSelectionPrompter() {
    }

    /**
     * Pregunta al usuario si desea usar todas las columnas del CSV o solo algunas específicas,
     * y devuelve la especificación elegida.
     * <p>
     * Flujo de interacción:
     * <ol>
     *     <li>Muestra un menú con dos opciones:
     *         <ul>
     *             <li>{@code 1}) Usar todas las columnas.</li>
     *             <li>{@code 2}) Seleccionar columnas específicas.</li>
     *         </ul>
     *     </li>
     *     <li>Si el usuario elige la opción {@code 2}, se le pide que escriba
     *         los números de columna separados por comas (por ejemplo: {@code 2,7,13}).</li>
     *     <li>Si el usuario no ingresa nada cuando se le piden las columnas,
     *         se asume que se usarán todas las columnas y se devuelve {@code "*" }.</li>
     *     <li>Para cualquier otra opción (incluyendo la {@code 1} o entradas no válidas),
     *         se considera que se usarán todas las columnas y se devuelve {@code "*" }.</li>
     * </ol>
     *
     * @param scanner      scanner para leer la entrada del usuario desde la consola.
     *                     No debe ser {@code null}.
     * @param totalColumns número total de columnas del archivo CSV. Actualmente no se utiliza
     *                     para validar la entrada pero puede servir para futuras mejoras
     *                     (por ejemplo, validación de rangos).
     * @return {@code "*"} si se usan todas las columnas o una lista de índices de columnas
     *         separados por comas (por ejemplo: {@code "2,7,13"}) si el usuario eligió columnas específicas.
     */
    public static String askColumnsSpec(Scanner scanner, int totalColumns) {
        System.out.println();
        System.out.println("¿Desea usar TODAS las columnas o seleccionar algunas?");
        System.out.println("  1) Todas las columnas");
        System.out.println("  2) Seleccionar columnas específicas");
        System.out.print("Opción (1/2): ");
        String option = scanner.nextLine().trim();

        if ("2".equals(option)) {
            System.out.println("Escriba los números de columna separados por coma (ejemplo: 2,7,13).");
            System.out.print("Columnas: ");
            String cols = scanner.nextLine().trim();
            if (cols.isEmpty()) {
                System.out.println("No se ingresó nada, se usarán todas las columnas.");
                return "*";
            }
            return cols;
        } else {
            System.out.println("Se usarán TODAS las columnas.");
            return "*";
        }
    }
}
