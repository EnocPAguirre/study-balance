package proyecto.core.menu.util;


/**
 * Utilidad para imprimir las columnas detectadas en el CSV.
 */
public final class HeaderPrinter {

    private HeaderPrinter() {
        // evitar instanciación
    }
    /**
     * Imprime en consola las columnas de la cabecera junto con su índice.
     * <p>
     * Suele usarse después de leer la primera línea del CSV para que el usuario
     * pueda ver qué columnas existen y, con base en ello, elegir cuáles quiere
     * procesar (por número o por nombre).
     *
     * @param headerCols arreglo con los nombres de las columnas tal como aparecen
     *                   en la cabecera del archivo CSV.
     */
    public static void printHeaderColumns(String[] headerCols) {
        System.out.println();
        System.out.println("Columnas detectadas en el archivo:");
        for (int i = 0; i < headerCols.length; i++) {
            System.out.println("  " + (i + 1) + ") " + headerCols[i]);
        }
        System.out.println();
    }
}
