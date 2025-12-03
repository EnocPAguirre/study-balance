package proyecto.core.csv.processor.concurrent.parts.split;

import proyecto.core.csv.processor.common.util.CsvUtils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Utilidad para dividir un archivo CSV grande en N subarchivos (solo datos, sin cabecera).
 *
 * <p>
 * Características principales:
 * </p>
 * <ul>
 *     <li>Lee la cabecera una sola vez y la devuelve como parte del resultado.</li>
 *     <li>Cada subarchivo generado <strong>no</strong> incluye la línea de cabecera.</li>
 *     <li>Solo se escriben filas de datos no vacías.</li>
 *     <li>Las filas de datos se distribuyen en partes de tamaño lo más equilibrado posible.</li>
 * </ul>
 *
 * <p>
 * Esta clase está pensada para escenarios de procesamiento por partes,
 * donde cada subarchivo será consumido por un flujo concurrente independiente.
 * </p>
 */
public class CsvSplitter {

    /**
     * Separador de columnas por defecto utilizado al trabajar con el CSV
     * en operaciones que no especifican uno de forma explícita.
     */
    private final String separator = ",";

    /**
     * Crea una nueva instancia de {@code CsvSplitter}.
     *
     * <p>
     * Actualmente no mantiene estado relevante; se incluye el constructor
     * explícito para futuras extensiones o para coherencia con el diseño.
     * </p>
     */
    public CsvSplitter() {
        // Constructor intencionalmente vacío.
    }

    /**
     * Divide un archivo CSV en {@code numParts} subarchivos, escribiendo
     * únicamente las filas de datos (sin cabecera) en cada parte.
     *
     * <p>
     * El algoritmo realiza primero un recorrido para:
     * </p>
     * <ul>
     *     <li>Obtener la cabecera original.</li>
     *     <li>Contar cuántas filas de datos no vacías existen.</li>
     * </ul>
     *
     * <p>
     * Luego calcula cuántas líneas le corresponden a cada parte, intentando
     * equilibrar la distribución. Finalmente, crea los archivos en
     * {@code tempDir} con nombres del tipo {@code part_0.csv}, {@code part_1.csv}, etc.
     * </p>
     *
     * @param inputFile archivo CSV de entrada (debe existir y ser un archivo regular).
     * @param tempDir   directorio donde se crearán los subarchivos CSV resultantes;
     *                  si no existe se intenta crear.
     * @param numParts  número de subarchivos (partes) a generar; debe ser mayor que 0.
     * @param separator separador de columnas a utilizar para contar columnas
     *                  en la cabecera (por ejemplo, {@code ","}).
     *
     * @return un {@link CsvSplitResult} que contiene la cabecera original,
     *         el número total de columnas y la lista de archivos generados.
     *
     * @throws FileNotFoundException si el archivo de entrada no existe o no es un archivo regular.
     * @throws IllegalArgumentException si {@code numParts} es menor o igual que 0.
     * @throws IOException si ocurre un error de E/S al leer el archivo de entrada,
     *                     crear el directorio temporal o escribir las partes.
     */
    public static CsvSplitResult split(File inputFile,
                                       File tempDir,
                                       int numParts,
                                       String separator) throws IOException {

        if (!inputFile.exists() || !inputFile.isFile()) {
            throw new FileNotFoundException("No existe: " + inputFile.getAbsolutePath());
        }
        if (numParts <= 0) {
            throw new IllegalArgumentException("numParts debe ser > 0");
        }

        if (!tempDir.exists()) {
            if (!tempDir.mkdirs()) {
                throw new IOException("No se pudo crear directorio temporal: " + tempDir.getAbsolutePath());
            }
        }

        String headerLine;
        long totalDataLines = 0L;

        // Primer recorrido: leer cabecera y contar cuántas filas de datos no vacías hay
        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            headerLine = br.readLine();
            if (headerLine == null) {
                throw new IOException("Archivo vacío: " + inputFile.getAbsolutePath());
            }

            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    totalDataLines++;
                }
            }
        }

        int totalColumns = CsvUtils.countColumns(headerLine, separator);

        long baseLinesPerPart = totalDataLines / numParts;
        long remainder        = totalDataLines % numParts;

        List<File> partFiles = new ArrayList<>(numParts);

        // Segundo recorrido: repartir las filas de datos en las partes
        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            // Saltar la cabecera
            br.readLine();

            String line;
            for (int i = 0; i < numParts; i++) {
                long linesThisPart = baseLinesPerPart + (i < remainder ? 1 : 0);

                File partFile = new File(tempDir, "part_" + i + ".csv");
                partFiles.add(partFile);

                try (BufferedWriter bw = new BufferedWriter(new FileWriter(partFile))) {
                    long written = 0L;
                    while (written < linesThisPart && (line = br.readLine()) != null) {
                        if (line.trim().isEmpty()) {
                            continue; // no la contamos ni la escribimos
                        }
                        bw.write(line);
                        bw.newLine();
                        written++;
                    }
                }
            }
        }

        return new CsvSplitResult(headerLine, totalColumns, partFiles);
    }

}
