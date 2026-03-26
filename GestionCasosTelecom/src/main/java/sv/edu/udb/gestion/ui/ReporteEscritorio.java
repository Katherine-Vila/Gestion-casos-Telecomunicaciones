package sv.edu.udb.gestion.ui;

import sv.edu.udb.gestion.dao.CasoDAO;
import sv.edu.udb.gestion.entity.EstadoCaso;
import sv.edu.udb.gestion.service.ReporteService;

import javax.swing.*;
import java.awt.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Map;

public class ReporteEscritorio extends JFrame {
    // ------------------------------------------------------------
    // 3) Reportes por rango de fechas (Punto 3 del PDF)
    //    Esta UI pide fechaInicio y fechaFin y llama a ReporteService,
    //    el cual consulta al DAO y muestra el resumen (y agrega salida por consola).
    // ------------------------------------------------------------
    private final CasoDAO casoDAO;
    private final ReporteService reporteService;

    public ReporteEscritorio() {
        this.casoDAO = new CasoDAO();
        this.reporteService = new ReporteService(casoDAO);

        configurarVentana();
        mostrarFormularioReporte();
    }

    private void configurarVentana() {
        setTitle("Generador de Reportes (Rango de Fechas)");
        setSize(460, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    private void mostrarFormularioReporte() {
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField txtFechaInicio = new JTextField();
        JTextField txtFechaFin = new JTextField();
        JButton btnGenerar = new JButton("Generar Reporte");

        panel.add(new JLabel("Fecha Inicio (YYYY-MM-DD):"));
        panel.add(txtFechaInicio);
        panel.add(new JLabel("Fecha Fin (YYYY-MM-DD):"));
        panel.add(txtFechaFin);
        panel.add(new JLabel(""));
        panel.add(btnGenerar);

        btnGenerar.addActionListener(e -> {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                sdf.setLenient(false);

                Date fechaInicio = new Date(sdf.parse(txtFechaInicio.getText().trim()).getTime());
                Date fechaFin = new Date(sdf.parse(txtFechaFin.getText().trim()).getTime());

                Map<EstadoCaso, Integer> conteo = reporteService.generarReportePorFechas(fechaInicio, fechaFin);

                String resumen =
                        "Reporte generado exitosamente.\n\n" +
                        "FINALIZADO: " + conteo.getOrDefault(EstadoCaso.FINALIZADO, 0) + "\n" +
                        "EN_DESARROLLO: " + conteo.getOrDefault(EstadoCaso.EN_DESARROLLO, 0) + "\n" +
                        "RECHAZADO: " + conteo.getOrDefault(EstadoCaso.RECHAZADO, 0) + "\n";

                JOptionPane.showMessageDialog(this, resumen, "Reporte Generado", JOptionPane.INFORMATION_MESSAGE);

                txtFechaInicio.setText("");
                txtFechaFin.setText("");
            } catch (java.text.ParseException ex) {
                JOptionPane.showMessageDialog(this,
                        "Formato de fecha inválido. Use YYYY-MM-DD.",
                        "Error de Formato",
                        JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Error al generar el reporte: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        add(panel, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ReporteEscritorio().setVisible(true));
    }
}

