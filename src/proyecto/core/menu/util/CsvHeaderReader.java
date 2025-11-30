package proyecto.core.menu.util;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Utilidad para leer la línea de cabecera de un archivo CSV.
 */
public final class CsvHeaderReader {

    private CsvHeaderReader() {
    }
    /**
     * Lee la primera línea (cabecera) del archivo CSV ubicado en la ruta dada.
     *
     * @param inputPath ruta del archivo CSV de entrada del que se desea leer la cabecera.
     * @return la línea de cabecera del CSV, o {@code null} si no se pudo leer.
     */
    public static String readHeaderLine(String inputPath) {
        File f = new File(inputPath);
        if (!f.exists() || !f.isFile()) {
            System.err.println("No se encontró el archivo: " + f.getAbsolutePath());
            return null;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String header = br.readLine();
            if (header == null) {
                System.err.println("El archivo está vacío.");
            }
            return header;
        } catch (IOException e) {
            System.err.println("Error al leer cabecera: " + e.getMessage());
            return null;
        }
    }
}
