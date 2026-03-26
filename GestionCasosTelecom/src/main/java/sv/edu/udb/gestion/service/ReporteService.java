package sv.edu.udb.gestion.service;

import sv.edu.udb.gestion.dao.CasoDAO;
import sv.edu.udb.gestion.entity.EstadoCaso;

import java.sql.Date;
import java.sql.SQLException;
import java.util.Map;

public class ReporteService {
    // ------------------------------------------------------------
    // 3) Reportes por rango de fechas (Punto 3 del PDF)
    //    Esta clase es el "servicio" de la generacion del reporte:
    //    - recibe fechaInicio y fechaFin
    //    - llama al DAO (CasoDAO) para hacer la consulta SQL
    //    - muestra el resumen y devuelve el mapa (para usarlo en UI)
    // ------------------------------------------------------------
    private final CasoDAO casoDAO;

    public ReporteService(CasoDAO casoDAO) {
        this.casoDAO = casoDAO;
    }

    /**
     * Genera un reporte: cantidad de casos por estado entre {@code fechaInicio} y {@code fechaFin} (inclusive).
     * La salida se imprime en consola y también se devuelve como mapa para la UI.
     */
    public Map<EstadoCaso, Integer> generarReportePorFechas(Date fechaInicio, Date fechaFin) throws SQLException {
        System.out.println("--------------------------------------------------");
        System.out.println("      REPORTE DE CASOS POR RANGO DE FECHAS");
        System.out.println("Rango: " + fechaInicio + " a " + fechaFin);
        System.out.println("--------------------------------------------------");

        Map<EstadoCaso, Integer> conteo = casoDAO.contarCasosPorEstadoYFechas(fechaInicio, fechaFin);

        System.out.println("Casos Cumplidos (FINALIZADO): " + conteo.getOrDefault(EstadoCaso.FINALIZADO, 0));
        System.out.println("Casos en Desarrollo (EN_DESARROLLO): " + conteo.getOrDefault(EstadoCaso.EN_DESARROLLO, 0));
        System.out.println("Casos Rechazados (RECHAZADO): " + conteo.getOrDefault(EstadoCaso.RECHAZADO, 0));

        System.out.println("--------------------------------------------------");
        System.out.println("Reporte generado exitosamente.");
        System.out.println("--------------------------------------------------");
        return conteo;
    }
}

