package proyecto.core.csv.processor.concurrent.util;

/**
 * Utilidad para determinar el número de partes/hilos a utilizar
 * en el procesamiento concurrente de un archivo CSV.
 * <p>
 * Esta clase ofrece una política sencilla:
 * <ul>
 *     <li>Si el usuario no especifica un número de partes (o especifica un valor inválido),
 *         se utiliza el número de CPUs disponibles en la JVM.</li>
 *     <li>Si el usuario proporciona un entero positivo, se usa ese valor directamente.</li>
 * </ul>
 */
public final class NumPartsResolver {

    /**
     * Constructor privado para evitar instanciación.
     * <p>
     * La clase funciona únicamente como contenedor de métodos estáticos.
     */
    private NumPartsResolver() {
    }

    /**
     * Resuelve el número de partes a usar para el procesamiento concurrente.
     * <p>
     * Regla de decisión:
     * <ul>
     *     <li>Si {@code partsOpt} es {@code null} o menor o igual que cero,
     *         se devuelve el número de procesadores disponibles en la JVM,
     *         obtenido mediante {@link Runtime#getRuntime()} y
     *         {@link Runtime#availableProcessors()}.</li>
     *     <li>En caso contrario, se devuelve {@code partsOpt} tal cual.</li>
     * </ul>
     *
     * @param partsOpt número de partes deseado, o {@code null} si se quiere
     *                 que el sistema decida automáticamente.
     * @return el número de partes que se deben utilizar para el procesamiento
     *         concurrente.
     */
    public static int resolveNumParts(Integer partsOpt) {
        int cpus = Runtime.getRuntime().availableProcessors();
        return (partsOpt == null || partsOpt <= 0) ? cpus : partsOpt;
    }
}
