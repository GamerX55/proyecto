package ui;

import modelo.*;
import servicio.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class PanelCaja extends JPanel {
    private ProductoService productoService;
    private VentaService ventaService;
    private JTextField txtBusquedaProducto, txtCantidad;
    private JTable tablaCarrito;
    private DefaultTableModel modeloCarrito;
    private JLabel lblTotal, lblArticulos;

    // Carrito interno
    private DetalleVenta[] carrito;
    private int totalCarrito;

    private static final Color COLOR_PRINCIPAL = new Color(211, 84, 0);
    private static final Color COLOR_CLARO = new Color(230, 126, 34);

    public PanelCaja(ProductoService productoService, VentaService ventaService) {
        this.productoService = productoService;
        this.ventaService = ventaService;
        this.carrito = new DetalleVenta[50];
        this.totalCarrito = 0;
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        crearComponentes();
    }

    private void crearComponentes() {
        add(crearPanelTitulo(), BorderLayout.NORTH);

        JPanel panelContenido = new JPanel(new BorderLayout(10, 0));
        panelContenido.setBackground(Color.WHITE);

        // Panel izquierdo: búsqueda de productos
        panelContenido.add(crearPanelBusqueda(), BorderLayout.NORTH);
        // Panel central: carrito
        panelContenido.add(crearPanelCarrito(), BorderLayout.CENTER);

        add(panelContenido, BorderLayout.CENTER);
        add(crearPanelCobro(), BorderLayout.SOUTH);
    }

    private JPanel crearPanelTitulo() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_PRINCIPAL);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        JLabel lblTitulo = new JLabel("Punto de Venta - Caja");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        panel.add(lblTitulo, BorderLayout.WEST);

        JButton btnNuevaVenta = new JButton("Nueva Venta");
        btnNuevaVenta.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnNuevaVenta.setBackground(COLOR_CLARO);
        btnNuevaVenta.setForeground(Color.WHITE);
        btnNuevaVenta.setFocusPainted(false);
        btnNuevaVenta.setBorderPainted(false);
        btnNuevaVenta.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnNuevaVenta.addActionListener(e -> limpiarCarrito());
        panel.add(btnNuevaVenta, BorderLayout.EAST);
        return panel;
    }

    private JPanel crearPanelBusqueda() {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(COLOR_PRINCIPAL, 2),
                "Agregar Productos", 0, 0, new Font("Segoe UI", Font.BOLD, 14), COLOR_PRINCIPAL),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        JPanel campos = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        campos.setBackground(Color.WHITE);

        campos.add(crearLabel("Buscar producto:"));
        txtBusquedaProducto = new JTextField(20);
        txtBusquedaProducto.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtBusquedaProducto.setToolTipText("ID o nombre del producto");
        campos.add(txtBusquedaProducto);

        campos.add(crearLabel("Cant.:"));
        txtCantidad = new JTextField("1", 5);
        txtCantidad.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        campos.add(txtCantidad);

        JButton btnAgregar = new JButton("Agregar al Carrito");
        btnAgregar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnAgregar.setBackground(COLOR_PRINCIPAL);
        btnAgregar.setForeground(Color.WHITE);
        btnAgregar.setFocusPainted(false);
        btnAgregar.setBorderPainted(false);
        btnAgregar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAgregar.addActionListener(e -> agregarAlCarrito());
        campos.add(btnAgregar);

        JButton btnMas = new JButton("+");
        btnMas.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnMas.setBackground(new Color(39, 174, 96));
        btnMas.setForeground(Color.WHITE);
        btnMas.setFocusPainted(false);
        btnMas.setBorderPainted(false);
        btnMas.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnMas.setPreferredSize(new Dimension(45, 30));
        btnMas.setToolTipText("Aumentar cantidad del producto seleccionado");
        btnMas.addActionListener(e -> modificarCantidadCarrito(1));
        campos.add(btnMas);

        JButton btnMenos = new JButton("−");
        btnMenos.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnMenos.setBackground(new Color(243, 156, 18));
        btnMenos.setForeground(Color.WHITE);
        btnMenos.setFocusPainted(false);
        btnMenos.setBorderPainted(false);
        btnMenos.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnMenos.setPreferredSize(new Dimension(45, 30));
        btnMenos.setToolTipText("Disminuir cantidad del producto seleccionado");
        btnMenos.addActionListener(e -> modificarCantidadCarrito(-1));
        campos.add(btnMenos);

        JButton btnQuitar = new JButton("Quitar");
        btnQuitar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnQuitar.setBackground(new Color(231, 76, 60));
        btnQuitar.setForeground(Color.WHITE);
        btnQuitar.setFocusPainted(false);
        btnQuitar.setBorderPainted(false);
        btnQuitar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnQuitar.setToolTipText("Quitar producto seleccionado del carrito");
        btnQuitar.addActionListener(e -> quitarDelCarrito());
        campos.add(btnQuitar);

        txtBusquedaProducto.addActionListener(e -> agregarAlCarrito());

        panel.add(campos, BorderLayout.CENTER);
        return panel;
    }

    private JLabel crearLabel(String texto) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        return lbl;
    }

    // Filtro de documento para permitir solo números
    private class NumericDocumentFilter extends DocumentFilter {
        private final int maxLength;

        public NumericDocumentFilter(int maxLength) {
            this.maxLength = maxLength;
        }

        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                throws BadLocationException {
            if (string == null) return;
            if (isNumeric(string) && (fb.getDocument().getLength() + string.length() <= maxLength)) {
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                throws BadLocationException {
            if (text == null) return;
            if (isNumeric(text) && (fb.getDocument().getLength() - length + text.length() <= maxLength)) {
                super.replace(fb, offset, length, text, attrs);
            }
        }

        private boolean isNumeric(String text) {
            return text.matches("\\d*");
        }
    }

    // Método para aplicar filtro numérico a un JTextField
    private void aplicarFiltroNumerico(JTextField textField, int maxLength) {
        ((AbstractDocument) textField.getDocument()).setDocumentFilter(new NumericDocumentFilter(maxLength));
    }

    private JPanel crearPanelCarrito() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(COLOR_PRINCIPAL, 2),
                "Carrito de Venta", 0, 0, new Font("Segoe UI", Font.BOLD, 14), COLOR_PRINCIPAL),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        String[] columnas = {"ID Producto", "Producto", "Cantidad", "P. Unitario", "Subtotal"};
        modeloCarrito = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaCarrito = new JTable(modeloCarrito);
        tablaCarrito.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tablaCarrito.setRowHeight(30);
        tablaCarrito.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        tablaCarrito.getTableHeader().setBackground(COLOR_PRINCIPAL);
        tablaCarrito.getTableHeader().setForeground(Color.BLACK);
        tablaCarrito.setSelectionBackground(new Color(245, 183, 140));

        JScrollPane scroll = new JScrollPane(tablaCarrito);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199)));
        panel.add(scroll, BorderLayout.CENTER);

        // Resumen
        JPanel panelResumen = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        panelResumen.setBackground(new Color(253, 246, 227));
        panelResumen.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        lblArticulos = new JLabel("Artículos: 0");
        lblArticulos.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblArticulos.setForeground(new Color(52, 73, 94));
        panelResumen.add(lblArticulos);
        lblTotal = new JLabel("TOTAL: $0.00");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTotal.setForeground(COLOR_PRINCIPAL);
        panelResumen.add(lblTotal);
        panel.add(panelResumen, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel crearPanelCobro() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setBackground(new Color(236, 240, 241));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnCobrar = new JButton("  COBRAR VENTA  ");
        btnCobrar.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnCobrar.setBackground(new Color(39, 174, 96));
        btnCobrar.setForeground(Color.WHITE);
        btnCobrar.setFocusPainted(false);
        btnCobrar.setBorderPainted(false);
        btnCobrar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCobrar.setPreferredSize(new Dimension(250, 45));
        btnCobrar.addActionListener(e -> procesarVenta());
        panel.add(btnCobrar);

        JButton btnCancelar = new JButton("Cancelar Venta");
        btnCancelar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCancelar.setBackground(new Color(231, 76, 60));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFocusPainted(false);
        btnCancelar.setBorderPainted(false);
        btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancelar.addActionListener(e -> limpiarCarrito());
        panel.add(btnCancelar);

        return panel;
    }

    private void agregarAlCarrito() {
        String busqueda = txtBusquedaProducto.getText().trim();
        if (busqueda.isEmpty()) return;

        // Buscar producto por ID o nombre
        Producto producto = productoService.buscarPorId(busqueda);
        if (producto == null) {
            Producto[] resultados = productoService.buscarProductos(busqueda);
            if (resultados.length == 1) {
                producto = resultados[0];
            } else if (resultados.length > 1) {
                // Mostrar selector
                producto = (Producto) JOptionPane.showInputDialog(this,
                    "Se encontraron varios productos. Seleccione uno:",
                    "Seleccionar Producto", JOptionPane.PLAIN_MESSAGE, null,
                    resultados, resultados[0]);
            } else {
                JOptionPane.showMessageDialog(this, "Producto no encontrado: " + busqueda,
                    "No encontrado", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }

        if (producto == null) return;
        if (!producto.isActivo()) {
            JOptionPane.showMessageDialog(this, "El producto no está activo", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int cantidad;
        try {
            cantidad = Integer.parseInt(txtCantidad.getText().trim());
            if (cantidad <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Ingrese una cantidad válida", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (producto.getStock() < cantidad) {
            JOptionPane.showMessageDialog(this,
                "Stock insuficiente. Disponible: " + producto.getStock(),
                "Sin Stock", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Verificar si ya está en carrito
        boolean encontrado = false;
        for (int i = 0; i < totalCarrito; i++) {
            if (carrito[i].getIdProducto().equals(producto.getIdProducto())) {
                int nuevaCant = carrito[i].getCantidad() + cantidad;
                if (producto.getStock() < nuevaCant) {
                    JOptionPane.showMessageDialog(this,
                        "Stock insuficiente. Disponible: " + producto.getStock() + ", en carrito: " + carrito[i].getCantidad(),
                        "Sin Stock", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                carrito[i].setCantidad(nuevaCant);
                encontrado = true;
                break;
            }
        }

        if (!encontrado) {
            if (totalCarrito >= carrito.length) {
                DetalleVenta[] nuevo = new DetalleVenta[carrito.length * 2];
                for (int i = 0; i < totalCarrito; i++) nuevo[i] = carrito[i];
                carrito = nuevo;
            }
            carrito[totalCarrito++] = new DetalleVenta(producto.getIdProducto(),
                producto.getNombre(), cantidad, producto.getPrecioVenta());
        }

        actualizarTablaCarrito();
        txtBusquedaProducto.setText("");
        txtCantidad.setText("1");
        txtBusquedaProducto.requestFocus();
    }

    private void modificarCantidadCarrito(int delta) {
        int fila = tablaCarrito.getSelectedRow();
        if (fila == -1 || fila >= totalCarrito) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto del carrito",
                "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }

        DetalleVenta detalle = carrito[fila];
        int nuevaCantidad = detalle.getCantidad() + delta;

        if (nuevaCantidad <= 0) {
            // Si llega a 0 o menos, quitar del carrito
            for (int i = fila; i < totalCarrito - 1; i++) {
                carrito[i] = carrito[i + 1];
            }
            carrito[--totalCarrito] = null;
        } else {
            // Verificar stock al aumentar
            if (delta > 0) {
                Producto producto = productoService.buscarPorId(detalle.getIdProducto());
                if (producto != null && producto.getStock() < nuevaCantidad) {
                    JOptionPane.showMessageDialog(this,
                        "Stock insuficiente. Disponible: " + producto.getStock() +
                        ", en carrito: " + detalle.getCantidad(),
                        "Sin Stock", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
            detalle.setCantidad(nuevaCantidad);
        }

        actualizarTablaCarrito();
        // Mantener selección
        if (nuevaCantidad > 0 && fila < totalCarrito) {
            tablaCarrito.setRowSelectionInterval(fila, fila);
        }
    }

    private void quitarDelCarrito() {
        int fila = tablaCarrito.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto del carrito", "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // Remover del arreglo
        for (int i = fila; i < totalCarrito - 1; i++) {
            carrito[i] = carrito[i + 1];
        }
        carrito[--totalCarrito] = null;
        actualizarTablaCarrito();
    }

    private void actualizarTablaCarrito() {
        modeloCarrito.setRowCount(0);
        double total = 0;
        int articulos = 0;
        for (int i = 0; i < totalCarrito; i++) {
            DetalleVenta d = carrito[i];
            Object[] fila = {
                d.getIdProducto(), d.getNombreProducto(), d.getCantidad(),
                String.format("$%.2f", d.getPrecioUnitario()),
                String.format("$%.2f", d.getSubtotal())
            };
            modeloCarrito.addRow(fila);
            total += d.getSubtotal();
            articulos += d.getCantidad();
        }
        lblTotal.setText("TOTAL: " + String.format("$%.2f", total));
        lblArticulos.setText("Artículos: " + articulos);
    }

    private void procesarVenta() {
        if (totalCarrito == 0) {
            JOptionPane.showMessageDialog(this, "El carrito está vacío", "Sin productos", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double totalVenta = 0;
        int articulosTotal = 0;
        for (int i = 0; i < totalCarrito; i++) {
            totalVenta += carrito[i].getSubtotal();
            articulosTotal += carrito[i].getCantidad();
        }
        final double total = totalVenta;
        final int articulos = articulosTotal;

        // === Diálogo de cobro ===
        JDialog dialogo = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Cobrar Venta", true);
        dialogo.setSize(500, 560);
        dialogo.setLocationRelativeTo(this);
        dialogo.setLayout(new BorderLayout());

        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));
        panelPrincipal.setBackground(Color.WHITE);
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));

        // --- Resumen de venta ---
        JPanel resumen = new JPanel(new GridBagLayout());
        resumen.setBackground(new Color(253, 246, 227));
        resumen.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(COLOR_PRINCIPAL, 2),
                "Resumen de Venta", 0, 0, new Font("Segoe UI", Font.BOLD, 14), COLOR_PRINCIPAL),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        GridBagConstraints gr = new GridBagConstraints();
        gr.insets = new Insets(4, 5, 4, 5);
        gr.fill = GridBagConstraints.HORIZONTAL;

        gr.gridx = 0; gr.gridy = 0; gr.weightx = 0.4;
        JLabel lblArt = new JLabel("Artículos:");
        lblArt.setFont(new Font("Segoe UI", Font.BOLD, 14));
        resumen.add(lblArt, gr);
        gr.gridx = 1; gr.weightx = 0.6;
        JLabel lblArtVal = new JLabel(String.valueOf(articulos));
        lblArtVal.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        resumen.add(lblArtVal, gr);

        gr.gridx = 0; gr.gridy = 1; gr.weightx = 0.4;
        JLabel lblTot = new JLabel("TOTAL A COBRAR:");
        lblTot.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTot.setForeground(COLOR_PRINCIPAL);
        resumen.add(lblTot, gr);
        gr.gridx = 1; gr.weightx = 0.6;
        JLabel lblTotVal = new JLabel(String.format("$%.2f", total));
        lblTotVal.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTotVal.setForeground(COLOR_PRINCIPAL);
        resumen.add(lblTotVal, gr);

        panelPrincipal.add(resumen);
        panelPrincipal.add(Box.createRigidArea(new Dimension(0, 10)));

        // --- Selector de método ---
        JPanel metodoPanel = new JPanel(new GridBagLayout());
        metodoPanel.setBackground(Color.WHITE);
        GridBagConstraints gm = new GridBagConstraints();
        gm.insets = new Insets(4, 5, 4, 5);
        gm.fill = GridBagConstraints.HORIZONTAL;
        gm.gridx = 0; gm.gridy = 0; gm.weightx = 0.4;
        JLabel lblMetodo = new JLabel("Método de pago:");
        lblMetodo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        metodoPanel.add(lblMetodo, gm);
        gm.gridx = 1; gm.weightx = 0.6;
        JComboBox<String> comboMetodo = new JComboBox<>(new String[]{"Efectivo", "Tarjeta de Débito", "Tarjeta de Crédito", "Transferencia"});
        comboMetodo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        metodoPanel.add(comboMetodo, gm);
        panelPrincipal.add(metodoPanel);
        panelPrincipal.add(Box.createRigidArea(new Dimension(0, 5)));

        // --- Paneles dinámicos por método (CardLayout) ---
        CardLayout cardMetodo = new CardLayout();
        JPanel panelMetodoExtra = new JPanel(cardMetodo);
        panelMetodoExtra.setBackground(Color.WHITE);

        // == Efectivo ==
        JPanel panelEfectivo = new JPanel(new GridBagLayout());
        panelEfectivo.setBackground(Color.WHITE);
        panelEfectivo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(46, 204, 113), 1),
                "Pago en Efectivo", 0, 0, new Font("Segoe UI", Font.BOLD, 12), new Color(46, 204, 113)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        GridBagConstraints ge = new GridBagConstraints();
        ge.insets = new Insets(4, 5, 4, 5);
        ge.fill = GridBagConstraints.HORIZONTAL;

        ge.gridx = 0; ge.gridy = 0; ge.weightx = 0.4;
        JLabel lblRecibido = new JLabel("Monto recibido ($):");
        lblRecibido.setFont(new Font("Segoe UI", Font.BOLD, 14));
        panelEfectivo.add(lblRecibido, ge);
        ge.gridx = 1; ge.weightx = 0.6;
        JTextField txtRecibido = new JTextField();
        txtRecibido.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        panelEfectivo.add(txtRecibido, ge);

        ge.gridx = 0; ge.gridy = 1; ge.weightx = 0.4;
        JLabel lblCambioTit = new JLabel("Cambio:");
        lblCambioTit.setFont(new Font("Segoe UI", Font.BOLD, 16));
        panelEfectivo.add(lblCambioTit, ge);
        ge.gridx = 1; ge.weightx = 0.6;
        JLabel lblCambio = new JLabel("$0.00");
        lblCambio.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblCambio.setForeground(new Color(39, 174, 96));
        panelEfectivo.add(lblCambio, ge);

        // Cálculo automático de cambio
        txtRecibido.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void actualizar() {
                try {
                    double recibido = Double.parseDouble(txtRecibido.getText().trim());
                    double cambio = recibido - total;
                    if (cambio >= 0) {
                        lblCambio.setText(String.format("$%.2f", cambio));
                        lblCambio.setForeground(new Color(39, 174, 96));
                    } else {
                        lblCambio.setText("Insuficiente");
                        lblCambio.setForeground(new Color(231, 76, 60));
                    }
                } catch (NumberFormatException ex) {
                    lblCambio.setText("$0.00");
                    lblCambio.setForeground(new Color(39, 174, 96));
                }
            }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { actualizar(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { actualizar(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { actualizar(); }
        });
        panelMetodoExtra.add(panelEfectivo, "Efectivo");

        // == Transferencia ==
        JPanel panelTransferencia = new JPanel(new GridBagLayout());
        panelTransferencia.setBackground(Color.WHITE);
        panelTransferencia.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(52, 152, 219), 1),
                "Datos de Transferencia", 0, 0, new Font("Segoe UI", Font.BOLD, 12), new Color(52, 152, 219)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        GridBagConstraints gt = new GridBagConstraints();
        gt.insets = new Insets(4, 5, 4, 5);
        gt.fill = GridBagConstraints.HORIZONTAL;

        gt.gridx = 0; gt.gridy = 0; gt.weightx = 0.4;
        panelTransferencia.add(new JLabel("Banco origen:"), gt);
        gt.gridx = 1; gt.weightx = 0.6;
        JTextField txtBancoOrigen = new JTextField();
        txtBancoOrigen.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panelTransferencia.add(txtBancoOrigen, gt);

        gt.gridx = 0; gt.gridy = 1; gt.weightx = 0.4;
        panelTransferencia.add(new JLabel("Nº de referencia:"), gt);
        gt.gridx = 1; gt.weightx = 0.6;
        JTextField txtRefTransf = new JTextField();
        txtRefTransf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panelTransferencia.add(txtRefTransf, gt);

        gt.gridx = 0; gt.gridy = 2; gt.weightx = 0.4;
        panelTransferencia.add(new JLabel("Fecha transferencia:"), gt);
        gt.gridx = 1; gt.weightx = 0.6;
        JTextField txtFechaTransf = new JTextField(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        txtFechaTransf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panelTransferencia.add(txtFechaTransf, gt);
        panelMetodoExtra.add(panelTransferencia, "Transferencia");

        // == Tarjeta de Crédito ==
        JPanel panelTCredito = new JPanel(new GridBagLayout());
        panelTCredito.setBackground(Color.WHITE);
        panelTCredito.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(243, 156, 18), 1),
                "Datos de Tarjeta de Crédito", 0, 0, new Font("Segoe UI", Font.BOLD, 12), new Color(243, 156, 18)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(4, 5, 4, 5);
        gc.fill = GridBagConstraints.HORIZONTAL;

        gc.gridx = 0; gc.gridy = 0; gc.weightx = 0.4;
        panelTCredito.add(new JLabel("Últimos 4 dígitos:"), gc);
        gc.gridx = 1; gc.weightx = 0.6;
        JTextField txtUltimos4TC = new JTextField();
        txtUltimos4TC.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        aplicarFiltroNumerico(txtUltimos4TC, 4);
        panelTCredito.add(txtUltimos4TC, gc);

        gc.gridx = 0; gc.gridy = 1; gc.weightx = 0.4;
        panelTCredito.add(new JLabel("Titular de tarjeta:"), gc);
        gc.gridx = 1; gc.weightx = 0.6;
        JTextField txtTitularTC = new JTextField();
        txtTitularTC.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panelTCredito.add(txtTitularTC, gc);

        gc.gridx = 0; gc.gridy = 2; gc.weightx = 0.4;
        panelTCredito.add(new JLabel("Nº de autorización:"), gc);
        gc.gridx = 1; gc.weightx = 0.6;
        JTextField txtAutorizacionTC = new JTextField();
        txtAutorizacionTC.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        aplicarFiltroNumerico(txtAutorizacionTC, 20);
        panelTCredito.add(txtAutorizacionTC, gc);
        panelMetodoExtra.add(panelTCredito, "Tarjeta de Crédito");

        // == Tarjeta de Débito ==
        JPanel panelTDebito = new JPanel(new GridBagLayout());
        panelTDebito.setBackground(Color.WHITE);
        panelTDebito.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(46, 204, 113), 1),
                "Datos de Tarjeta de Débito", 0, 0, new Font("Segoe UI", Font.BOLD, 12), new Color(46, 204, 113)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        GridBagConstraints gd = new GridBagConstraints();
        gd.insets = new Insets(4, 5, 4, 5);
        gd.fill = GridBagConstraints.HORIZONTAL;

        gd.gridx = 0; gd.gridy = 0; gd.weightx = 0.4;
        panelTDebito.add(new JLabel("Últimos 4 dígitos:"), gd);
        gd.gridx = 1; gd.weightx = 0.6;
        JTextField txtUltimos4TD = new JTextField();
        txtUltimos4TD.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        aplicarFiltroNumerico(txtUltimos4TD, 4);
        panelTDebito.add(txtUltimos4TD, gd);

        gd.gridx = 0; gd.gridy = 1; gd.weightx = 0.4;
        panelTDebito.add(new JLabel("Titular de tarjeta:"), gd);
        gd.gridx = 1; gd.weightx = 0.6;
        JTextField txtTitularTD = new JTextField();
        txtTitularTD.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panelTDebito.add(txtTitularTD, gd);

        gd.gridx = 0; gd.gridy = 2; gd.weightx = 0.4;
        panelTDebito.add(new JLabel("Nº de autorización:"), gd);
        gd.gridx = 1; gd.weightx = 0.6;
        JTextField txtAutorizacionTD = new JTextField();
        txtAutorizacionTD.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        aplicarFiltroNumerico(txtAutorizacionTD, 20);
        panelTDebito.add(txtAutorizacionTD, gd);
        panelMetodoExtra.add(panelTDebito, "Tarjeta de Débito");

        cardMetodo.show(panelMetodoExtra, "Efectivo");
        comboMetodo.addActionListener(ev -> {
            String sel = (String) comboMetodo.getSelectedItem();
            cardMetodo.show(panelMetodoExtra, sel);
            dialogo.revalidate();
            dialogo.repaint();
        });
        panelPrincipal.add(panelMetodoExtra);

        JScrollPane scrollDialog = new JScrollPane(panelPrincipal);
        scrollDialog.setBorder(null);
        scrollDialog.getVerticalScrollBar().setUnitIncrement(10);
        dialogo.add(scrollDialog, BorderLayout.CENTER);

        // --- Botones del diálogo ---
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelBotones.setBackground(new Color(236, 240, 241));

        JButton btnConfirmar = new JButton("  CONFIRMAR COBRO  ");
        btnConfirmar.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnConfirmar.setBackground(new Color(39, 174, 96));
        btnConfirmar.setForeground(Color.WHITE);
        btnConfirmar.setFocusPainted(false);
        btnConfirmar.setBorderPainted(false);
        btnConfirmar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnConfirmar.addActionListener(e -> {
            String metodoPago = (String) comboMetodo.getSelectedItem();
            String referencia = "";
            String detalleTicket = "";
            double montoRecibido = 0;
            double cambioCalculado = 0;

            // Validar campos según método de pago
            if ("Efectivo".equals(metodoPago)) {
                try {
                    montoRecibido = Double.parseDouble(txtRecibido.getText().trim());
                    if (montoRecibido < total) {
                        JOptionPane.showMessageDialog(dialogo,
                            "El monto recibido ($" + String.format("%.2f", montoRecibido) +
                            ") es menor al total ($" + String.format("%.2f", total) + ")",
                            "Monto insuficiente", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    cambioCalculado = montoRecibido - total;
                    referencia = "Efectivo";
                    detalleTicket = "Recibido:  $" + String.format("%.2f", montoRecibido) +
                                    "\nCambio:    $" + String.format("%.2f", cambioCalculado);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialogo, "Ingrese el monto recibido del cliente",
                        "Campo requerido", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else if ("Transferencia".equals(metodoPago)) {
                if (txtBancoOrigen.getText().trim().isEmpty() || txtRefTransf.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(dialogo, "Complete Banco origen y Nº de referencia",
                        "Campos incompletos", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                referencia = "Ref: " + txtRefTransf.getText().trim();
                detalleTicket = "Banco:       " + txtBancoOrigen.getText().trim() +
                    "\nReferencia:  " + txtRefTransf.getText().trim() +
                    "\nFecha:       " + txtFechaTransf.getText().trim();
            } else if ("Tarjeta de Crédito".equals(metodoPago)) {
                if (txtUltimos4TC.getText().trim().isEmpty() ||
                    txtTitularTC.getText().trim().isEmpty() ||
                    txtAutorizacionTC.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(dialogo, "Complete todos los datos de la tarjeta de crédito",
                        "Campos incompletos", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                // Validación adicional de longitud
                if (txtUltimos4TC.getText().trim().length() != 4) {
                    JOptionPane.showMessageDialog(dialogo, "Los últimos 4 dígitos deben ser exactamente 4 números",
                        "Validación de Tarjeta", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                referencia = "Auth: " + txtAutorizacionTC.getText().trim();
                detalleTicket = "Tarjeta:     ****" + txtUltimos4TC.getText().trim() +
                    "\nTitular:     " + txtTitularTC.getText().trim() +
                    "\nAutorización: " + txtAutorizacionTC.getText().trim();
            } else if ("Tarjeta de Débito".equals(metodoPago)) {
                if (txtUltimos4TD.getText().trim().isEmpty() ||
                    txtTitularTD.getText().trim().isEmpty() ||
                    txtAutorizacionTD.getText().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(dialogo, "Complete todos los datos de la tarjeta de débito",
                        "Campos incompletos", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                // Validación adicional de longitud
                if (txtUltimos4TD.getText().trim().length() != 4) {
                    JOptionPane.showMessageDialog(dialogo, "Los últimos 4 dígitos deben ser exactamente 4 números",
                        "Validación de Tarjeta", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                referencia = "Auth: " + txtAutorizacionTD.getText().trim();
                detalleTicket = "Tarjeta:     ****" + txtUltimos4TD.getText().trim() +
                    "\nTitular:     " + txtTitularTD.getText().trim() +
                    "\nAutorización: " + txtAutorizacionTD.getText().trim();
            }

            // Crear detalles copiados
            DetalleVenta[] detallesVenta = new DetalleVenta[totalCarrito];
            for (int i = 0; i < totalCarrito; i++) {
                detallesVenta[i] = carrito[i];
            }

            // Reducir stock
            for (int i = 0; i < totalCarrito; i++) {
                productoService.reducirStock(carrito[i].getIdProducto(), carrito[i].getCantidad());
            }

            // Crear venta
            String idVenta = ventaService.generarIdVenta();
            String hora = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"));
            Venta venta = new Venta(idVenta, LocalDate.now(), hora, detallesVenta, totalCarrito,
                articulos, total, metodoPago, referencia, "Público General");
            ventaService.agregarVenta(venta);

            dialogo.dispose();

            // === Generar ticket detallado ===
            StringBuilder ticket = new StringBuilder();
            ticket.append("════════════════════════════════\n");
            ticket.append("       TICKET DE VENTA\n");
            ticket.append("      SISTEMA ABARROTERA\n");
            ticket.append("════════════════════════════════\n");
            ticket.append("Folio: ").append(idVenta).append("\n");
            ticket.append("Fecha: ").append(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))).append("\n");
            ticket.append("Hora:  ").append(hora).append("\n");
            ticket.append("────────────────────────────────\n");
            ticket.append("PRODUCTOS:\n");
            ticket.append("────────────────────────────────\n");
            for (int i = 0; i < totalCarrito; i++) {
                DetalleVenta d = carrito[i];
                ticket.append(d.getNombreProducto()).append("\n");
                ticket.append("  ").append(d.getCantidad()).append(" x $")
                      .append(String.format("%.2f", d.getPrecioUnitario()))
                      .append(" = $").append(String.format("%.2f", d.getSubtotal())).append("\n");
            }
            ticket.append("────────────────────────────────\n");
            ticket.append("Artículos: ").append(articulos).append("\n");
            ticket.append("TOTAL:     $").append(String.format("%.2f", total)).append("\n");
            ticket.append("────────────────────────────────\n");
            ticket.append("PAGO:\n");
            ticket.append("Método:    ").append(metodoPago).append("\n");
            if (!detalleTicket.isEmpty()) {
                ticket.append(detalleTicket).append("\n");
            }
            ticket.append("════════════════════════════════\n");
            ticket.append("    ¡Gracias por su compra!");

            JTextArea txtTicket = new JTextArea(ticket.toString());
            txtTicket.setFont(new Font("Courier New", Font.PLAIN, 12));
            txtTicket.setEditable(false);
            JScrollPane scrollTicket = new JScrollPane(txtTicket);
            scrollTicket.setPreferredSize(new Dimension(370, 450));
            JOptionPane.showMessageDialog(PanelCaja.this, scrollTicket,
                "Venta Registrada - " + idVenta, JOptionPane.INFORMATION_MESSAGE);

            limpiarCarrito();
        });
        panelBotones.add(btnConfirmar);

        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCancelar.setBackground(new Color(149, 165, 166));
        btnCancelar.setForeground(Color.WHITE);
        btnCancelar.setFocusPainted(false);
        btnCancelar.setBorderPainted(false);
        btnCancelar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancelar.addActionListener(e -> dialogo.dispose());
        panelBotones.add(btnCancelar);

        dialogo.add(panelBotones, BorderLayout.SOUTH);
        dialogo.setVisible(true);
    }

    private void limpiarCarrito() {
        carrito = new DetalleVenta[50];
        totalCarrito = 0;
        actualizarTablaCarrito();
        txtBusquedaProducto.setText("");
        txtCantidad.setText("1");
        txtBusquedaProducto.requestFocus();
    }

    public void refrescar() {
        // No requiere actualización especial
    }
}
