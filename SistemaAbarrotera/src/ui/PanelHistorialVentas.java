package ui;

import modelo.*;
import servicio.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PanelHistorialVentas extends JPanel {
    private VentaService ventaService;
    private JTable tablaVentas;
    private DefaultTableModel modeloTabla;
    private JTextField txtBusqueda;
    private JLabel lblTotalVentas, lblMontoTotal, lblVentasHoy, lblMontoHoy;

    private static final Color COLOR_PRINCIPAL = new Color(44, 62, 80);
    private static final Color COLOR_CLARO = new Color(52, 73, 94);

    public PanelHistorialVentas(VentaService ventaService) {
        this.ventaService = ventaService;
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        crearComponentes();
    }

    private void crearComponentes() {
        add(crearPanelTitulo(), BorderLayout.NORTH);
        JPanel panelCentral = new JPanel(new BorderLayout(0, 10));
        panelCentral.setBackground(Color.WHITE);
        panelCentral.add(crearPanelBusqueda(), BorderLayout.NORTH);
        panelCentral.add(crearPanelTabla(), BorderLayout.CENTER);
        add(panelCentral, BorderLayout.CENTER);
        add(crearPanelEstadisticas(), BorderLayout.SOUTH);
    }

    private JPanel crearPanelTitulo() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_PRINCIPAL);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        JLabel lblTitulo = new JLabel("Historial de Ventas");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        panel.add(lblTitulo, BorderLayout.WEST);

        JButton btnVerDetalle = new JButton("Ver Detalle");
        btnVerDetalle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnVerDetalle.setBackground(COLOR_CLARO);
        btnVerDetalle.setForeground(Color.WHITE);
        btnVerDetalle.setFocusPainted(false);
        btnVerDetalle.setBorderPainted(false);
        btnVerDetalle.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnVerDetalle.addActionListener(e -> verDetalleVenta());
        panel.add(btnVerDetalle, BorderLayout.EAST);
        return panel;
    }

    private JPanel crearPanelBusqueda() {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        JLabel lbl = new JLabel("Buscar venta:");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panel.add(lbl, BorderLayout.WEST);
        txtBusqueda = new JTextField();
        txtBusqueda.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtBusqueda.setToolTipText("Buscar por ID, producto, cliente o método de pago");
        txtBusqueda.addActionListener(e -> buscarVentas());
        panel.add(txtBusqueda, BorderLayout.CENTER);

        JPanel panelBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panelBtns.setBackground(Color.WHITE);
        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnBuscar.setBackground(COLOR_PRINCIPAL);
        btnBuscar.setForeground(Color.WHITE);
        btnBuscar.setFocusPainted(false);
        btnBuscar.setBorderPainted(false);
        btnBuscar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBuscar.addActionListener(e -> buscarVentas());
        panelBtns.add(btnBuscar);
        JButton btnTodas = new JButton("Mostrar Todas");
        btnTodas.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnTodas.setBackground(new Color(127, 140, 141));
        btnTodas.setForeground(Color.WHITE);
        btnTodas.setFocusPainted(false);
        btnTodas.setBorderPainted(false);
        btnTodas.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTodas.addActionListener(e -> { txtBusqueda.setText(""); actualizarTabla(ventaService.obtenerVentas()); });
        panelBtns.add(btnTodas);
        JButton btnHoy = new JButton("Solo Hoy");
        btnHoy.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnHoy.setBackground(new Color(39, 174, 96));
        btnHoy.setForeground(Color.WHITE);
        btnHoy.setFocusPainted(false);
        btnHoy.setBorderPainted(false);
        btnHoy.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnHoy.addActionListener(e -> actualizarTabla(ventaService.obtenerVentasDelDia(LocalDate.now())));
        panelBtns.add(btnHoy);
        panel.add(panelBtns, BorderLayout.EAST);
        return panel;
    }

    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(COLOR_PRINCIPAL, 2),
                "Ventas Registradas", 0, 0, new Font("Segoe UI", Font.BOLD, 14), COLOR_PRINCIPAL),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        String[] columnas = {"ID Venta", "Fecha", "Hora", "Artículos", "Total", "Método Pago", "Cliente"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaVentas = new JTable(modeloTabla);
        tablaVentas.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaVentas.setRowHeight(28);
        tablaVentas.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaVentas.getTableHeader().setBackground(COLOR_PRINCIPAL);
        tablaVentas.getTableHeader().setForeground(Color.WHITE);
        tablaVentas.setSelectionBackground(new Color(174, 182, 191));

        tablaVentas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) verDetalleVenta();
            }
        });

        JScrollPane scroll = new JScrollPane(tablaVentas);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199)));
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelEstadisticas() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 15, 0));
        panel.setBackground(new Color(236, 240, 241));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        lblTotalVentas = crearLabelStat("Total Ventas: 0", new Color(52, 73, 94));
        panel.add(lblTotalVentas);
        lblMontoTotal = crearLabelStat("Monto Total: $0.00", new Color(39, 174, 96));
        panel.add(lblMontoTotal);
        lblVentasHoy = crearLabelStat("Ventas Hoy: 0", new Color(41, 128, 185));
        panel.add(lblVentasHoy);
        lblMontoHoy = crearLabelStat("Monto Hoy: $0.00", new Color(230, 126, 34));
        panel.add(lblMontoHoy);
        return panel;
    }

    private JLabel crearLabelStat(String texto, Color color) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(color);
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        return lbl;
    }

    private void verDetalleVenta() {
        int fila = tablaVentas.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una venta", "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String idVenta = (String) modeloTabla.getValueAt(fila, 0);
        Venta v = ventaService.buscarPorId(idVenta);
        if (v == null) return;

        StringBuilder detalle = new StringBuilder();
        detalle.append("Venta: ").append(v.getIdVenta()).append("\n");
        detalle.append("Fecha: ").append(v.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
               .append(" ").append(v.getHora()).append("\n");
        detalle.append("Cliente: ").append(v.getCliente()).append("\n");
        detalle.append("Pago: ").append(v.getMetodoPago()).append("\n\n");
        detalle.append("─── Productos ───\n");
        for (int i = 0; i < v.getTotalDetalles(); i++) {
            DetalleVenta d = v.getDetalles()[i];
            detalle.append(d.getNombreProducto())
                   .append("  ×").append(d.getCantidad())
                   .append("  $").append(String.format("%.2f", d.getPrecioUnitario()))
                   .append("  = $").append(String.format("%.2f", d.getSubtotal())).append("\n");
        }
        detalle.append("\nTOTAL: $").append(String.format("%.2f", v.getTotal()));

        JTextArea txtDetalle = new JTextArea(detalle.toString());
        txtDetalle.setFont(new Font("Courier New", Font.PLAIN, 12));
        txtDetalle.setEditable(false);
        JScrollPane scroll = new JScrollPane(txtDetalle);
        scroll.setPreferredSize(new Dimension(400, 350));
        JOptionPane.showMessageDialog(this, scroll, "Detalle - " + idVenta, JOptionPane.INFORMATION_MESSAGE);
    }

    private void buscarVentas() {
        String termino = txtBusqueda.getText().trim();
        if (termino.isEmpty()) {
            actualizarTabla(ventaService.obtenerVentas());
        } else {
            Venta[] resultados = ventaService.buscarVentas(termino);
            actualizarTabla(resultados);
            if (resultados.length == 0)
                JOptionPane.showMessageDialog(this, "No se encontraron ventas con: " + termino,
                    "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void actualizarTabla(Venta[] lista) {
        modeloTabla.setRowCount(0);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        for (Venta v : lista) {
            Object[] fila = {
                v.getIdVenta(), v.getFecha().format(fmt), v.getHora(),
                v.getTotalArticulos(), String.format("$%.2f", v.getTotal()),
                v.getMetodoPago(), v.getCliente()
            };
            modeloTabla.addRow(fila);
        }
        actualizarEstadisticas();
    }

    public void refrescar() { actualizarTabla(ventaService.obtenerVentas()); }

    private void actualizarEstadisticas() {
        lblTotalVentas.setText("Total Ventas: " + ventaService.getTotalVentas());
        lblMontoTotal.setText("Monto Total: " + String.format("$%.2f", ventaService.calcularTotalVentas()));
        Venta[] hoy = ventaService.obtenerVentasDelDia(LocalDate.now());
        lblVentasHoy.setText("Ventas Hoy: " + hoy.length);
        lblMontoHoy.setText("Monto Hoy: " + String.format("$%.2f", ventaService.calcularVentasDelDia(LocalDate.now())));
    }
}
