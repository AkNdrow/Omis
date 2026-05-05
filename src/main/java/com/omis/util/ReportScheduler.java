package com.omis.util;

import com.omis.dao.VentaDAO;
import com.omis.model.Venta;
import com.omis.service.ReporteService;

import java.io.File;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Servicio encargado de la generación automática de reportes.
 * Realiza un chequeo inicial al arrancar y mantiene un hilo en background.
 */
public class ReportScheduler {

    private static ScheduledExecutorService scheduler;
    private static final VentaDAO ventaDAO = new VentaDAO();

    /**
     * Inicia el planificador.
     * 1. Verifica reportes pendientes de días pasados (Startup check).
     * 2. Inicia el cron para revisar periódicamente.
     */
    public static void iniciar() {
        System.out.println("[ReportScheduler] Iniciando chequeo de reportes...");
        
        // 1. Startup check: Validar si falta algún reporte pasado
        chequearReportesAtrasados();

        // 2. Tarea recurrente: Revisar cada 15 minutos si ya es medianoche
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            try {
                LocalDateTime ahora = LocalDateTime.now();
                // Si son pasadas las 00:00 pero antes de las 00:15 (ventana de ejecución)
                if (ahora.getHour() == 0 && ahora.getMinute() < 15) {
                    System.out.println("[ReportScheduler] Ejecutando generación nocturna de reportes...");
                    generarReportesPendientesParaFecha(ahora.toLocalDate().minusDays(1));
                }
            } catch (Exception e) {
                System.err.println("[ReportScheduler] Error en hilo de reportes: " + e.getMessage());
            }
        }, 1, 15, TimeUnit.MINUTES);
    }

    public static void detener() {
        if (scheduler != null && !scheduler.isShutdown()) {
            System.out.println("[ReportScheduler] Deteniendo servicio de reportes.");
            scheduler.shutdown();
        }
    }

    /**
     * Verifica retrospectivamente si se generaron los reportes del día anterior, semana o mes anterior.
     */
    private static void chequearReportesAtrasados() {
        LocalDate ayer = LocalDate.now().minusDays(1);
        
        // Revisar últimos 3 días como margen de seguridad
        for (int i = 0; i < 3; i++) {
            LocalDate fechaRevision = ayer.minusDays(i);
            generarReportesPendientesParaFecha(fechaRevision);
        }
    }

    /**
     * Genera los reportes (Diario, Semanal, Mensual) correspondientes al final del día indicado,
     * si es que no han sido generados ya.
     */
    private static void generarReportesPendientesParaFecha(LocalDate fecha) {
        // --- REPORTE DIARIO ---
        if (!existeReporteDiario(fecha)) {
            List<Venta> ventasDia = ventaDAO.findByDateRange(fecha.toString() + " 00:00:00", fecha.toString() + " 23:59:59");
            if (!ventasDia.isEmpty()) { // Solo generar si hubo ventas
                System.out.println("[ReportScheduler] Generando reporte diario faltante para: " + fecha);
                ReporteService.generarReporteDiario(fecha, ventasDia);
            }
        }

        // --- REPORTE SEMANAL (Si 'fecha' es Domingo) ---
        if (fecha.getDayOfWeek() == DayOfWeek.SUNDAY && !existeReporteSemanal(fecha)) {
            LocalDate inicioSemana = fecha.minusDays(6); // Lunes
            List<Venta> ventasSemana = ventaDAO.findByDateRange(inicioSemana.toString() + " 00:00:00", fecha.toString() + " 23:59:59");
            if (!ventasSemana.isEmpty()) {
                System.out.println("[ReportScheduler] Generando reporte semanal faltante para fin de semana: " + fecha);
                ReporteService.generarReporteSemanal(inicioSemana, fecha, ventasSemana);
            }
        }

        // --- REPORTE MENSUAL (Si 'fecha' es fin de mes) ---
        LocalDate finDeMes = fecha.with(TemporalAdjusters.lastDayOfMonth());
        if (fecha.equals(finDeMes) && !existeReporteMensual(fecha)) {
            LocalDate inicioMes = fecha.withDayOfMonth(1);
            List<Venta> ventasMes = ventaDAO.findByDateRange(inicioMes.toString() + " 00:00:00", fecha.toString() + " 23:59:59");
            if (!ventasMes.isEmpty()) {
                System.out.println("[ReportScheduler] Generando reporte mensual faltante para mes: " + fecha.getMonth());
                ReporteService.generarReporteMensual(fecha, ventasMes);
            }
        }
    }

    // --- Métodos auxiliares para verificar si el archivo físico ya existe ---

    private static boolean existeReporteDiario(LocalDate fecha) {
        String dirDia = getDirectorioMes(fecha) + File.separator + fecha.format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        File f = new File(dirDia, "Reporte del dia.pdf");
        return f.exists();
    }

    private static boolean existeReporteSemanal(LocalDate domingo) {
        String dirMes = getDirectorioMes(domingo);
        LocalDate lunes = domingo.minusDays(6);
        File f = new File(dirMes, "Reporte semanal (" + lunes + " al " + domingo + ").pdf");
        return f.exists();
    }

    private static boolean existeReporteMensual(LocalDate fechaFinMes) {
        String dirMes = getDirectorioMes(fechaFinMes);
        File f = new File(dirMes, "Reporte del mes.pdf");
        return f.exists();
    }

    private static String getDirectorioMes(LocalDate fecha) {
        String[] MESES = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
        String mesAnio = MESES[fecha.getMonthValue() - 1] + " " + fecha.getYear();
        return "Reportes de venta Omis" + File.separator + fecha.getYear() + File.separator + mesAnio;
    }
}
