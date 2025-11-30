package proyecto.core.menu.steps;

import java.util.Scanner;

/**
 * Utilidad para solicitar al usuario una expresión de filtro sobre las filas del CSV.
 * <p>
 * Esta clase guía al usuario para decidir si desea filtrar las filas y, en caso afirmativo,
 * le pide que escriba una expresión utilizando los nombres de las columnas y operadores
 * lógicos sencillos.
 * <p>
 * La expresión de filtro se devuelve como una cadena que luego puede ser interpretada
 * por el motor de filtrado del sistema.
 *
 * Ejemplos de expresiones:
 * <ul>
 *     <li>{@code GPA >= 9}</li>
 *     <li>{@code Age >= 18 AND Stress_Level >= 7}</li>
 *     <li>{@code Has_Anxiety = "Yes" OR Borough_MX = "Coyoacan"}</li>
 * </ul>
 */
public final class FilterPrompter {

    /**
     * Constructor privado para evitar la creación de instancias.
     * <p>
     * Esta clase solo contiene métodos estáticos.
     */
    private FilterPrompter() {
        // Evita la instanciación
    }

    /**
     * Pregunta al usuario si desea aplicar un filtro sobre las filas del CSV y,
     * en caso afirmativo, le solicita que escriba una expresión de filtro.
     * <p>
     * Flujo:
     * <ol>
     *     <li>Pregunta si se desea aplicar filtro (opciones 1 = Sí, 2 = No).</li>
     *     <li>Si el usuario no elige la opción {@code "1"}, se asume que no hay filtro
     *         y se devuelve {@code null}.</li>
     *     <li>Si el usuario elige aplicar filtro, se muestran las instrucciones y
     *         ejemplos de expresiones válidas.</li>
     *     <li>Se lee la expresión escrita por el usuario:
     *         <ul>
     *             <li>Si la expresión está vacía, se informa al usuario y se devuelve {@code null}.</li>
     *             <li>En caso contrario, se devuelve la expresión tal como fue escrita.</li>
     *         </ul>
     *     </li>
     * </ol>
     * La expresión puede usar:
     * <ul>
     *     <li>Operadores de comparación: {@code <}, {@code >}, {@code =}, {@code !=}, {@code <=}, {@code >=}.</li>
     *     <li>Conectores lógicos: {@code AND}, {@code OR}.</li>
     * </ul>
     *
     * @param scanner    instancia de {@link Scanner} utilizada para leer la entrada del usuario
     *                   desde la consola. No debe ser {@code null}.
     * @param headerCols arreglo con los nombres de las columnas del archivo CSV tal como
     *                   aparecen en la cabecera. Actualmente se utiliza solo para mostrar
     *                   contexto al usuario (por ejemplo, en la impresión previa de cabeceras),
     *                   pero podría servir para validaciones futuras del filtro.
     * @return la expresión de filtro introducida por el usuario, o {@code null} si se decide
     *         no aplicar filtro o si no se introduce ninguna expresión.
     */
    public static String askFilterExpression(Scanner scanner, String[] headerCols) {
        System.out.println();
        System.out.println("¿Desea aplicar un filtro sobre las filas?");
        System.out.println("  1) Sí");
        System.out.println("  2) No");
        System.out.print("Opción (1/2): ");
        String option = scanner.nextLine().trim();

        if (!"1".equals(option)) {
            System.out.println("No se aplicará filtro (se incluyen todas las filas).");
            return null;
        }

        System.out.println();
        System.out.println("Escriba una expresión usando los NOMBRES de columna tal como aparecen arriba.");
        System.out.println("Operadores permitidos: <, >, =, !=, <=, >= y conectores AND / OR.");
        System.out.println("Ejemplos:");
        System.out.println("  GPA >= 9");
        System.out.println("  Age >= 18 AND Stress_Level >= 7");
        System.out.println("  Has_Anxiety = \"Yes\" OR Has_Anxiety = \"No\"");
        System.out.println();
        System.out.print("Filtro: ");

        String expr = scanner.nextLine().trim();
        if (expr.isEmpty()) {
            System.out.println("No se ingresó filtro. No se aplicará ningún criterio.");
            return null;
        }

        System.out.println("Filtro construido: " + expr);
        return expr;
    }
}
