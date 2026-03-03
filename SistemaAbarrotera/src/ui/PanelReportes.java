package ui;

import modelo.*;
import servicio.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class PanelReportes extends JPanel {
    private CompraService compraService;
    private VentaService ventaService;
    private ProductoService productoService;
    private ClienteService clienteService;
    private ProveedorService proveedorService;

    private JLabel lblVentasHoy, lblVentasTotal, lblComprasTotal, lblGanancia;
    private JLabel lblProductos, lblInventarioValor, lblClientes, lblProveedoresLbl;
    private DefaultTableModel modeloTopProductos, modeloUltimasVentas, modeloBajoStock, modeloProveedores;

    private static final Color COLOR_PRINCIPAL = new Color(155, 89, 182);

    public PanelReportes(CompraService compraService, VentaService ventaService, ProductoService productoService,
                         ClienteService clienteService, ProveedorService proveedorService) {
        this.compraService = compraService;
        this.ventaService = ventaService;
        this.productoService = productoService;
        this.clienteService = clienteService;
        this.proveedorService = proveedorService;
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        crearComponentes();
    }

    private void crearComponentes() {
        add(crearPanelTitulo(), BorderLayout.NORTH);
        JPanel contenido = new JPanel(new BorderLayout(10, 10));
        contenido.setBackground(Color.WHITE);
        contenido.add(crearPanelTarjetas(), BorderLayout.NORTH);

        JPanel panelTablas = new JPanel(new GridLayout(2, 2, 10, 10));
        panelTablas.setBackground(Color.WHITE);
        panelTablas.add(crearPanelTopProductos());
        panelTablas.add(crearPanelUltimasVentas());
        panelTablas.add(crearPanelBajoStock());
        panelTablas.add(crearPanelProveedores());
        contenido.add(panelTablas, BorderLayout.CENTER);
        add(contenido, BorderLayout.CENTER);
    }

    private JPanel crearPanelTitulo() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_PRINCIPAL);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        JLabel lblTitulo = new JLabel("Dashboard - Reportes y Estadísticas");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitulo.setForeground(Color.WHITE);
        panel.add(lblTitulo, BorderLayout.WEST);

        JButton btnRefrescar = new JButton("Actualizar Datos");
        btnRefrescar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnRefrescar.setBackground(new Color(142, 68, 173));
        btnRefrescar.setForeground(Color.WHITE);
        btnRefrescar.setFocusPainted(false);
        btnRefrescar.setBorderPainted(false);
        btnRefrescar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefrescar.addActionListener(e -> actualizarReportes());
        panel.add(btnRefrescar, BorderLayout.EAST);
        return panel;
    }

    private JPanel crearPanelTarjetas() {
        JPanel panel = new JPanel(new GridLayout(2, 4, 10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        lblVentasHoy = new JLabel("$0.00");
        panel.add(crearTarjeta("Ventas Hoy", lblVentasHoy, new Color(39, 174, 96)));
        lblVentasTotal = new JLabel("$0.00");
        panel.add(crearTarjeta("Ventas Totales", lblVentasTotal, new Color(41, 128, 185)));
        lblComprasTotal = new JLabel("0");
        panel.add(crearTarjeta("Compras Totales", lblComprasTotal, new Color(231, 76, 60)));
        lblGanancia = new JLabel("$0.00");
        panel.add(crearTarjeta("Ganancia Estimada", lblGanancia, new Color(243, 156, 18)));

        lblProductos = new JLabel("0");
        panel.add(crearTarjeta("Productos Activos", lblProductos, new Color(230, 126, 34)));
        lblInventarioValor = new JLabel("$0.00");
        panel.add(crearTarjeta("Valor Inventario", lblInventarioValor, new Color(22, 160, 133)));
        lblClientes = new JLabel("0");
        panel.add(crearTarjeta("Clientes Activos", lblClientes, new Color(39, 174, 96)));
        lblProveedoresLbl = new JLabel("0");
        panel.add(crearTarjeta("Proveedores", lblProveedoresLbl, COLOR_PRINCIPAL));

        return panel;
    }

    private JPanel crearTarjeta(String titulo, JLabel lblValor, Color color) {
        JPanel tarjeta = new JPanel(new BorderLayout());
        tarjeta.setBackground(color);
        tarjeta.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));

        JLabel lblTit = new JLabel(titulo);
        lblTit.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblTit.setForeground(new Color(255, 255, 255, 200));
        tarjeta.add(lblTit, BorderLayout.NORTH);

        lblValor.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblValor.setForeground(Color.WHITE);
        tarjeta.add(lblValor, BorderLayout.CENTER);
        return tarjeta;
    }

    private JPanel crearPanelTopProductos() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(39, 174, 96), 2),
                "Top Productos Vendidos", 0, 0, new Font("Segoe UI", Font.BOLD, 12), new Color(39, 174, 96)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        modeloTopProductos = new DefaultTableModel(new String[]{"Producto", "Unidades"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        panel.add(new JScrollPane(crearTablaReporte(modeloTopProductos, new Color(39, 174, 96))), BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelUltimasVentas() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(41, 128, 185), 2),
                "Últimas Ventas", 0, 0, new Font("Segoe UI", Font.BOLD, 12), new Color(41, 128, 185)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        modeloUltimasVentas = new DefaultTableModel(new String[]{"ID", "Fecha", "Total", "Método"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        panel.add(new JScrollPane(crearTablaReporte(modeloUltimasVentas, new Color(41, 128, 185))), BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelBajoStock() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(231, 76, 60), 2),
                "Alertas de Inventario", 0, 0, new Font("Segoe UI", Font.BOLD, 12), new Color(231, 76, 60)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        modeloBajoStock = new DefaultTableModel(new String[]{"Producto", "Stock", "Mín.", "Estado"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable tabla = crearTablaReporte(modeloBajoStock, new Color(231, 76, 60));
        tabla.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(CENTER);
                if (!isSelected) {
                    if ("SIN STOCK".equals(value)) {
                        c.setBackground(new Color(255, 200, 200));
                        c.setForeground(new Color(192, 57, 43));
                    } else {
                        c.setBackground(new Color(255, 248, 220));
                        c.setForeground(new Color(243, 156, 18));
                    }
                }
                return c;
            }
        });
        panel.add(new JScrollPane(tabla), BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelProveedores() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(COLOR_PRINCIPAL, 2),
                "Resumen Proveedores / Compras", 0, 0, new Font("Segoe UI", Font.BOLD, 12), COLOR_PRINCIPAL),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        modeloProveedores = new DefaultTableModel(new String[]{"Proveedor", "Compras", "Costo Total"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        panel.add(new JScrollPane(crearTablaReporte(modeloProveedores, COLOR_PRINCIPAL)), BorderLayout.CENTER);
        return panel;
    }

    private JTable crearTablaReporte(DefaultTableModel modelo, Color headerColor) {
        JTable tabla = new JTable(modelo);
        tabla.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        tabla.setRowHeight(24);
        tabla.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));
        tabla.getTableHeader().setBackground(headerColor);
        tabla.getTableHeader().setForeground(Color.BLACK);
        tabla.setSelectionBackground(new Color(215, 189, 226));
        return tabla;
    }

    public void actualizarReportes() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate hoy = LocalDate.now();

        double ventasHoy = ventaService.calcularVentasDelDia(hoy);
        double ventasTotal = ventaService.calcularTotalVentas();
        double costosTotal = compraService.calcularTotalCostos();
        double ganancia = ventasTotal - costosTotal;

        lblVentasHoy.setText(String.format("$%.2f", ventasHoy));
        lblVentasTotal.setText(String.format("$%.2f", ventasTotal));
        lblComprasTotal.setText(String.valueOf(compraService.getTotalCompras()));
        lblGanancia.setText(String.format("$%.2f", ganancia));
        lblProductos.setText(String.valueOf(productoService.getTotalProductosActivos()));
        lblInventarioValor.setText(String.format("$%.2f", productoService.getValorInventarioVenta()));
        lblClientes.setText(String.valueOf(clienteService.getTotalClientesActivos()));
        lblProveedoresLbl.setText(String.valueOf(proveedorService.getTotalProveedoresActivos()));

        // Top Productos
        modeloTopProductos.setRowCount(0);
        String[] topN = ventaService.getTopProductosNombres(10);
        int[] topC = ventaService.getTopProductosCantidades(10);
        for (int i = 0; i < topN.length; i++) {
            modeloTopProductos.addRow(new Object[]{topN[i], topC[i]});
        }

        // Últimas Ventas
        modeloUltimasVentas.setRowCount(0);
        Venta[] ultimas = ventaService.obtenerUltimasVentas(10);
        for (Venta v : ultimas) {
            modeloUltimasVentas.addRow(new Object[]{
                v.getIdVenta(), v.getFecha().format(fmt),
                String.format("$%.2f", v.getTotal()), v.getMetodoPago()
            });
        }

        // Alertas Bajo Stock
        modeloBajoStock.setRowCount(0);
        Producto[] bajoStock = productoService.obtenerProductosBajoStock();
        for (Producto p : bajoStock) {
            modeloBajoStock.addRow(new Object[]{
                p.getNombre(), p.getStock(), p.getStockMinimo(),
                p.isSinStock() ? "SIN STOCK" : "BAJO"
            });
        }

        // Proveedores
        modeloProveedores.setRowCount(0);
        Map<String, Integer> comprasPorProv = compraService.getComprasPorProveedor();
        Map<String, Double> costosPorProv = compraService.getCostosPorProveedor();
        for (String prov : comprasPorProv.keySet()) {
            modeloProveedores.addRow(new Object[]{
                prov, comprasPorProv.get(prov),
                String.format("$%.2f", costosPorProv.getOrDefault(prov, 0.0))
            });
        }
    }
}
