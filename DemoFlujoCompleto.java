package sv.edu.udb;

import sv.edu.udb.gestion.demo.DemoFlujoCompleto;
import sv.edu.udb.gestion.ui.MenuPrincipal;

public class App {

    public static void main(String[] args) {
        // Modo demo: recorre todo el flujo con datos de prueba en MySQL (sin menú).
        // En IntelliJ: Run → Edit Configurations → Program arguments: demo
        if (args != null && args.length > 0 && "demo".equalsIgnoreCase(args[0].trim())) {
            DemoFlujoCompleto.ejecutar();
            return;
        }

        System.out.println("========== GESTIÓN DE CASOS (Telecom) ==========");
        System.out.println();
        System.out.println("Flujo resumido (especificación del proyecto):");
        System.out.println(" 1) Jefe de área pide el caso → queda EN_ESPERA (todavía sin código PRS…).");
        System.out.println(" 2) Jefe de desarrollo revisa la solicitud: la rechaza o la acepta.");
        System.out.println(" 3) Si acepta: se genera el código, se asigna programador, fecha límite y probador → EN_DESARROLLO.");
        System.out.println(" 4) El programador escribe la bitácora y el % de avance (puede repetir varias veces).");
        System.out.println(" 5) Cuando termina su parte, finaliza el caso → ESPERANDO_APROBACION.");
        System.out.println(" 6) El probador aprueba (con fecha de puesta en producción) o devuelve con observaciones (7 días para corregir).");
        System.out.println(" 7) Si vence la fecha límite en desarrollo, o el plazo de 7 días en devuelto, el sistema puede marcar VENCIDO.");
        System.out.println();
        System.out.println("Tip: ejecuta con argumento  demo  para ver un recorrido automático sin menú.");
        System.out.println("Abajo va el menú interactivo (registra departamentos y usuarios antes de probar casos).");
        System.out.println();

        MenuPrincipal.main(args);
    }
}
