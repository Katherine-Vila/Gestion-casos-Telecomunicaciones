//1) Interfaz escritorio (Punto 1)
package sv.edu.udb.gestion.ui;

import sv.edu.udb.gestion.dao.ArchivoAdjuntoDAO;
import sv.edu.udb.gestion.dao.UsuarioDAO;
import sv.edu.udb.gestion.entity.*;
import sv.edu.udb.gestion.service.CasoService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class InterfazPunto1 extends JFrame {
    private final CasoService casoService;
    private final ArchivoAdjuntoDAO archivoAdjuntoDAO;
    private final UsuarioDAO usuarioDAO;

    // Constructor
    public InterfazPunto1() {
        // Inicializar servicios/DAO (lógica de Persona 2 vive en el service/DAO)
        this.casoService = new CasoService();
        this.archivoAdjuntoDAO = new ArchivoAdjuntoDAO();
        this.usuarioDAO = new UsuarioDAO();

        configurarVentana();
        crearMenuPrincipal();

        // Mensaje de bienvenida
        JLabel lblBienvenida = new JLabel("Bienvenido. Seleccione una opción del menú.", SwingConstants.CENTER);
        lblBienvenida.setFont(new Font("Arial", Font.BOLD, 16));
        add(lblBienvenida, BorderLayout.CENTER);
    }

    // Configuración básica de la ventana
    private void configurarVentana() {
        setTitle("Gestión de Casos - Punto 1");
        setSize(750, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    // Menú principal para navegar entre formularios
    private void crearMenuPrincipal() {
        JMenuBar menuBar = new JMenuBar();

        JMenu menuCasos = new JMenu("Casos");
        JMenuItem itemApertura = new JMenuItem("Abrir Nuevo Caso");
        JMenuItem itemAsignacion = new JMenuItem("Asignar Caso a Programador");
        menuCasos.add(itemApertura);
        menuCasos.add(itemAsignacion);

        JMenu menuBitacora = new JMenu("Bitácora");
        JMenuItem itemConsultaBitacora = new JMenuItem("Consultar Bitácora");
        menuBitacora.add(itemConsultaBitacora);

        JMenu menuFuncionesWeb = new JMenu("Funciones Web (Simuladas)");
        JMenuItem itemSubirPDF = new JMenuItem("Subir PDF a Caso");
        JMenuItem itemVistaAvance = new JMenuItem("Vista General de Avance");
        menuFuncionesWeb.add(itemSubirPDF);
        menuFuncionesWeb.add(itemVistaAvance);

        menuBar.add(menuCasos);
        menuBar.add(menuBitacora);
        menuBar.add(menuFuncionesWeb);
        setJMenuBar(menuBar);

        // Acciones de menú
        itemApertura.addActionListener(e -> mostrarFormularioApertura());
        itemAsignacion.addActionListener(e -> mostrarFormularioAsignacion());
        itemConsultaBitacora.addActionListener(e -> mostrarFormularioConsultaBitacora());
        itemSubirPDF.addActionListener(e -> mostrarFormularioSubirPDF());
        itemVistaAvance.addActionListener(e -> mostrarFormularioVistaAvance());
    }

    // --- FORMULARIO 1A: APERTURA DE CASOS ---
    private void mostrarFormularioApertura() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField txtTitulo = new JTextField();
        JTextArea txtDescripcion = new JTextArea(3, 20);
        Departamento[] departamentos;
        try {
            //1) UI llama a Persona 2 (service). Capturamos SQLException para no romper la ventana.
            departamentos = casoService.obtenerTodosDepartamentos().toArray(new Departamento[0]);
        } catch (Exception ex) {
            departamentos = new Departamento[0];
            JOptionPane.showMessageDialog(this,
                    "No se pudieron cargar departamentos: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        JComboBox<Departamento> cmbDepartamento = new JComboBox<>(departamentos);
        JTextField txtSolicitanteId = new JTextField();
        JButton btnGuardar = new JButton("Abrir Caso");

        panel.add(new JLabel("Título/Resumen del Caso:"));
        panel.add(txtTitulo);
        panel.add(new JLabel("Descripción:"));
        panel.add(new JScrollPane(txtDescripcion));
        panel.add(new JLabel("Departamento:"));
        panel.add(cmbDepartamento);
        panel.add(new JLabel("Solicitante (ID Usuario):"));
        panel.add(txtSolicitanteId);
        panel.add(new JLabel(""));
        panel.add(btnGuardar);

        btnGuardar.addActionListener(e -> {
            try {
                Departamento departamento = (Departamento) cmbDepartamento.getSelectedItem();
                if (departamento == null) throw new IllegalArgumentException("Seleccione un departamento.");

                String titulo = txtTitulo.getText() != null ? txtTitulo.getText().trim() : "";
                String desc = txtDescripcion.getText() != null ? txtDescripcion.getText().trim() : "";
                String descripcionTotal = titulo;
                if (!desc.isEmpty()) {
                    descripcionTotal = descripcionTotal.isEmpty() ? desc : (descripcionTotal + " - " + desc);
                }
                if (descripcionTotal.isEmpty()) {
                    throw new IllegalArgumentException("Ingrese al menos una descripción.");
                }

                Long solicitanteId = Long.parseLong(txtSolicitanteId.getText().trim());

                //2) Lógica de Persona 2 (service) para abrir el caso
                Long casoId = casoService.solicitarCaso(descripcionTotal, departamento.getId(), solicitanteId);

                JOptionPane.showMessageDialog(this,
                        "Caso abierto exitosamente!\nID: " + casoId,
                        "OK",
                        JOptionPane.INFORMATION_MESSAGE);
                limpiarFormulario(panel);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        setContentPane(panel);
        revalidate();
        repaint();
    }

    // --- FORMULARIO 1B: ASIGNACIÓN DE CASOS ---
    private void mostrarFormularioAsignacion() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField txtCasoId = new JTextField();
        Usuario[] programadores;
        try {
            //1) UI llama a Persona 2 (service). Capturamos SQLException para no romper la ventana.
            programadores = casoService.obtenerUsuariosPorRol("PROGRAMADOR").toArray(new Usuario[0]);
        } catch (Exception ex) {
            programadores = new Usuario[0];
            JOptionPane.showMessageDialog(this,
                    "No se pudieron cargar programadores: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        JComboBox<Usuario> cmbProgramador = new JComboBox<>(programadores);
        JButton btnAsignar = new JButton("Asignar Caso");

        panel.add(new JLabel("ID del Caso:"));
        panel.add(txtCasoId);
        panel.add(new JLabel("Programador:"));
        panel.add(cmbProgramador);
        panel.add(new JLabel(""));
        panel.add(btnAsignar);

        btnAsignar.addActionListener(e -> {
            try {
                Long casoId = Long.parseLong(txtCasoId.getText().trim());
                Usuario programador = (Usuario) cmbProgramador.getSelectedItem();
                if (programador == null) throw new IllegalArgumentException("Seleccione un programador.");

                //2) Lógica de Persona 2 (service) para asignar programador (sobrecarga simplificada)
                casoService.asignarProgramador(casoId, programador.getId());

                //2) Lógica de Persona 2 para registrar una bitácora informativa
                casoService.registrarBitacora(casoId, "Caso asignado a programador: " + programador.getNombre());

                JOptionPane.showMessageDialog(this,
                        "Caso asignado correctamente!",
                        "OK",
                        JOptionPane.INFORMATION_MESSAGE);
                limpiarFormulario(panel);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        setContentPane(panel);
        revalidate();
        repaint();
    }

    // --- FORMULARIO 1C: CONSULTA DE BITÁCORA ---
    private void mostrarFormularioConsultaBitacora() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel panelEntrada = new JPanel(new FlowLayout());
        JTextField txtCasoId = new JTextField(10);
        JButton btnConsultar = new JButton("Consultar Bitácora");
        panelEntrada.add(new JLabel("ID del Caso:"));
        panelEntrada.add(txtCasoId);
        panelEntrada.add(btnConsultar);

        JTextArea txtResultado = new JTextArea();
        txtResultado.setEditable(false);
        JScrollPane scrollResultado = new JScrollPane(txtResultado);

        panel.add(panelEntrada, BorderLayout.NORTH);
        panel.add(scrollResultado, BorderLayout.CENTER);

        btnConsultar.addActionListener(e -> {
            try {
                Long casoId = Long.parseLong(txtCasoId.getText().trim());

                //2) Lógica de Persona 2 (service) para obtener bitácora
                List<Bitacora> bitacoras = casoService.obtenerBitacoras(casoId);

                StringBuilder sb = new StringBuilder("=== BITÁCORA DEL CASO " + casoId + " ===\n\n");
                if (bitacoras.isEmpty()) {
                    sb.append("No hay registros en la bitácora para este caso.");
                } else {
                    for (Bitacora b : bitacoras) {
                        sb.append("Fecha: ").append(b.getFecha()).append("\n");
                        sb.append("Descripción: ").append(b.getDescripcion()).append("\n");
                        sb.append("Avance: ").append(b.getPorcentajeAvance()).append("%\n\n");
                    }
                }
                txtResultado.setText(sb.toString());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        setContentPane(panel);
        revalidate();
        repaint();
    }

    // --- FORMULARIO 1D: SUBIDA DE PDF (Simulado en Escritorio) ---
    private void mostrarFormularioSubirPDF() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField txtCasoId = new JTextField();
        JTextField txtUsuarioSubioId = new JTextField();

        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PDF Files", "pdf"));

        JButton btnSeleccionar = new JButton("Seleccionar PDF");
        JButton btnSubir = new JButton("Simular Subida");

        String[] rutaArchivo = new String[1];
        String[] nombreArchivo = new String[1];

        btnSeleccionar.addActionListener(e -> {
            int resultado = fc.showOpenDialog(this);
            if (resultado == JFileChooser.APPROVE_OPTION) {
                rutaArchivo[0] = fc.getSelectedFile().getAbsolutePath();
                nombreArchivo[0] = fc.getSelectedFile().getName();
                JOptionPane.showMessageDialog(this, "Archivo seleccionado: " + nombreArchivo[0]);
            }
        });

        panel.add(new JLabel("ID del Caso:"));
        panel.add(txtCasoId);
        panel.add(new JLabel("Usuario que sube (ID Usuario):"));
        panel.add(txtUsuarioSubioId);
        panel.add(new JLabel("Archivo PDF:"));
        panel.add(btnSeleccionar);
        panel.add(new JLabel(""));
        panel.add(btnSubir);

        btnSubir.addActionListener(e -> {
            try {
                if (rutaArchivo[0] == null) throw new IllegalArgumentException("Seleccione un archivo primero.");

                Long casoId = Long.parseLong(txtCasoId.getText().trim());
                Long usuarioSubioId = Long.parseLong(txtUsuarioSubioId.getText().trim());

                // Cargamos entidades necesarias para insertar el adjunto.
                //2) Lógica de Persona 2 (service/DAO)
                Caso caso = casoService.obtenerCasoPorId(casoId);
                if (caso == null) throw new IllegalArgumentException("Caso no encontrado: " + casoId);

                Usuario usuarioSubio = usuarioDAO.buscarPorId(usuarioSubioId);
                if (usuarioSubio == null) throw new IllegalArgumentException("Usuario no encontrado: " + usuarioSubioId);

                ArchivoAdjunto archivo = new ArchivoAdjunto();
                archivo.setCaso(caso);
                archivo.setNombreArchivo(nombreArchivo[0]);
                archivo.setRuta(rutaArchivo[0]);
                // En tu proyecto, TipoArchivo no tiene "PDF". Usamos SOLICITUD como equivalente.
                archivo.setTipo(ArchivoAdjunto.TipoArchivo.SOLICITUD);
                archivo.setUsuarioSubio(usuarioSubio);
                archivo.setFechaSubida(new java.util.Date());

                //2) Acceso a datos (DAO) para persistir el adjunto
                archivoAdjuntoDAO.insertar(archivo);

                //2) Bitácora informativa
                casoService.registrarBitacora(casoId, "PDF simulado subido: " + nombreArchivo[0]);

                JOptionPane.showMessageDialog(this,
                        "Simulación de subida de PDF exitosa!",
                        "OK",
                        JOptionPane.INFORMATION_MESSAGE);
                limpiarFormulario(panel);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        setContentPane(panel);
        revalidate();
        repaint();
    }

    // --- FORMULARIO 1E: VISTA GENERAL DE AVANCE ---
    private void mostrarFormularioVistaAvance() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel panelEntrada = new JPanel(new FlowLayout());
        JTextField txtCasoId = new JTextField(10);
        JButton btnConsultar = new JButton("Consultar Avance");
        panelEntrada.add(new JLabel("ID del Caso:"));
        panelEntrada.add(txtCasoId);
        panelEntrada.add(btnConsultar);

        JTextArea txtResultado = new JTextArea();
        txtResultado.setEditable(false);
        JScrollPane scrollResultado = new JScrollPane(txtResultado);

        panel.add(panelEntrada, BorderLayout.NORTH);
        panel.add(scrollResultado, BorderLayout.CENTER);

        btnConsultar.addActionListener(e -> {
            try {
                Long casoId = Long.parseLong(txtCasoId.getText().trim());

                //2) Lógica de Persona 2 (service) para obtener el caso
                Caso caso = casoService.obtenerCasoPorId(casoId);
                if (caso == null) throw new IllegalArgumentException("Caso no encontrado: " + casoId);

                StringBuilder sb = new StringBuilder("=== VISTA DE AVANCE DEL CASO " + casoId + " ===\n\n");
                sb.append("Descripción: ").append(caso.getDescripcionSolicitud()).append("\n");
                sb.append("Porcentaje de Avance: ").append(caso.getPorcentajeAvance()).append("%\n");
                sb.append("Estado Actual: ").append(caso.getEstado()).append("\n");

                txtResultado.setText(sb.toString());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        setContentPane(panel);
        revalidate();
        repaint();
    }

    // --- Métodos Auxiliares ---
    private void limpiarFormulario(JPanel panel) {
        for (Component c : panel.getComponents()) {
            if (c instanceof JTextField) ((JTextField) c).setText("");
            if (c instanceof JTextArea) ((JTextArea) c).setText("");
            if (c instanceof JComboBox) {
                JComboBox<?> combo = (JComboBox<?>) c;
                if (combo.getItemCount() > 0) combo.setSelectedIndex(0);
            }
        }
    }

    // --- Método Principal para Ejecutar ---
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            InterfazPunto1 ventana = new InterfazPunto1();
            ventana.setVisible(true);
        });
    }
}

