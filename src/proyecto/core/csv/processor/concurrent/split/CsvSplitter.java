package proyecto.core.csv.processor.concurrent.split;

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
 * Divide un archivo CSV grande en N subarchivos (solo datos, sin cabecera).
 * <p>
 * Cada subarchivo:
 * <ul>
 *     <li>No incluye la línea de cabecera.</li>
 *     <li>Contiene únicamente filas de datos, distribuidas en esquema round-robin.</li>
 * </ul>
 */
public class CsvSplitter {

    /**
     * Separador de columnas utilizado al trabajar con el CSV.
     */
    private final String separator = ",";

    /**
     * Crea un nuevo {@code CsvSplitter} usando el separador por defecto ({@code ","}).
     * <p>
     * Actualmente no requiere configuración adicional; la instancia se utiliza
     * para invocar al método {@link #split(File, File, int)}.
     */
    public CsvSplitter() {
        // Constructor explícito para satisfacer doclint / javadoc
    }

    /**
     * Divide el archivo CSV de entrada en {@code numParts} archivos parciales.
     * <p>
     * Comportamiento:
     * <ul>
     *     <li>Valida que {@code inputFile} exista y sea un archivo regular.</li>
     *     <li>Lee la primera línea como cabecera y la conserva en memoria.</li>
     *     <li>Cuenta el número total de columnas a partir de la cabecera.</li>
     *     <li>Distribuye las filas de datos restantes en {@code numParts} archivos,
     *         usando un esquema round-robin.</li>
     *     <li>Ignora líneas vacías (solo espacios).</li>
     * </ul>
     *
     * @param inputFile archivo CSV original a dividir.
     * @param tempDir   directorio donde se crearán los archivos parciales.
     * @param numParts  número de partes en las que se desea dividir el archivo;
     *                  debe ser mayor que 0.
     * @return un {@link CsvSplitResult} que contiene:
     *         <ul>
     *             <li>La línea de cabecera original.</li>
     *             <li>El número total de columnas.</li>
     *             <li>La lista de archivos parciales generados.</li>
     *         </ul>
     * @throws IOException              si ocurre un error de E/S al leer o escribir.
     * @throws FileNotFoundException    si {@code inputFile} no existe o no es un archivo.
     * @throws IllegalArgumentException si {@code numParts} es menor o igual a 0.
     */
    public CsvSplitResult split(File inputFile, File tempDir, int numParts) throws IOException {
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

        List<File> partFiles = new ArrayList<File>(numParts);
        List<BufferedWriter> writers = new ArrayList<BufferedWriter>(numParts);
        for (int i = 0; i < numParts; i++) {
            File partFile = new File(tempDir, "part_" + i + ".csv");
            partFiles.add(partFile);
            writers.add(new BufferedWriter(new FileWriter(partFile)));
        }

        String headerLine;
        int totalColumns;
        try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
            headerLine = br.readLine();
            if (headerLine == null) {
                throw new IOException("Archivo vacío: " + inputFile.getAbsolutePath());
            }
            totalColumns = CsvUtils.countColumns(headerLine, separator);

            String line;
            int idx = 0;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                int wIndex = idx % numParts;  // round-robin
                BufferedWriter bw = writers.get(wIndex);
                bw.write(line);
                bw.newLine();
                idx++;
            }
        } finally {
            for (BufferedWriter bw : writers) {
                try {
                    bw.close();
                } catch (IOException e) {
                    // ignorar
                }
            }
        }
        return new CsvSplitResult(headerLine, totalColumns, partFiles);
    }
}
