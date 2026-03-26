//3) Servicio para generar el reporte por rango de fechas
package sv.edu.udb.gestion.service;

import sv.edu.udb.gestion.dao.CasoDAO;
import sv.edu.udb.gestion.entity.EstadoCaso;

import java.sql.Date;
import java.util.Map;

public class ReporteService {
    private final CasoDAO casoDAO;

    // Constructor que recibe el DAO
    public ReporteService(CasoDAO casoDAO) {
        this.casoDAO = casoDAO;
    }

    /**
     * Genera un reporte de cantidad de casos por estado en un rango de fechas.
     * (Salida simulada como consola; puedes extenderlo a exportación PDF/Excel después.)
     */
    public void generarReportePorFechas(Date fechaInicio, Date fechaFin) throws java.sql.SQLException {
        System.out.println("--------------------------------------------------");
        System.out.println("      REPORTE DE CASOS POR RANGO DE FECHAS");
        System.out.println("--------------------------------------------------");
        System.out.println("Rango: " + fechaInicio + " a " + fechaFin);
        System.out.println("--------------------------------------------------");

        Map<EstadoCaso, Integer> conteo = casoDAO.contarCasosPorEstadoYFechas(fechaInicio, fechaFin);

        System.out.println("Casos Cumplidos (FINALIZADO): " + conteo.getOrDefault(EstadoCaso.FINALIZADO, 0));
        System.out.println("Casos en Desarrollo (EN_DESARROLLO): " + conteo.getOrDefault(EstadoCaso.EN_DESARROLLO, 0));
        System.out.println("Casos Rechazados (RECHAZADO): " + conteo.getOrDefault(EstadoCaso.RECHAZADO, 0));

        System.out.println("--------------------------------------------------");
        System.out.println("Reporte generado exitosamente en consola.");
        System.out.println("--------------------------------------------------");
    }
}

