package sv.edu.udb.gestion.ui;

import sv.edu.udb.gestion.dao.ArchivoAdjuntoDAO;
import sv.edu.udb.gestion.dao.DepartamentoDAO;
import sv.edu.udb.gestion.dao.UsuarioDAO;
import sv.edu.udb.gestion.entity.*;
import sv.edu.udb.gestion.service.CasoService;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class InterfazPunto1 extends JFrame {
    // ------------------------------------------------------------
    // 1) Interfaz escritorio (Punto 1 del PDF)
    //    Este JFrame contiene las pantallas (formularios) para:
    //    - Abrir nuevo caso
    //    - Asignar caso a programador (Jefe de desarrollo)
    //    - Consultar bitacora del caso
    //    - Registrar bitacora (programador/empleado)
    //    - Subir PDF a un caso (web/simulacion en escritorio)
    //    - Ver vista general de avance
    // ------------------------------------------------------------
    private final String rolUsuario;

    private final CasoService casoService;
    private final DepartamentoDAO departamentoDAO;
    private final UsuarioDAO usuarioDAO;
    private final ArchivoAdjuntoDAO archivoAdjuntoDAO;

    public InterfazPunto1(String rolUsuario) {
        this.rolUsuario = (rolUsuario == null || rolUsuario.trim().isEmpty()) ? "INVITADO" : rolUsuario.toUpperCase();
        this.casoService = new CasoService();
        this.departamentoDAO = new DepartamentoDAO();
        this.usuarioDAO = new UsuarioDAO();
        this.archivoAdjuntoDAO = new ArchivoAdjuntoDAO();

        configurarVentana();
        crearMenuPrincipal();
        mostrarBienvenida();
    }

    private void configurarVentana() {
        setTitle("Interfaz Escritorio - Gestión de Casos");
        setSize(760, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    private void crearMenuPrincipal() {
        JMenuBar menuBar = new JMenuBar();

        JMenu menuCasos = new JMenu("Casos");
        JMenuItem itemApertura = new JMenuItem("Abrir Nuevo Caso");
        JMenuItem itemAsignacion = new JMenuItem("Asignar Caso a Programador");
        menuCasos.add(itemApertura);
        menuCasos.add(itemAsignacion);

        JMenu menuBitacora = new JMenu("Bitácora");
        JMenuItem itemConsultaBitacora = new JMenuItem("Consultar Bitácora");
        JMenuItem itemRegistrarBitacora = new JMenuItem("Registrar Bitácora");
        menuBitacora.add(itemConsultaBitacora);
        menuBitacora.add(itemRegistrarBitacora);

        JMenu menuFuncionesWeb = new JMenu("Funciones Web (Simuladas)");
        JMenuItem itemSubirPDF = new JMenuItem("Subir PDF a Caso");
        JMenuItem itemVistaAvance = new JMenuItem("Vista General de Avance");
        menuFuncionesWeb.add(itemSubirPDF);
        menuFuncionesWeb.add(itemVistaAvance);

        menuBar.add(menuCasos);
        menuBar.add(menuBitacora);
        menuBar.add(menuFuncionesWeb);
        setJMenuBar(menuBar);

        List<String> accionesPermitidas = ControlAcceso.obtenerAccionesPermitidas(this.rolUsuario);
        itemApertura.setEnabled(accionesPermitidas.contains(ControlAcceso.ABRIR_CASO));
        itemAsignacion.setEnabled(accionesPermitidas.contains(ControlAcceso.ASIGNAR_CASO));
        itemConsultaBitacora.setEnabled(accionesPermitidas.contains(ControlAcceso.VER_BITACORA));
        itemRegistrarBitacora.setEnabled(accionesPermitidas.contains(ControlAcceso.REGISTRAR_BITACORA));
        itemSubirPDF.setEnabled(accionesPermitidas.contains(ControlAcceso.SUBIR_PDF));
        itemVistaAvance.setEnabled(accionesPermitidas.contains(ControlAcceso.VER_PROGRESO));

        itemApertura.addActionListener(e -> mostrarPorAccion(ControlAcceso.ABRIR_CASO));
        itemAsignacion.addActionListener(e -> mostrarPorAccion(ControlAcceso.ASIGNAR_CASO));
        itemConsultaBitacora.addActionListener(e -> mostrarPorAccion(ControlAcceso.VER_BITACORA));
        itemRegistrarBitacora.addActionListener(e -> mostrarPorAccion(ControlAcceso.REGISTRAR_BITACORA));
        itemSubirPDF.addActionListener(e -> mostrarPorAccion(ControlAcceso.SUBIR_PDF));
        itemVistaAvance.addActionListener(e -> mostrarPorAccion(ControlAcceso.VER_PROGRESO));
    }

    private void mostrarBienvenida() {
        JPanel panelCentral = new JPanel(new BorderLayout());
        JLabel lblBienvenida = new JLabel(
                "Bienvenido. Rol: " + rolUsuario.toUpperCase() + ". Seleccione una opción del menú.",
                SwingConstants.CENTER
        );
        lblBienvenida.setFont(new Font("Arial", Font.BOLD, 15));
        panelCentral.add(lblBienvenida, BorderLayout.CENTER);
        setContentPane(panelCentral);
        revalidate();
        repaint();
    }

    /**
     * Cambia la vista según la acción permitida.
     */
    public void mostrarPorAccion(String accion) {
        // Seguridad extra: aunque el menú esté deshabilitado, evitamos que se ejecute por código.
        if (!ControlAcceso.puedeAcceder(rolUsuario, accion)) {
            JOptionPane.showMessageDialog(this, "Acceso denegado para la acción: " + accion,
                    "Permisos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        switch (accion) {
            case ControlAcceso.ABRIR_CASO:
                mostrarFormularioApertura();
                break;
            case ControlAcceso.ASIGNAR_CASO:
                mostrarFormularioAsignacion();
                break;
            case ControlAcceso.VER_BITACORA:
                mostrarFormularioConsultaBitacora();
                break;
            case ControlAcceso.REGISTRAR_BITACORA:
                mostrarFormularioRegistrarBitacora();
                break;
            case ControlAcceso.SUBIR_PDF:
                mostrarFormularioSubirPDF();
                break;
            case ControlAcceso.VER_PROGRESO:
                mostrarFormularioVistaAvance();
                break;
            default:
                mostrarBienvenida();
                break;
        }
    }

    // 1) Interfaz escritorio - Formulario 1A: Apertura de casos
    private void mostrarFormularioApertura() {
        JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextArea txtDescripcion = new JTextArea(4, 20);
        JComboBox<Departamento> cmbDepartamento;
        JComboBox<Usuario> cmbSolicitante;

        try {
            List<Departamento> departamentos = departamentoDAO.listarTodos();
            cmbDepartamento = new JComboBox<>(departamentos.toArray(new Departamento[0]));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar departamentos: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        cmbSolicitante = new JComboBox<>();
        configurarRenderersCombo(cmbDepartamento);
        configurarRenderersCombo(cmbSolicitante);

        // Cargar solicitantes según el departamento seleccionado
        cargarSolicitantesPorDepartamento((Departamento) cmbDepartamento.getSelectedItem(), cmbSolicitante);

        cmbDepartamento.addActionListener(e ->
                cargarSolicitantesPorDepartamento((Departamento) cmbDepartamento.getSelectedItem(), cmbSolicitante)
        );

        JButton btnGuardar = new JButton("Abrir Caso");

        panel.add(new JLabel("Descripción del Caso:"));
        panel.add(new JScrollPane(txtDescripcion));
        panel.add(new JLabel("Departamento:"));
        panel.add(cmbDepartamento);
        panel.add(new JLabel("Solicitante (Jefe de Área):"));
        panel.add(cmbSolicitante);
        panel.add(new JLabel("")); // spacer
        panel.add(btnGuardar);

        btnGuardar.addActionListener(e -> {
            try {
                Departamento depto = (Departamento) cmbDepartamento.getSelectedItem();
                Usuario solicitante = (Usuario) cmbSolicitante.getSelectedItem();
                String desc = txtDescripcion.getText().trim();

                if (depto == null) throw new IllegalArgumentException("Seleccione un departamento.");
                if (solicitante == null) throw new IllegalArgumentException("Seleccione un solicitante.");
                if (desc.isEmpty()) throw new IllegalArgumentException("Ingrese la descripción del caso.");

                Long casoId = casoService.solicitarCaso(desc, depto.getId(), solicitante.getId());
                JOptionPane.showMessageDialog(this,
                        "Caso abierto exitosamente!\nID del caso: " + casoId,
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                txtDescripcion.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        setContentPane(panel);
        revalidate();
        repaint();
    }

    private void cargarSolicitantesPorDepartamento(Departamento departamento, JComboBox<Usuario> cmbSolicitante) {
        cmbSolicitante.removeAllItems();
        if (departamento == null || departamento.getId() == null) return;

        try {
            List<Usuario> jefes = usuarioDAO.listarPorRol(Rol.JEFE_AREA);
            for (Usuario u : jefes) {
                if (u.getDepartamento() != null && u.getDepartamento().getId().equals(departamento.getId())) {
                    cmbSolicitante.addItem(u);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar solicitantes: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void configurarRenderersCombo(JComboBox<?> combo) {
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Departamento) {
                    Departamento d = (Departamento) value;
                    setText(d.getCodigo() + " - " + d.getNombre());
                } else if (value instanceof Usuario) {
                    Usuario u = (Usuario) value;
                    setText(u.getNombre() + " (ID: " + u.getId() + ")");
                }
                return this;
            }
        });
    }

    // 1) Interfaz escritorio - Formulario 1B: Asignacion de casos
    private void mostrarFormularioAsignacion() {
        JPanel panel = new JPanel(new GridLayout(8, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField txtCasoId = new JTextField();

        JComboBox<Usuario> cmbJefeDesarrollo;
        JComboBox<Usuario> cmbProgramador;

        JTextField txtFechaLimite = new JTextField();
        JTextArea txtAnalisis = new JTextArea(3, 20);

        JComboBox<Usuario> cmbProbador = new JComboBox<>();
        JButton btnCargarProbadores = new JButton("Cargar Probadores");
        JButton btnAsignar = new JButton("Asignar Caso");

        try {
            List<Usuario> jefes = usuarioDAO.listarPorRol(Rol.JEFE_DESARROLLO);
            cmbJefeDesarrollo = new JComboBox<>(jefes.toArray(new Usuario[0]));
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al cargar jefes de desarrollo: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        cmbProgramador = new JComboBox<>();

        configurarRenderersCombo(cmbJefeDesarrollo);
        configurarRenderersCombo(cmbProgramador);
        configurarRenderersCombo(cmbProbador);

        cmbJefeDesarrollo.addActionListener(e -> {
            Usuario jefe = (Usuario) cmbJefeDesarrollo.getSelectedItem();
            if (jefe != null) {
                cargarProgramadoresPorJefe(jefe.getId(), cmbProgramador);
            }
        });

        // Cargar programadores iniciales (si aplica)
        Usuario jefeInicial = (Usuario) cmbJefeDesarrollo.getSelectedItem();
        if (jefeInicial != null) {
            cargarProgramadoresPorJefe(jefeInicial.getId(), cmbProgramador);
        }

        btnCargarProbadores.addActionListener(e -> {
            try {
                Long casoId = Long.parseLong(txtCasoId.getText().trim());
                Caso caso = casoService.obtenerCasoPorId(casoId);
                if (caso == null) throw new IllegalArgumentException("Caso no encontrado.");
                if (caso.getDepartamento() == null) throw new IllegalArgumentException("El caso no tiene departamento.");

                cargarProbadoresPorDepartamento(caso.getDepartamento().getId(), cmbProbador);
                JOptionPane.showMessageDialog(this, "Probadores cargados para el departamento del caso.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al cargar probadores: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnAsignar.addActionListener(e -> {
            try {
                Long casoId = Long.parseLong(txtCasoId.getText().trim());
                Usuario jefe = (Usuario) cmbJefeDesarrollo.getSelectedItem();
                Usuario programador = (Usuario) cmbProgramador.getSelectedItem();
                Usuario probador = (Usuario) cmbProbador.getSelectedItem();

                if (jefe == null) throw new IllegalArgumentException("Seleccione un jefe de desarrollo.");
                if (programador == null) throw new IllegalArgumentException("Seleccione un programador.");
                if (probador == null) throw new IllegalArgumentException("Seleccione un probador.");

                java.sql.Date fechaLimite = parseFechaSql(txtFechaLimite.getText().trim(), "yyyy-MM-dd");
                String analisis = txtAnalisis.getText().trim();
                if (analisis.isEmpty()) throw new IllegalArgumentException("Ingrese el análisis/descripcion.");

                casoService.asignarProgramador(
                        casoId,
                        jefe.getId(),
                        programador.getId(),
                        fechaLimite,
                        analisis,
                        probador.getId()
                );

                JOptionPane.showMessageDialog(this, "Caso asignado correctamente!");
                txtAnalisis.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(new JLabel("ID del Caso:"));
        panel.add(txtCasoId);
        panel.add(new JLabel("Jefe de Desarrollo:"));
        panel.add(cmbJefeDesarrollo);
        panel.add(new JLabel("Programador:"));
        panel.add(cmbProgramador);
        panel.add(new JLabel("Fecha límite (yyyy-MM-dd):"));
        panel.add(txtFechaLimite);
        panel.add(new JLabel("Análisis/Descripción del trabajo:"));
        panel.add(new JScrollPane(txtAnalisis));
        panel.add(new JLabel("Probador (empleado del depto del caso):"));
        panel.add(cmbProbador);
        panel.add(new JLabel(""));
        panel.add(btnCargarProbadores);
        panel.add(new JLabel(""));
        panel.add(btnAsignar);

        setContentPane(panel);
        revalidate();
        repaint();
    }

    private void cargarProgramadoresPorJefe(Long jefeDesarrolloId, JComboBox<Usuario> cmbProgramador) {
        cmbProgramador.removeAllItems();
        if (jefeDesarrolloId == null) return;
        try {
            List<Usuario> programadores = usuarioDAO.listarProgramadoresPorJefe(jefeDesarrolloId);
            for (Usuario p : programadores) {
                cmbProgramador.addItem(p);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar programadores: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cargarProbadoresPorDepartamento(Long departamentoId, JComboBox<Usuario> cmbProbador) {
        cmbProbador.removeAllItems();
        if (departamentoId == null) return;
        try {
            List<Usuario> empleados = usuarioDAO.listarPorRol(Rol.EMPLEADO);
            for (Usuario u : empleados) {
                if (u.getDepartamento() != null && u.getDepartamento().getId().equals(departamentoId)) {
                    cmbProbador.addItem(u);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al cargar probadores: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // 1) Interfaz escritorio - Formulario 1C: Consulta de bitacora
    private void mostrarFormularioConsultaBitacora() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel panelEntrada = new JPanel(new FlowLayout());
        JTextField txtCasoId = new JTextField(12);
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
                List<Bitacora> bitacoras = casoService.obtenerBitacoras(casoId);

                StringBuilder sb = new StringBuilder("=== BITÁCORA DEL CASO " + casoId + " ===\n\n");
                if (bitacoras.isEmpty()) {
                    sb.append("No hay registros en la bitácora para este caso.");
                } else {
                    for (Bitacora b : bitacoras) {
                        sb.append("Fecha: ").append(b.getFecha()).append("\n");
                        sb.append("Avance: ").append(b.getPorcentajeAvance()).append("%\n");
                        sb.append("Descripción: ").append(b.getDescripcion()).append("\n\n");
                    }
                }
                txtResultado.setText(sb.toString());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        setContentPane(panel);
        revalidate();
        repaint();
    }

    // 1) Interfaz escritorio - Formulario 1D: Registrar bitacora
    private void mostrarFormularioRegistrarBitacora() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField txtCasoId = new JTextField();
        JTextArea txtDescripcion = new JTextArea(3, 20);
        JTextField txtPorcentaje = new JTextField();

        JButton btnGuardar = new JButton("Registrar Bitácora");

        panel.add(new JLabel("ID del Caso:"));
        panel.add(txtCasoId);
        panel.add(new JLabel("Descripción del trabajo:"));
        panel.add(new JScrollPane(txtDescripcion));
        panel.add(new JLabel("Porcentaje de Avance (0-100):"));
        panel.add(txtPorcentaje);
        panel.add(new JLabel(""));
        panel.add(btnGuardar);

        btnGuardar.addActionListener(e -> {
            try {
                Long casoId = Long.parseLong(txtCasoId.getText().trim());
                String desc = txtDescripcion.getText().trim();
                int porc = Integer.parseInt(txtPorcentaje.getText().trim());

                if (desc.isEmpty()) throw new IllegalArgumentException("Ingrese una descripción.");

                casoService.registrarBitacora(casoId, desc, porc);
                JOptionPane.showMessageDialog(this, "Bitácora registrada correctamente.");
                txtDescripcion.setText("");
                txtPorcentaje.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        setContentPane(panel);
        revalidate();
        repaint();
    }

    // 1) Interfaz escritorio - Formulario 1E: Subida de PDF (en escritorio)
    private void mostrarFormularioSubirPDF() {
        JPanel panel = new JPanel(new GridLayout(7, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField txtCasoId = new JTextField();
        JTextField txtUsuarioSubioId = new JTextField();

        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("PDF Files", "pdf"));

        JLabel lblArchivo = new JLabel("Ningún archivo seleccionado");
        final File[] archivoSeleccionado = new File[1];

        JComboBox<ArchivoAdjunto.TipoArchivo> cmbTipo = new JComboBox<>(ArchivoAdjunto.TipoArchivo.values());
        configurarRenderersComboTipoArchivo(cmbTipo);

        JButton btnSeleccionar = new JButton("Seleccionar PDF");
        JButton btnSubir = new JButton("Subir PDF");

        btnSeleccionar.addActionListener(e -> {
            int resultado = fc.showOpenDialog(this);
            if (resultado == JFileChooser.APPROVE_OPTION) {
                archivoSeleccionado[0] = fc.getSelectedFile();
                lblArchivo.setText("Archivo: " + archivoSeleccionado[0].getName());
            }
        });

        btnSubir.addActionListener(e -> {
            try {
                if (archivoSeleccionado[0] == null) throw new IllegalArgumentException("Seleccione un PDF primero.");

                Long casoId = Long.parseLong(txtCasoId.getText().trim());
                Long usuarioSubioId = Long.parseLong(txtUsuarioSubioId.getText().trim());

                Caso caso = casoService.obtenerCasoPorId(casoId);
                if (caso == null) throw new IllegalArgumentException("Caso no encontrado.");

                Usuario usuarioSubio = usuarioDAO.buscarPorId(usuarioSubioId);
                if (usuarioSubio == null) throw new IllegalArgumentException("Usuario no encontrado.");

                ArchivoAdjunto archivo = new ArchivoAdjunto();
                archivo.setCaso(caso);
                archivo.setNombreArchivo(archivoSeleccionado[0].getName());
                archivo.setRuta(archivoSeleccionado[0].getAbsolutePath());
                archivo.setTipo((ArchivoAdjunto.TipoArchivo) cmbTipo.getSelectedItem());
                archivo.setUsuarioSubio(usuarioSubio);

                archivoAdjuntoDAO.insertar(archivo);

                // Intentar registrar una entrada en bitácora sin cambiar el % actual.
                try {
                    casoService.registrarBitacora(
                            casoId,
                            "PDF subido: " + archivo.getNombreArchivo(),
                            caso.getPorcentajeAvance() != null ? caso.getPorcentajeAvance() : 0
                    );
                } catch (Exception ignored) {
                    // Bitácora depende del estado del caso; no bloqueamos el upload.
                }

                JOptionPane.showMessageDialog(this, "PDF subido correctamente!");
                lblArchivo.setText("Ningún archivo seleccionado");
                archivoSeleccionado[0] = null;
                txtCasoId.setText("");
                txtUsuarioSubioId.setText("");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error al subir: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(new JLabel("ID del Caso:"));
        panel.add(txtCasoId);
        panel.add(new JLabel("ID del Usuario que Sube:"));
        panel.add(txtUsuarioSubioId);
        panel.add(new JLabel("Tipo de Archivo:"));
        panel.add(cmbTipo);
        panel.add(new JLabel("PDF:"));
        panel.add(btnSeleccionar);
        panel.add(new JLabel(" "));
        panel.add(lblArchivo);
        panel.add(new JLabel(""));
        panel.add(btnSubir);

        setContentPane(panel);
        revalidate();
        repaint();
    }

    private void configurarRenderersComboTipoArchivo(JComboBox<?> combo) {
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof ArchivoAdjunto.TipoArchivo) {
                    ArchivoAdjunto.TipoArchivo t = (ArchivoAdjunto.TipoArchivo) value;
                    setText(t.name());
                }
                return this;
            }
        });
    }

    // 1) Interfaz escritorio - Formulario 1F: Vista general de avance
    private void mostrarFormularioVistaAvance() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel panelEntrada = new JPanel(new FlowLayout());
        JTextField txtCasoId = new JTextField(12);
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
                Caso caso = casoService.obtenerCasoPorId(casoId);
                if (caso == null) throw new IllegalArgumentException("Caso no encontrado.");

                StringBuilder sb = new StringBuilder("=== VISTA DE AVANCE DEL CASO " + casoId + " ===\n\n");
                sb.append("Descripción del Caso: ").append(caso.getDescripcionSolicitud()).append("\n");
                sb.append("Porcentaje de Avance: ").append(caso.getPorcentajeAvance()).append("%\n");
                sb.append("Estado Actual: ").append(caso.getEstado()).append("\n");

                txtResultado.setText(sb.toString());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        setContentPane(panel);
        revalidate();
        repaint();
    }

    private java.sql.Date parseFechaSql(String text, String pattern) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        sdf.setLenient(false);
        Date parsed = sdf.parse(text);
        return new java.sql.Date(parsed.getTime());
    }

    // --- Método Principal para Ejecutar ---
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            String rol = JOptionPane.showInputDialog(null,
                    "Por favor, ingrese su rol:\n(ADMIN, JEFE_AREA, JEFE_DESARROLLO, PROGRAMADOR, EMPLEADO)",
                    "Autenticación de Rol",
                    JOptionPane.QUESTION_MESSAGE);
            InterfazPunto1 ventana = new InterfazPunto1(rol);
            ventana.setVisible(true);
        });
    }
}

