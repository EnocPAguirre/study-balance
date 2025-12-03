/**
 * Proporciona utilidades para el registro de métricas y estadísticas
 * relacionadas con la ejecución de procesos sobre archivos CSV.
 *
 * <p>
 * Actualmente este paquete incluye:
 * </p>
 * <ul>
 *     <li>{@link proyecto.core.csv.metrics.ExecutionLogger}: registra en un
 *         archivo CSV el historial de ejecuciones (fecha y hora, modo de
 *         ejecución, archivos de entrada/salida y tiempos).</li>
 * </ul>
 *
 * <p>
 * La intención de este paquete es centralizar todas las herramientas de
 * medición y monitorización ligeras del proyecto, de forma que puedan ser
 * reutilizadas tanto por los procesadores secuenciales como por los
 * concurrentes.
 * </p>
 */
package proyecto.core.csv.metrics;

