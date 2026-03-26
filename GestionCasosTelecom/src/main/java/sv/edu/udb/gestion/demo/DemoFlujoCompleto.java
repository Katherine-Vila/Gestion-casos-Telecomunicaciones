package sv.edu.udb.gestion.demo;

import sv.edu.udb.gestion.dao.DepartamentoDAO;
import sv.edu.udb.gestion.dao.UsuarioDAO;
import sv.edu.udb.gestion.entity.Departamento;
import sv.edu.udb.gestion.entity.Rol;
import sv.edu.udb.gestion.entity.Usuario;
import sv.edu.udb.gestion.service.CasoService;
import sv.edu.udb.gestion.util.DatabaseConnection;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

/**
 * Demostración automática del flujo: crea datos mínimos en la BD y recorre
 * solicitud -> analisis -> asignacion (codigo del caso) -> bitacora -> finalizar -> aprobar.
 * Los correos llevan un sufijo numérico para no chocar con ejecuciones anteriores.
 */
public final class DemoFlujoCompleto {

    private DemoFlujoCompleto() {
    }

    public static void ejecutar() {
        long sufijo = System.currentTimeMillis();
        CasoService servicio = new CasoService();
        DepartamentoDAO departamentoDAO = new DepartamentoDAO();
        UsuarioDAO usuarioDAO = new UsuarioDAO();

        System.out.println("========== DEMO: flujo completo (requiere MySQL y esquema gestion_cambios) ==========");
        DatabaseConnection.testConnection();

        try {
            servicio.verificarVencimientos();
        } catch (SQLException e) {
            System.err.println("Aviso: no se pudieron revisar vencimientos al inicio: " + e.getMessage());
        }

        try {
            // 1) Departamento con código de 3 letras (sirve para el prefijo del código del caso)
            Departamento depto = new Departamento("PRS", "Departamento demo " + sufijo);
            departamentoDAO.insertar(depto);
            System.out.println("\n[1] Departamento creado: id=" + depto.getId() + " código=" + depto.getCodigo());

            // 2) Usuarios mínimos: jefe de área, jefe de desarrollo, programador bajo ese jefe, empleado probador
            Usuario jefeArea = new Usuario("Jefe Área demo", "jefe_area_" + sufijo + "@demo.com", "demo", Rol.JEFE_AREA);
            jefeArea.setDepartamento(depto);
            usuarioDAO.insertar(jefeArea);
            System.out.println("[2a] Jefe de área: id=" + jefeArea.getId());

            Usuario jefeDev = new Usuario("Jefe Desarrollo demo", "jefe_dev_" + sufijo + "@demo.com", "demo", Rol.JEFE_DESARROLLO);
            usuarioDAO.insertar(jefeDev);
            System.out.println("[2b] Jefe de desarrollo: id=" + jefeDev.getId());

            Usuario programador = new Usuario("Programador demo", "prog_" + sufijo + "@demo.com", "demo", Rol.PROGRAMADOR);
            programador.setJefeDesarrollo(jefeDev);
            usuarioDAO.insertar(programador);
            System.out.println("[2c] Programador (a cargo del jefe dev): id=" + programador.getId());

            Usuario probador = new Usuario("Probador demo", "prob_" + sufijo + "@demo.com", "demo", Rol.EMPLEADO);
            probador.setDepartamento(depto);
            usuarioDAO.insertar(probador);
            System.out.println("[2d] Probador (mismo departamento que el pedido): id=" + probador.getId());

            // 3) Solicitud inicial: EN_ESPERA, aun sin codigo
            System.out.println("\n[3] Solicitar caso (descripcion + depto + solicitante)...");
            Long casoId = servicio.solicitarCaso("Requerimiento demo: reporte de ventas", depto.getId(), jefeArea.getId());
            System.out.println("     Caso insertado con id=" + casoId + " (sin código hasta que lo acepte desarrollo).");

            // 4) "Analizar" = cargar y validar que sigue en EN_ESPERA
            System.out.println("\n[4] Analizar caso (carga para decision del jefe de desarrollo)...");
            System.out.println("     Estado al analizar: " + servicio.analizarCaso(casoId).getEstado());

            // 5) Aceptacion: genera codigo PRS..., asigna roles y fecha limite -> EN_DESARROLLO
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, 14);
            Date fechaLimite = cal.getTime();
            System.out.println("\n[5] Asignar programador, analisis, probador y fecha limite...");
            servicio.asignarProgramador(casoId, jefeDev.getId(), programador.getId(), fechaLimite,
                    "Análisis demo: campos y tablas a tocar.", probador.getId());

            // 6) Bitacora + porcentaje
            System.out.println("\n[6] Registrar bitacora (avance)...");
            servicio.registrarBitacora(casoId, "Implementación inicial del reporte", 60);
            servicio.registrarBitacora(casoId, "Ajustes de consulta y pruebas locales", 100);

            // 7) Cierre del programador -> espera al probador
            System.out.println("\n[7] Finalizar trabajo del programador -> ESPERANDO_APROBACION...");
            servicio.finalizarCaso(casoId);

            // 8) Aprobacion del probador -> FINALIZADO + fecha de puesta en produccion
            System.out.println("\n[8] Aprobar caso (fecha de puesta en produccion)...");
            servicio.aprobarCaso(casoId, new Date());

            System.out.println("\n=== DEMO terminada. Revisa en la tabla 'casos' el registro id=" + casoId + " ===");
        } catch (Exception e) {
            System.err.println("\nError en la demo (MySQL encendido y script aplicado?): " + e.getMessage());
            e.printStackTrace();
        }
    }
}
