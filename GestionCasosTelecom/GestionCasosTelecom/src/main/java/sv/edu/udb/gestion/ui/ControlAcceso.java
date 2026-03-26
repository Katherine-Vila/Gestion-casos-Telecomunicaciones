//4) Roles de acceso - Control de Acceso
package sv.edu.udb.gestion.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ControlAcceso {
    // Definimos las acciones clave que pueden ser restringidas
    public static final String ABRIR_CASO = "abrir_caso";
    public static final String ASIGNAR_CASO = "asignar_caso";
    public static final String REGISTRAR_BITACORA = "registrar_bitacora";
    public static final String VER_BITACORA = "ver_bitacora";
    public static final String SUBIR_PDF = "subir_pdf";
    public static final String VER_PROGRESO = "ver_progreso";
    public static final String GENERAR_REPORTE = "generar_reporte";

    /**
     * Verifica si un usuario con un rol dado tiene permiso para realizar una acción.
     */
    public static boolean puedeAcceder(String rolUsuario, String accion) {
        if (rolUsuario == null || accion == null) {
            return false;
        }

        String rolUpper = rolUsuario.toUpperCase();

        switch (rolUpper) {
            case "ADMIN":
                return true;
            case "JEFE_AREA":
                return accion.equals(ABRIR_CASO) || accion.equals(VER_BITACORA);
            case "JEFE_DESARROLLO":
                return accion.equals(ASIGNAR_CASO) || accion.equals(VER_PROGRESO);
            case "PROGRAMADOR":
                return accion.equals(REGISTRAR_BITACORA) || accion.equals(VER_PROGRESO);
            case "EMPLEADO":
                return accion.equals(VER_BITACORA) || accion.equals(SUBIR_PDF);
            default:
                return false;
        }
    }

    /**
     * Obtiene una lista de acciones permitidas para un rol dado (para habilitar/deshabilitar UI).
     */
    public static List<String> obtenerAccionesPermitidas(String rolUsuario) {
        List<String> acciones = new ArrayList<>();
        if (rolUsuario == null) return acciones;

        String rolUpper = rolUsuario.toUpperCase();

        switch (rolUpper) {
            case "ADMIN":
                acciones.addAll(Arrays.asList(
                        ABRIR_CASO,
                        ASIGNAR_CASO,
                        REGISTRAR_BITACORA,
                        VER_BITACORA,
                        SUBIR_PDF,
                        VER_PROGRESO,
                        GENERAR_REPORTE
                ));
                break;
            case "JEFE_AREA":
                acciones.addAll(Arrays.asList(ABRIR_CASO, VER_BITACORA));
                break;
            case "JEFE_DESARROLLO":
                acciones.addAll(Arrays.asList(ASIGNAR_CASO, VER_PROGRESO));
                break;
            case "PROGRAMADOR":
                acciones.addAll(Arrays.asList(REGISTRAR_BITACORA, VER_PROGRESO));
                break;
            case "EMPLEADO":
                acciones.addAll(Arrays.asList(VER_BITACORA, SUBIR_PDF));
                break;
        }
        return acciones;
    }
}

