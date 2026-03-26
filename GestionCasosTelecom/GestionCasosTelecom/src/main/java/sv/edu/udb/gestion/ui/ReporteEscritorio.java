//3) Interfaz de escritorio para ejecutar el reporte por rango de fechas
package sv.edu.udb.gestion.ui;

import sv.edu.udb.gestion.dao.CasoDAO;
import sv.edu.udb.gestion.service.ReporteService;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;

public class ReporteEscritorio extends JFrame {
    private final CasoDAO casoDAO;
    private final ReporteService reporteService;

    public ReporteEscritorio() {
        //3) Preparación de dependencias (Persona 3: reportes)
        this.casoDAO = new CasoDAO();
        this.reporteService = new ReporteService(casoDAO);

        configurarVentana();
        mostrarFormularioReporte();
    }

    private void configurarVentana() {
        setTitle("Generador de Reportes (Punto 3)");
        setSize(480, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void mostrarFormularioReporte() {
        JPanel panel = new JPanel(new GridLayout(4, 2, 10, 10));
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
                // Validar formato de fechas
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                sdf.setLenient(false);

                java.util.Date utilInicio = sdf.parse(txtFechaInicio.getText().trim());
                java.util.Date utilFin = sdf.parse(txtFechaFin.getText().trim());

                java.sql.Date fechaInicio = new java.sql.Date(utilInicio.getTime());
                java.sql.Date fechaFin = new java.sql.Date(utilFin.getTime());

                //3) Lógica de Persona 3 (service) para generar el reporte (se imprime en consola)
                reporteService.generarReportePorFechas(fechaInicio, fechaFin);

                JOptionPane.showMessageDialog(this,
                        "Reporte generado. Revise la consola para ver los resultados.",
                        "Reporte Generado",
                        JOptionPane.INFORMATION_MESSAGE);

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
                ex.printStackTrace(); // depuración
            }
        });

        setContentPane(panel);
        revalidate();
        repaint();
    }

    // Método Principal para Ejecutar esta Ventana
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ReporteEscritorio ventana = new ReporteEscritorio();
            ventana.setVisible(true);
        });
    }
}

