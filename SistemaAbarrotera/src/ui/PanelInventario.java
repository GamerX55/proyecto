package ui;

import modelo.*;
import servicio.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

public class PanelInventario extends JPanel {
    private ProductoService productoService;
    private JTable tablaInventario;
    private DefaultTableModel modeloTabla;
    private JTextField txtBusqueda;
    private JLabel lblTotalUnidades, lblValorCosto, lblValorVenta, lblBajoStock, lblSinStock;
    private JComboBox<String> comboFiltro;

    private static final Color COLOR_PRINCIPAL = new Color(22, 160, 133);
    private static final Color COLOR_CLARO = new Color(26, 188, 156);

    public PanelInventario(ProductoService productoService) {
        this.productoService = productoService;
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        crearComponentes();
    }

    private void crearComponentes() {
        add(crearPanelTitulo(), BorderLayout.NORTH);
        JPanel panelCentral = new JPanel(new BorderLayout(0, 10));
        panelCentral.setBackground(Color.WHITE);
        panelCentral.add(crearPanelFiltros(), BorderLayout.NORTH);
        panelCentral.add(crearPanelTabla(), BorderLayout.CENTER);
        add(panelCentral, BorderLayout.CENTER);
        add(crearPanelEstadisticas(), BorderLayout.SOUTH);
    }

    private JPanel crearPanelTitulo() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_PRINCIPAL);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        JLabel lblTitulo = new JLabel("Control de Inventario");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        panel.add(lblTitulo, BorderLayout.WEST);

        JButton btnAjustar = new JButton("Ajustar Stock");
        btnAjustar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnAjustar.setBackground(COLOR_CLARO);
        btnAjustar.setForeground(Color.WHITE);
        btnAjustar.setFocusPainted(false);
        btnAjustar.setBorderPainted(false);
        btnAjustar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAjustar.addActionListener(e -> ajustarStockSeleccionado());
        panel.add(btnAjustar, BorderLayout.EAST);
        return panel;
    }

    private JPanel crearPanelFiltros() {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));

        JPanel izq = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        izq.setBackground(Color.WHITE);
        JLabel lbl = new JLabel("Filtrar:");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        izq.add(lbl);
        comboFiltro = new JComboBox<>(new String[]{"Todos", "Con Stock", "Bajo Stock", "Sin Stock"});
        comboFiltro.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboFiltro.addActionListener(e -> aplicarFiltro());
        izq.add(comboFiltro);
        panel.add(izq, BorderLayout.WEST);

        txtBusqueda = new JTextField();
        txtBusqueda.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtBusqueda.addActionListener(e -> buscarInventario());
        panel.add(txtBusqueda, BorderLayout.CENTER);

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnBuscar.setBackground(COLOR_PRINCIPAL);
        btnBuscar.setForeground(Color.WHITE);
        btnBuscar.setFocusPainted(false);
        btnBuscar.setBorderPainted(false);
        btnBuscar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBuscar.addActionListener(e -> buscarInventario());
        panel.add(btnBuscar, BorderLayout.EAST);
        return panel;
    }

    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(COLOR_PRINCIPAL, 2),
                "Inventario de Productos", 0, 0, new Font("Segoe UI", Font.BOLD, 14), COLOR_PRINCIPAL),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        String[] columnas = {"ID", "Producto", "Categoría", "Stock Actual", "Stock Mín.", "Estado", "Unidad", "Valor Costo", "Valor Venta"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaInventario = new JTable(modeloTabla);
        tablaInventario.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaInventario.setRowHeight(28);
        tablaInventario.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaInventario.getTableHeader().setBackground(COLOR_PRINCIPAL);
        tablaInventario.getTableHeader().setForeground(Color.BLACK);
        tablaInventario.setSelectionBackground(new Color(163, 228, 215));

        // Renderer para Estado con colores
        tablaInventario.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(CENTER);
                if (!isSelected) {
                    String estado = (String) value;
                    if ("SIN STOCK".equals(estado)) {
                        c.setBackground(new Color(255, 200, 200));
                        c.setForeground(new Color(192, 57, 43));
                    } else if ("BAJO".equals(estado)) {
                        c.setBackground(new Color(255, 248, 220));
                        c.setForeground(new Color(243, 156, 18));
                    } else {
                        c.setBackground(new Color(212, 245, 212));
                        c.setForeground(new Color(39, 174, 96));
                    }
                }
                return c;
            }
        });

        // Renderer para Stock Actual
        tablaInventario.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(CENTER);
                setFont(new Font("Segoe UI", Font.BOLD, 13));
                return c;
            }
        });

        tablaInventario.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) ajustarStockSeleccionado();
            }
        });

        JScrollPane scroll = new JScrollPane(tablaInventario);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199)));
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelEstadisticas() {
        JPanel panel = new JPanel(new GridLayout(1, 5, 10, 0));
        panel.setBackground(new Color(236, 240, 241));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        lblTotalUnidades = crearLabelStat("Unidades Totales: 0", new Color(52, 73, 94));
        panel.add(lblTotalUnidades);
        lblValorCosto = crearLabelStat("Valor Costo: $0.00", new Color(41, 128, 185));
        panel.add(lblValorCosto);
        lblValorVenta = crearLabelStat("Valor Venta: $0.00", new Color(39, 174, 96));
        panel.add(lblValorVenta);
        lblBajoStock = crearLabelStat("Bajo Stock: 0", new Color(243, 156, 18));
        panel.add(lblBajoStock);
        lblSinStock = crearLabelStat("Sin Stock: 0", new Color(231, 76, 60));
        panel.add(lblSinStock);
        return panel;
    }

    private JLabel crearLabelStat(String texto, Color color) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lbl.setForeground(color);
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        return lbl;
    }

    private void ajustarStockSeleccionado() {
        int fila = tablaInventario.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto", "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String id = (String) modeloTabla.getValueAt(fila, 0);
        String nombre = (String) modeloTabla.getValueAt(fila, 1);
        Producto p = productoService.buscarPorId(id);
        if (p == null) return;

        String input = JOptionPane.showInputDialog(this,
            "Producto: " + nombre + "\nStock actual: " + p.getStock() + "\n\nIngrese nuevo stock:",
            "Ajustar Stock", JOptionPane.PLAIN_MESSAGE);
        if (input != null && !input.trim().isEmpty()) {
            try {
                int nuevoStock = Integer.parseInt(input.trim());
                if (nuevoStock < 0) {
                    JOptionPane.showMessageDialog(this, "El stock no puede ser negativo", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                productoService.ajustarStock(id, nuevoStock);
                refrescar();
                JOptionPane.showMessageDialog(this, "Stock actualizado: " + p.getStock() + " → " + nuevoStock,
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Ingrese un número válido", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void aplicarFiltro() {
        String filtro = (String) comboFiltro.getSelectedItem();
        Producto[] lista;
        if ("Bajo Stock".equals(filtro)) {
            lista = productoService.obtenerProductosBajoStock();
        } else if ("Sin Stock".equals(filtro)) {
            lista = productoService.obtenerProductosSinStock();
        } else if ("Con Stock".equals(filtro)) {
            Producto[] todos = productoService.obtenerProductosActivos();
            int count = 0;
            for (Producto p : todos) if (p.getStock() > p.getStockMinimo()) count++;
            lista = new Producto[count];
            int idx = 0;
            for (Producto p : todos) if (p.getStock() > p.getStockMinimo()) lista[idx++] = p;
        } else {
            lista = productoService.obtenerProductos();
        }
        actualizarTabla(lista);
    }

    private void buscarInventario() {
        String termino = txtBusqueda.getText().trim();
        if (termino.isEmpty()) {
            aplicarFiltro();
        } else {
            actualizarTabla(productoService.buscarProductos(termino));
        }
    }

    public void actualizarTabla(Producto[] lista) {
        modeloTabla.setRowCount(0);
        for (Producto p : lista) {
            String estado = p.isSinStock() ? "SIN STOCK" : p.isBajoStock() ? "BAJO" : "OK";
            Object[] fila = {
                p.getIdProducto(), p.getNombre(), p.getCategoria(),
                p.getStock(), p.getStockMinimo(), estado, p.getUnidadMedida(),
                String.format("$%.2f", p.getStock() * p.getPrecioCompra()),
                String.format("$%.2f", p.getStock() * p.getPrecioVenta())
            };
            modeloTabla.addRow(fila);
        }
        actualizarEstadisticas();
    }

    public void refrescar() { aplicarFiltro(); }

    private void actualizarEstadisticas() {
        lblTotalUnidades.setText("Unidades: " + productoService.getTotalStockGeneral());
        lblValorCosto.setText("V. Costo: " + String.format("$%.2f", productoService.getValorInventario()));
        lblValorVenta.setText("V. Venta: " + String.format("$%.2f", productoService.getValorInventarioVenta()));
        lblBajoStock.setText("Bajo Stock: " + productoService.obtenerProductosBajoStock().length);
        lblSinStock.setText("Sin Stock: " + productoService.obtenerProductosSinStock().length);
    }
}
