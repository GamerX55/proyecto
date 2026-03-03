package ui;

import modelo.*;
import servicio.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;

public class PanelProductos extends JPanel {
    private ProductoService productoService;
    private ProveedorService proveedorService;
    private JTable tablaProductos;
    private DefaultTableModel modeloTabla;
    private JTextField txtBusqueda;
    private JLabel lblTotal, lblActivos, lblBajoStock;

    private static final Color COLOR_PRINCIPAL = new Color(230, 126, 34);
    private static final Color COLOR_CLARO = new Color(245, 176, 65);
    private static final Color COLOR_SELECCION = new Color(250, 215, 160);

    public PanelProductos(ProductoService productoService, ProveedorService proveedorService) {
        this.productoService = productoService;
        this.proveedorService = proveedorService;
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
        JLabel lblTitulo = new JLabel("Catálogo de Productos");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        panel.add(lblTitulo, BorderLayout.WEST);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBotones.setOpaque(false);
        JButton btnAgregar = crearBotonHeader("+ Añadir Producto");
        btnAgregar.addActionListener(e -> mostrarDialogoProducto(null));
        panelBotones.add(btnAgregar);
        JButton btnEditar = crearBotonHeader("Editar Seleccionado");
        btnEditar.addActionListener(e -> editarProductoSeleccionado());
        panelBotones.add(btnEditar);
        panel.add(panelBotones, BorderLayout.EAST);
        return panel;
    }

    private JButton crearBotonHeader(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(COLOR_CLARO);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // Filtro para números enteros
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

    // Filtro para números decimales
    private class DecimalDocumentFilter extends DocumentFilter {
        private final int maxLength;

        public DecimalDocumentFilter(int maxLength) {
            this.maxLength = maxLength;
        }

        @Override
        public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr)
                throws BadLocationException {
            if (string == null) return;
            String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
            String newText = currentText.substring(0, offset) + string + currentText.substring(offset);
            if (isValidDecimal(newText) && newText.length() <= maxLength) {
                super.insertString(fb, offset, string, attr);
            }
        }

        @Override
        public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
                throws BadLocationException {
            if (text == null) return;
            String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
            String newText = currentText.substring(0, offset) + text + currentText.substring(offset + length);
            if (isValidDecimal(newText) && newText.length() <= maxLength) {
                super.replace(fb, offset, length, text, attrs);
            }
        }

        private boolean isValidDecimal(String text) {
            // Permite números con un solo punto decimal
            return text.matches("\\d*\\.?\\d*");
        }
    }

    private void aplicarFiltroNumerico(JTextField textField, int maxLength) {
        ((AbstractDocument) textField.getDocument()).setDocumentFilter(new NumericDocumentFilter(maxLength));
    }

    private void aplicarFiltroDecimal(JTextField textField, int maxLength) {
        ((AbstractDocument) textField.getDocument()).setDocumentFilter(new DecimalDocumentFilter(maxLength));
    }

    private JPanel crearPanelBusqueda() {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
        JLabel lbl = new JLabel("Buscar producto:");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panel.add(lbl, BorderLayout.WEST);
        txtBusqueda = new JTextField();
        txtBusqueda.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtBusqueda.addActionListener(e -> buscarProductos());
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
        btnBuscar.addActionListener(e -> buscarProductos());
        panelBtns.add(btnBuscar);
        JButton btnTodos = new JButton("Mostrar Todos");
        btnTodos.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnTodos.setBackground(new Color(127, 140, 141));
        btnTodos.setForeground(Color.WHITE);
        btnTodos.setFocusPainted(false);
        btnTodos.setBorderPainted(false);
        btnTodos.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTodos.addActionListener(e -> { txtBusqueda.setText(""); actualizarTabla(productoService.obtenerProductos()); });
        panelBtns.add(btnTodos);
        panel.add(panelBtns, BorderLayout.EAST);
        return panel;
    }

    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(COLOR_PRINCIPAL, 2),
                "Lista de Productos", 0, 0, new Font("Segoe UI", Font.BOLD, 14), COLOR_PRINCIPAL),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        String[] columnas = {"ID", "Nombre", "Categoría", "P. Compra", "P. Venta", "Stock", "Mín.", "Unidad", "Proveedor", "Activo"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tablaProductos = new JTable(modeloTabla);
        tablaProductos.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaProductos.setRowHeight(28);
        tablaProductos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaProductos.getTableHeader().setBackground(COLOR_PRINCIPAL);
        tablaProductos.getTableHeader().setForeground(Color.BLACK);
        tablaProductos.setSelectionBackground(COLOR_SELECCION);

        // Renderer para Stock con colores
        tablaProductos.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(CENTER);
                if (!isSelected) {
                    int stock = Integer.parseInt(value.toString());
                    String minStr = table.getValueAt(row, 6).toString();
                    int min = Integer.parseInt(minStr);
                    if (stock <= 0) {
                        c.setBackground(new Color(255, 200, 200));
                        c.setForeground(new Color(192, 57, 43));
                    } else if (stock <= min) {
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

        tablaProductos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) editarProductoSeleccionado();
            }
        });

        JScrollPane scroll = new JScrollPane(tablaProductos);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199)));
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelEstadisticas() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        panel.setBackground(new Color(236, 240, 241));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        lblTotal = new JLabel("Total Productos: 0");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTotal.setForeground(new Color(52, 73, 94));
        panel.add(lblTotal);
        lblActivos = new JLabel("Activos: 0");
        lblActivos.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblActivos.setForeground(COLOR_PRINCIPAL);
        panel.add(lblActivos);
        lblBajoStock = new JLabel("Bajo Stock: 0");
        lblBajoStock.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblBajoStock.setForeground(new Color(231, 76, 60));
        panel.add(lblBajoStock);
        return panel;
    }

    public void mostrarDialogoProducto(Producto productoEditar) {
        boolean esEdicion = (productoEditar != null);
        JDialog dialogo = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                esEdicion ? "Editar Producto" : "Añadir Nuevo Producto", true);
        dialogo.setSize(520, 580);
        dialogo.setLocationRelativeTo(this);
        dialogo.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JTextField txtNombre = agregarCampoDialogo(form, gbc, "Nombre:", 0);
        JTextField txtDescripcion = agregarCampoDialogo(form, gbc, "Descripción:", 1);
        JTextField txtCategoria = agregarCampoDialogo(form, gbc, "Categoría:", 2);
        JTextField txtPrecioCompra = agregarCampoDialogo(form, gbc, "Precio Compra:", 3);
        aplicarFiltroDecimal(txtPrecioCompra, 10);
        JTextField txtPrecioVenta = agregarCampoDialogo(form, gbc, "Precio Venta:", 4);
        aplicarFiltroDecimal(txtPrecioVenta, 10);
        JTextField txtStockMinimo = agregarCampoDialogo(form, gbc, "Stock Mínimo:", 5);
        aplicarFiltroNumerico(txtStockMinimo, 6);

        // Combo unidad medida
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 1; gbc.weightx = 0.3;
        JLabel lblUnidad = new JLabel("Unidad Medida:");
        lblUnidad.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        form.add(lblUnidad, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        JComboBox<String> comboUnidad = new JComboBox<>(new String[]{"Pieza", "Kg", "Litro", "Caja", "Paquete", "Metro"});
        comboUnidad.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        form.add(comboUnidad, gbc);

        // Combo proveedor
        gbc.gridx = 0; gbc.gridy = 7; gbc.weightx = 0.3;
        JLabel lblProv = new JLabel("Proveedor:");
        lblProv.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        form.add(lblProv, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        JComboBox<Object> comboProv = new JComboBox<>();
        comboProv.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboProv.addItem("Sin proveedor");
        Proveedor[] provs = proveedorService.obtenerProveedoresActivos();
        for (Proveedor p : provs) comboProv.addItem(p);
        form.add(comboProv, gbc);

        JCheckBox chkActivo = new JCheckBox("Producto Activo", true);
        chkActivo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        chkActivo.setBackground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 2;
        form.add(chkActivo, gbc);

        if (esEdicion) {
            txtNombre.setText(productoEditar.getNombre());
            txtDescripcion.setText(productoEditar.getDescripcion());
            txtCategoria.setText(productoEditar.getCategoria());
            txtPrecioCompra.setText(String.format("%.2f", productoEditar.getPrecioCompra()));
            txtPrecioVenta.setText(String.format("%.2f", productoEditar.getPrecioVenta()));
            txtStockMinimo.setText(String.valueOf(productoEditar.getStockMinimo()));
            comboUnidad.setSelectedItem(productoEditar.getUnidadMedida());
            chkActivo.setSelected(productoEditar.isActivo());
            // Seleccionar proveedor
            for (int i = 0; i < comboProv.getItemCount(); i++) {
                Object item = comboProv.getItemAt(i);
                if (item instanceof Proveedor && ((Proveedor) item).getIdProveedor().equals(productoEditar.getIdProveedor())) {
                    comboProv.setSelectedIndex(i);
                    break;
                }
            }
        }

        dialogo.add(form, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelBotones.setBackground(new Color(236, 240, 241));
        JButton btnGuardar = new JButton(esEdicion ? "Guardar Cambios" : "Registrar Producto");
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnGuardar.setBackground(COLOR_PRINCIPAL);
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setBorderPainted(false);
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGuardar.addActionListener(e -> {
            if (txtNombre.getText().trim().isEmpty() || txtCategoria.getText().trim().isEmpty() ||
                txtPrecioCompra.getText().trim().isEmpty() || txtPrecioVenta.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialogo, "Nombre, Categoría y Precios son obligatorios",
                    "Campos incompletos", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                double pc = Double.parseDouble(txtPrecioCompra.getText().trim());
                double pv = Double.parseDouble(txtPrecioVenta.getText().trim());
                int sm = txtStockMinimo.getText().trim().isEmpty() ? 5 : Integer.parseInt(txtStockMinimo.getText().trim());
                String idProv = "";
                String nomProv = "Sin proveedor";
                Object selProv = comboProv.getSelectedItem();
                if (selProv instanceof Proveedor) {
                    idProv = ((Proveedor) selProv).getIdProveedor();
                    nomProv = ((Proveedor) selProv).getNombreEmpresa();
                }

                if (esEdicion) {
                    Producto actualizado = new Producto(productoEditar.getIdProducto(),
                        txtNombre.getText().trim(), txtDescripcion.getText().trim(),
                        txtCategoria.getText().trim(), pc, pv, idProv, nomProv,
                        productoEditar.getStock(), sm,
                        (String) comboUnidad.getSelectedItem(), chkActivo.isSelected());
                    productoService.actualizarProducto(productoEditar.getIdProducto(), actualizado);
                    JOptionPane.showMessageDialog(dialogo, "Producto actualizado exitosamente");
                } else {
                    String id = productoService.generarIdProducto();
                    Producto nuevo = new Producto(id, txtNombre.getText().trim(),
                        txtDescripcion.getText().trim(), txtCategoria.getText().trim(),
                        pc, pv, idProv, nomProv, 0, sm,
                        (String) comboUnidad.getSelectedItem(), chkActivo.isSelected());
                    productoService.agregarProducto(nuevo);
                    JOptionPane.showMessageDialog(dialogo, "Producto registrado con ID: " + id);
                }
                dialogo.dispose();
                actualizarTabla(productoService.obtenerProductos());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialogo, "Verifique que los valores numéricos sean correctos",
                    "Error de formato", JOptionPane.ERROR_MESSAGE);
            }
        });
        panelBotones.add(btnGuardar);

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

    private JTextField agregarCampoDialogo(JPanel panel, GridBagConstraints gbc, String etiqueta, int fila) {
        gbc.gridx = 0; gbc.gridy = fila; gbc.gridwidth = 1; gbc.weightx = 0.3;
        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(lbl, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        JTextField txt = new JTextField(20);
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panel.add(txt, gbc);
        return txt;
    }

    private void editarProductoSeleccionado() {
        int fila = tablaProductos.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un producto de la tabla", "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String id = (String) modeloTabla.getValueAt(fila, 0);
        Producto p = productoService.buscarPorId(id);
        if (p != null) mostrarDialogoProducto(p);
    }

    private void buscarProductos() {
        String termino = txtBusqueda.getText().trim();
        if (termino.isEmpty()) {
            actualizarTabla(productoService.obtenerProductos());
        } else {
            Producto[] resultados = productoService.buscarProductos(termino);
            actualizarTabla(resultados);
            if (resultados.length == 0)
                JOptionPane.showMessageDialog(this, "No se encontraron productos con: " + termino,
                    "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void actualizarTabla(Producto[] lista) {
        modeloTabla.setRowCount(0);
        for (Producto p : lista) {
            Object[] fila = {
                p.getIdProducto(), p.getNombre(), p.getCategoria(),
                String.format("$%.2f", p.getPrecioCompra()), String.format("$%.2f", p.getPrecioVenta()),
                p.getStock(), p.getStockMinimo(), p.getUnidadMedida(),
                p.getNombreProveedor(), p.isActivo() ? "Sí" : "No"
            };
            modeloTabla.addRow(fila);
        }
        actualizarEstadisticas();
    }

    public void refrescar() { actualizarTabla(productoService.obtenerProductos()); }

    private void actualizarEstadisticas() {
        lblTotal.setText("Total Productos: " + productoService.getTotalProductos());
        lblActivos.setText("Activos: " + productoService.getTotalProductosActivos());
        lblBajoStock.setText("Bajo Stock: " + productoService.obtenerProductosBajoStock().length);
    }
}
