//4) Roles de acceso - Interfaz principal con menú por permisos
package sv.edu.udb.gestion.ui;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class InterfazPrincipalConRoles extends JFrame {
    private String rolUsuario;

    public InterfazPrincipalConRoles() {
        // --- 1) Obtener el rol del usuario ---
        rolUsuario = JOptionPane.showInputDialog(null,
                "Por favor, ingrese su rol:\n" +
                        "(ADMIN, JEFE_AREA, JEFE_DESARROLLO, PROGRAMADOR, EMPLEADO)",
                "Autenticación de Rol",
                JOptionPane.QUESTION_MESSAGE);

        // Si el usuario cancela o no ingresa rol, asignamos un rol por defecto sin permisos
        if (rolUsuario == null || rolUsuario.trim().isEmpty()) {
            rolUsuario = "INVITADO"; // Rol sin permisos
            JOptionPane.showMessageDialog(this,
                    "No se especificó un rol. Acceso restringido.",
                    "Rol por defecto",
                    JOptionPane.WARNING_MESSAGE);
        }

        // --- 2) Configurar la ventana principal ---
        setTitle("Sistema de Gestión - Rol: " + rolUsuario.toUpperCase());
        setSize(850, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- 3) Crear el menú y aplicar control de acceso ---
        JMenuBar menuBar = new JMenuBar();

        // Menú Casos
        JMenu menuCasos = new JMenu("Casos");
        JMenuItem itemApertura = new JMenuItem("Abrir Nuevo Caso");
        JMenuItem itemAsignacion = new JMenuItem("Asignar Caso a Programador");
        menuCasos.add(itemApertura);
        menuCasos.add(itemAsignacion);

        // Menú Bitácora
        JMenu menuBitacora = new JMenu("Bitácora");
        JMenuItem itemConsultaBitacora = new JMenuItem("Consultar Bitácora");
        menuBitacora.add(itemConsultaBitacora);

        // Menú Reportes (Punto 3)
        JMenu menuReportes = new JMenu("Reportes");
        JMenuItem itemReporteFechas = new JMenuItem("Reporte por Fechas");
        menuReportes.add(itemReporteFechas);

        // Menú Progreso (Punto 2)
        JMenu menuProgreso = new JMenu("Progreso");
        JMenuItem itemVerProgreso = new JMenuItem("Ver Avance de Caso");
        JMenuItem itemSubirPDF = new JMenuItem("Subir PDF a Caso");
        menuProgreso.add(itemVerProgreso);
        menuProgreso.add(itemSubirPDF);

        // Añadir menús a la barra
        menuBar.add(menuCasos);
        menuBar.add(menuBitacora);
        menuBar.add(menuReportes);
        menuBar.add(menuProgreso);
        setJMenuBar(menuBar);

        // --- 4) Habilitar/Deshabilitar opciones del menú ---
        List<String> accionesPermitidas = ControlAcceso.obtenerAccionesPermitidas(rolUsuario);
        itemApertura.setEnabled(accionesPermitidas.contains(ControlAcceso.ABRIR_CASO));
        itemAsignacion.setEnabled(accionesPermitidas.contains(ControlAcceso.ASIGNAR_CASO));
        itemConsultaBitacora.setEnabled(accionesPermitidas.contains(ControlAcceso.VER_BITACORA));
        itemReporteFechas.setEnabled(accionesPermitidas.contains(ControlAcceso.GENERAR_REPORTE));
        itemVerProgreso.setEnabled(accionesPermitidas.contains(ControlAcceso.VER_PROGRESO));
        itemSubirPDF.setEnabled(accionesPermitidas.contains(ControlAcceso.SUBIR_PDF));

        // --- 5) Panel central informativo ---
        JPanel panelCentral = new JPanel(new BorderLayout());
        JLabel lblInfo = new JLabel(
                "Bienvenido, " + rolUsuario.toUpperCase() + "! Sus acciones disponibles están habilitadas en el menú.",
                SwingConstants.CENTER);
        lblInfo.setFont(new Font("Arial", Font.BOLD, 16));
        panelCentral.add(lblInfo, BorderLayout.CENTER);
        add(panelCentral, BorderLayout.CENTER);

        // --- 6) ActionListeners (ejemplo: mensajes; puedes conectar estas opciones a las pantallas reales) ---
        itemApertura.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Abrir formulario de Apertura de Caso (Punto 1)."));
        itemAsignacion.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Abrir formulario de Asignación de Caso (Punto 1)."));
        itemConsultaBitacora.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Abrir formulario de Consulta de Bitácora (Punto 1)."));
        itemReporteFechas.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Abrir formulario de Reporte por Fechas (Punto 3)."));
        itemVerProgreso.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Abrir formulario de Vista de Progreso (Punto 2)."));
        itemSubirPDF.addActionListener(e ->
                JOptionPane.showMessageDialog(this, "Abrir formulario de Subida de PDF (Punto 1)."));
    }

    // --- Método Principal para Ejecutar esta Ventana ---
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            InterfazPrincipalConRoles ventana = new InterfazPrincipalConRoles();
            ventana.setVisible(true);
        });
    }
}