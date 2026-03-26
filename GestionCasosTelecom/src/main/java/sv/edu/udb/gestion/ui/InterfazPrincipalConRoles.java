package sv.edu.udb.gestion.ui;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class InterfazPrincipalConRoles extends JFrame {
    // ------------------------------------------------------------
    // 4) Roles de acceso (Punto 4 del PDF)
    //    Esta UI:
    //    - Pide un rol al usuario (JOptionPane)
    //    - Construye un JMenuBar con opciones
    //    - Usa ControlAcceso.obtenerAccionesPermitidas(rol)
    //      para habilitar/deshabilitar items del menu
    //    - Al hacer click, abre la interfaz correspondiente
    //      (InterfazPunto1, VistaProgresoEscritorio, ReporteEscritorio)
    // ------------------------------------------------------------
    private final String rolUsuario;
    private InterfazPunto1 interfazPunto1;

    public InterfazPrincipalConRoles() {
        // --- 1. Obtener el rol del usuario ---
        String rolInput = JOptionPane.showInputDialog(null,
                "Por favor, ingrese su rol:\n" +
                        "(ADMIN, JEFE_AREA, JEFE_DESARROLLO, PROGRAMADOR, EMPLEADO)",
                "Autenticación de Rol",
                JOptionPane.QUESTION_MESSAGE);

        if (rolInput == null || rolInput.trim().isEmpty()) {
            rolInput = "INVITADO";
            JOptionPane.showMessageDialog(this, "No se especificó un rol. Acceso restringido.",
                    "Rol por defecto", JOptionPane.WARNING_MESSAGE);
        }

        this.rolUsuario = rolInput.toUpperCase();

        // --- 2. Configurar la ventana principal ---
        setTitle("Sistema de Gestión - Rol: " + this.rolUsuario);
        setSize(820, 620);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // --- 3. Crear el menú y aplicar control de acceso ---
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
        JMenuItem itemRegistrarBitacora = new JMenuItem("Registrar Bitácora");
        menuBitacora.add(itemConsultaBitacora);
        menuBitacora.add(itemRegistrarBitacora);

        // Menú Reportes
        JMenu menuReportes = new JMenu("Reportes");
        JMenuItem itemReporteFechas = new JMenuItem("Reporte por Fechas");
        menuReportes.add(itemReporteFechas);

        // Menú Progreso
        JMenu menuProgreso = new JMenu("Progreso");
        JMenuItem itemVerProgreso = new JMenuItem("Ver Avance de Caso");
        JMenuItem itemSubirPDF = new JMenuItem("Subir PDF a Caso");
        menuProgreso.add(itemVerProgreso);
        menuProgreso.add(itemSubirPDF);

        menuBar.add(menuCasos);
        menuBar.add(menuBitacora);
        menuBar.add(menuReportes);
        menuBar.add(menuProgreso);
        setJMenuBar(menuBar);

        // --- 4. Habilitar/Deshabilitar opciones del menú ---
        List<String> accionesPermitidas = ControlAcceso.obtenerAccionesPermitidas(this.rolUsuario);

        itemApertura.setEnabled(accionesPermitidas.contains(ControlAcceso.ABRIR_CASO));
        itemAsignacion.setEnabled(accionesPermitidas.contains(ControlAcceso.ASIGNAR_CASO));
        itemConsultaBitacora.setEnabled(accionesPermitidas.contains(ControlAcceso.VER_BITACORA));
        itemRegistrarBitacora.setEnabled(accionesPermitidas.contains(ControlAcceso.REGISTRAR_BITACORA));
        itemReporteFechas.setEnabled(accionesPermitidas.contains(ControlAcceso.GENERAR_REPORTE));
        itemVerProgreso.setEnabled(accionesPermitidas.contains(ControlAcceso.VER_PROGRESO));
        itemSubirPDF.setEnabled(accionesPermitidas.contains(ControlAcceso.SUBIR_PDF));

        // --- 5. Panel central informativo ---
        JPanel panelCentral = new JPanel(new BorderLayout());
        JLabel lblInfo = new JLabel(
                "Bienvenido, " + this.rolUsuario + "! Sus acciones habilitadas están en el menú.",
                SwingConstants.CENTER
        );
        lblInfo.setFont(new Font("Arial", Font.BOLD, 16));
        panelCentral.add(lblInfo, BorderLayout.CENTER);
        add(panelCentral, BorderLayout.CENTER);

        // --- 6. ActionListeners ---
        itemApertura.addActionListener(e -> abrirInterfazPunto1(ControlAcceso.ABRIR_CASO));
        itemAsignacion.addActionListener(e -> abrirInterfazPunto1(ControlAcceso.ASIGNAR_CASO));
        itemConsultaBitacora.addActionListener(e -> abrirInterfazPunto1(ControlAcceso.VER_BITACORA));
        itemRegistrarBitacora.addActionListener(e -> abrirInterfazPunto1(ControlAcceso.REGISTRAR_BITACORA));
        itemSubirPDF.addActionListener(e -> abrirInterfazPunto1(ControlAcceso.SUBIR_PDF));
        itemVerProgreso.addActionListener(e -> new VistaProgresoEscritorio().setVisible(true));
        itemReporteFechas.addActionListener(e -> new ReporteEscritorio().setVisible(true));
    }

    private void abrirInterfazPunto1(String accion) {
        if (interfazPunto1 == null) {
            interfazPunto1 = new InterfazPunto1(rolUsuario);
        }
        interfazPunto1.setVisible(true);
        interfazPunto1.mostrarPorAccion(accion);
        interfazPunto1.toFront();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new InterfazPrincipalConRoles().setVisible(true));
    }
}

