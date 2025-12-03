package proyecto.core.csv.processor.concurrent.parts.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Utilidad para escribir mensajes en un archivo de log
 * de forma segura en entornos concurrentes.
 * <p>
 * Esta clase proporciona un método estático para registrar errores
 * desde múltiples hilos, asegurando el acceso exclusivo al archivo
 * de log mediante un objeto de bloqueo compartido.
 */
public final class CsvLog {

    /**
     * Constructor privado para evitar instanciación.
     * <p>
     * La clase funciona únicamente como contenedor de métodos estáticos.
     */
    private CsvLog() {
        // Evitar instanciación
    }

    /**
     * Escribe un mensaje en el archivo de log de forma segura para hilos.
     * <p>
     * Comportamiento:
     * <ul>
     *     <li>Si {@code logFile}, {@code logLock} o {@code message} son {@code null},
     *         el método no hace nada.</li>
     *     <li>Usa un bloque {@code synchronized} sobre {@code logLock} para garantizar
     *         que solo un hilo escribe en el log a la vez.</li>
     *     <li>Abre el archivo en modo append ({@code FileWriter(logFile, true)}),
     *         escribe el mensaje y agrega un salto de línea.</li>
     *     <li>Si ocurre una excepción de E/S, muestra un mensaje de error por
     *         {@code System.err} con la causa.</li>
     * </ul>
     *
     * @param logFile archivo de log donde se escribirán los mensajes.
     * @param logLock objeto utilizado como lock para sincronizar el acceso concurrente
     *                al archivo de log. Debe ser el mismo objeto compartido entre
     *                todos los hilos que llamen a este método.
     * @param message mensaje a registrar en el archivo de log.
     */
    public static void logError(File logFile, Object logLock, String message) {
        if (logFile == null || logLock == null || message == null) {
            return;
        }

        synchronized (logLock) {
            try (BufferedWriter logWriter = new BufferedWriter(new FileWriter(logFile, true))) {
                logWriter.write(message);
                logWriter.newLine();
            } catch (IOException e) {
                System.err.println("No se pudo escribir en el log: " + e.getMessage());
            }
        }
    }
}
