package sv.edu.udb.gestion.ui;

import sv.edu.udb.gestion.entity.Bitacora;
import sv.edu.udb.gestion.entity.Caso;
import sv.edu.udb.gestion.service.CasoService;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class VistaProgresoEscritorio extends JFrame {
    // ------------------------------------------------------------
    // 2) Vista de progreso (Punto 2 del PDF)
    //    Esta vista muestra:
    //    - El porcentaje de avance del caso (Caso.porcentajeAvance)
    //    - La bitacora cronologica (ordenada por fecha desde BitacoraDAO)
    // ------------------------------------------------------------
    private final CasoService casoService;

    public VistaProgresoEscritorio() {
        this.casoService = new CasoService();

        configurarVentana();
        mostrarFormularioVistaProgreso();
    }

    private void configurarVentana() {
        setTitle("Vista de Progreso del Caso");
        setSize(650, 520);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    private void mostrarFormularioVistaProgreso() {
        JPanel panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel panelEntrada = new JPanel(new FlowLayout());
        JTextField txtCasoId = new JTextField(10);
        JButton btnConsultar = new JButton("Consultar Avance");
        panelEntrada.add(new JLabel("ID del Caso:"));
        panelEntrada.add(txtCasoId);
        panelEntrada.add(btnConsultar);

        JTextArea txtResultado = new JTextArea();
        txtResultado.setEditable(false);
        JScrollPane scrollResultado = new JScrollPane(txtResultado);

        panelPrincipal.add(panelEntrada, BorderLayout.NORTH);
        panelPrincipal.add(scrollResultado, BorderLayout.CENTER);

        btnConsultar.addActionListener(e -> {
            try {
                Long casoId = Long.parseLong(txtCasoId.getText().trim());

                Caso caso = casoService.obtenerCasoPorId(casoId);
                if (caso == null) throw new IllegalArgumentException("Caso no encontrado.");

                List<Bitacora> bitacoras = casoService.obtenerBitacoras(casoId);

                StringBuilder sb = new StringBuilder("=== VISTA DE PROGRESO DEL CASO ===\n\n");
                sb.append("ID Caso: ").append(casoId).append("\n");
                sb.append("Descripción del Caso: ").append(caso.getDescripcionSolicitud()).append("\n");
                sb.append("Porcentaje de Avance: ").append(caso.getPorcentajeAvance()).append("%\n");
                sb.append("Estado Actual: ").append(caso.getEstado()).append("\n\n");

                sb.append("=== BITÁCORA CRONOLÓGICA ===\n\n");
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
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Por favor, ingrese un ID de caso válido (número).",
                        "Error de formato",
                        JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error al obtener datos: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        add(panelPrincipal);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new VistaProgresoEscritorio().setVisible(true));
    }
}

