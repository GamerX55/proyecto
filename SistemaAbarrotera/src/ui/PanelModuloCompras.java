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
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PanelModuloCompras extends JPanel {
    private CompraService compraService;
    private ProveedorService proveedorService;
    private ProductoService productoService;
    private PanelProveedores panelProveedores;
    private JTextField txtIdProducto, txtNombreProducto, txtEmpresa;
    private JTextField txtCantidad, txtFecha, txtCosto, txtPrecio, txtOrdenPedido;
    private JTextField txtBusqueda;
    private JComboBox<Object> comboProveedor;
    private JTable tablaCompras;
    private DefaultTableModel modeloTabla;
    private JLabel lblTotalCompras, lblTotalCostos;
    private JButton btnRegistrar, btnModificar, btnEliminar, btnLimpiar;
    private boolean modoEdicion = false;
    private String idCompraEdicion = null;
    private boolean actualizandoCombo = false;
    private static final String COMBO_PLACEHOLDER = "Seleccione proveedor...";
    private static final String COMBO_AGREGAR_NUEVO = "+ Agregar nuevo proveedor";

    public PanelModuloCompras(CompraService compraService, ProveedorService proveedorService, ProductoService productoService, PanelProveedores panelProveedores) {
        this.compraService = compraService;
        this.proveedorService = proveedorService;
        this.productoService = productoService;
        this.panelProveedores = panelProveedores;
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        crearComponentes();
    }

    private void crearComponentes() {
        add(crearPanelTitulo(), BorderLayout.NORTH);

        JPanel panelCentral = new JPanel(new BorderLayout(10, 10));
        panelCentral.setBackground(Color.WHITE);
        panelCentral.add(crearPanelFormulario(), BorderLayout.NORTH);
        panelCentral.add(crearPanelTabla(), BorderLayout.CENTER);

        add(panelCentral, BorderLayout.CENTER);
        add(crearPanelEstadisticas(), BorderLayout.SOUTH);
    }

    private JPanel crearPanelTitulo() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(41, 128, 185));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblTitulo = new JLabel("Módulo de Compras");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        panel.add(lblTitulo, BorderLayout.WEST);

        return panel;
    }

    private JPanel crearPanelFormulario() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(41, 128, 185), 2),
                "Registrar Nueva Compra",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 14),
                new Color(41, 128, 185)
            ),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        agregarCampo(panel, gbc, "ID Producto:", txtIdProducto = crearTextField(), 0, 0);
        agregarCampo(panel, gbc, "Nombre Producto:", txtNombreProducto = crearTextField(), 0, 1);

        comboProveedor = new JComboBox<>();
        comboProveedor.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        actualizarComboProveedores();
        comboProveedor.addActionListener(e -> manejarSeleccionProveedor());
        agregarCampoComponente(panel, gbc, "Proveedor:", comboProveedor, 0, 2);

        txtEmpresa = crearTextField();
        txtEmpresa.setEditable(false);
        txtEmpresa.setBackground(new Color(236, 240, 241));
        agregarCampo(panel, gbc, "Empresa:", txtEmpresa, 1, 0);
        agregarCampo(panel, gbc, "Cantidad:", txtCantidad = crearTextField(), 1, 1);
        aplicarFiltroNumerico(txtCantidad, 8);
        agregarCampo(panel, gbc, "Fecha (DD/MM/YYYY):", txtFecha = crearTextField(), 1, 2);

        agregarCampo(panel, gbc, "Costo Unitario:", txtCosto = crearTextField(), 2, 0);
        aplicarFiltroDecimal(txtCosto, 10);
        agregarCampo(panel, gbc, "Precio Venta:", txtPrecio = crearTextField(), 2, 1);
        aplicarFiltroDecimal(txtPrecio, 10);
        agregarCampo(panel, gbc, "Orden de Pedido:", txtOrdenPedido = crearTextField(), 2, 2);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        gbc.insets = new Insets(15, 5, 5, 5);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        panelBotones.setBackground(Color.WHITE);

        btnRegistrar = crearBoton("Registrar Compra", new Color(46, 204, 113));
        btnRegistrar.addActionListener(e -> registrarCompra());
        panelBotones.add(btnRegistrar);

        btnModificar = crearBoton("Modificar Compra", new Color(52, 152, 219));
        btnModificar.addActionListener(e -> modificarCompra());
        btnModificar.setEnabled(false);
        panelBotones.add(btnModificar);

        btnEliminar = crearBoton("Eliminar Compra", new Color(231, 76, 60));
        btnEliminar.addActionListener(e -> eliminarCompra());
        btnEliminar.setEnabled(false);
        panelBotones.add(btnEliminar);

        btnLimpiar = crearBoton("Limpiar Campos", new Color(149, 165, 166));
        btnLimpiar.addActionListener(e -> cancelarEdicion());
        panelBotones.add(btnLimpiar);

        panel.add(panelBotones, gbc);

        txtFecha.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        return panel;
    }

    private JTextField crearTextField() {
        JTextField textField = new JTextField(15);
        textField.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return textField;
    }

    private void agregarCampo(JPanel panel, GridBagConstraints gbc, String etiqueta, JTextField campo, int fila, int columna) {
        agregarCampoComponente(panel, gbc, etiqueta, campo, fila, columna);
    }

    private void agregarCampoComponente(JPanel panel, GridBagConstraints gbc, String etiqueta, JComponent campo, int fila, int columna) {
        gbc.gridx = columna;
        gbc.gridy = fila;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(5, 5, 5, 5);

        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setBackground(Color.WHITE);

        JLabel lbl = new JLabel(etiqueta);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        contenedor.add(lbl, BorderLayout.NORTH);
        contenedor.add(campo, BorderLayout.CENTER);

        panel.add(contenedor, gbc);
    }

    private JButton crearBoton(String texto, Color color) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        boton.setBackground(color);
        boton.setForeground(Color.WHITE);
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        boton.setPreferredSize(new Dimension(160, 35));
        return boton;
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
            return text.matches("\\d*\\.?\\d*");
        }
    }

    private void aplicarFiltroNumerico(JTextField textField, int maxLength) {
        ((AbstractDocument) textField.getDocument()).setDocumentFilter(new NumericDocumentFilter(maxLength));
    }

    private void aplicarFiltroDecimal(JTextField textField, int maxLength) {
        ((AbstractDocument) textField.getDocument()).setDocumentFilter(new DecimalDocumentFilter(maxLength));
    }

    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(41, 128, 185), 2),
                "Historial de Compras",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 14),
                new Color(41, 128, 185)
            ),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Barra de búsqueda
        JPanel panelBusqueda = new JPanel(new BorderLayout(10, 0));
        panelBusqueda.setBackground(Color.WHITE);

        JLabel lblBuscar = new JLabel("Buscar:");
        lblBuscar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panelBusqueda.add(lblBuscar, BorderLayout.WEST);

        txtBusqueda = new JTextField();
        txtBusqueda.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtBusqueda.setToolTipText("Buscar por ID, producto, proveedor, empresa u orden de pedido");
        panelBusqueda.add(txtBusqueda, BorderLayout.CENTER);

        JPanel panelBotonesBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panelBotonesBusqueda.setBackground(Color.WHITE);

        JButton btnBuscar = crearBoton("Buscar", new Color(41, 128, 185));
        btnBuscar.setPreferredSize(new Dimension(100, 30));
        btnBuscar.addActionListener(e -> buscarCompras());
        panelBotonesBusqueda.add(btnBuscar);

        JButton btnMostrarTodo = crearBoton("Mostrar Todo", new Color(127, 140, 141));
        btnMostrarTodo.setPreferredSize(new Dimension(120, 30));
        btnMostrarTodo.addActionListener(e -> {
            txtBusqueda.setText("");
            actualizarTabla(compraService.obtenerCompras());
        });
        panelBotonesBusqueda.add(btnMostrarTodo);

        panelBusqueda.add(panelBotonesBusqueda, BorderLayout.EAST);
        panel.add(panelBusqueda, BorderLayout.NORTH);

        // Tabla
        String[] columnas = {"ID Compra", "ID Producto", "Producto", "Proveedor", "Empresa",
                            "Cantidad", "Fecha", "Costo", "Precio", "Orden"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tablaCompras = new JTable(modeloTabla);
        tablaCompras.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaCompras.setRowHeight(25);
        tablaCompras.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaCompras.getTableHeader().setBackground(new Color(52, 152, 219));
        tablaCompras.getTableHeader().setForeground(Color.BLACK);
        tablaCompras.setSelectionBackground(new Color(174, 214, 241));
        tablaCompras.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && tablaCompras.getSelectedRow() != -1) {
                cargarCompraSeleccionada();
            }
        });

        txtBusqueda.addActionListener(e -> buscarCompras());

        JScrollPane scrollPane = new JScrollPane(tablaCompras);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199)));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelEstadisticas() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        panel.setBackground(new Color(236, 240, 241));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        lblTotalCompras = new JLabel("Total Compras: 0");
        lblTotalCompras.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTotalCompras.setForeground(new Color(52, 73, 94));
        panel.add(lblTotalCompras);

        lblTotalCostos = new JLabel("Total en Costos: $0.00");
        lblTotalCostos.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTotalCostos.setForeground(new Color(52, 73, 94));
        panel.add(lblTotalCostos);

        return panel;
    }

    private void buscarCompras() {
        String termino = txtBusqueda.getText().trim();
        if (termino.isEmpty()) {
            actualizarTabla(compraService.obtenerCompras());
        } else {
            List<Compra> resultados = compraService.buscarCompras(termino);
            actualizarTabla(resultados);
            if (resultados.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "No se encontraron compras con el término: " + termino,
                    "Sin Resultados",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    private void actualizarTabla(List<Compra> listaCompras) {
        modeloTabla.setRowCount(0);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        for (Compra c : listaCompras) {
            Object[] fila = {
                c.getIdCompra(), c.getIdProducto(), c.getNombreProducto(),
                c.getProveedor(), c.getEmpresa(), c.getCantidad(),
                c.getFecha().format(fmt),
                String.format("$%.2f", c.getCosto()),
                String.format("$%.2f", c.getPrecio()),
                c.getOrdenPedido()
            };
            modeloTabla.addRow(fila);
        }
    }

    private void cargarCompraSeleccionada() {
        int filaSeleccionada = tablaCompras.getSelectedRow();
        if (filaSeleccionada == -1) return;

        modoEdicion = true;
        idCompraEdicion = (String) modeloTabla.getValueAt(filaSeleccionada, 0);

        txtIdProducto.setText((String) modeloTabla.getValueAt(filaSeleccionada, 1));
        txtNombreProducto.setText((String) modeloTabla.getValueAt(filaSeleccionada, 2));
        String proveedorStr = (String) modeloTabla.getValueAt(filaSeleccionada, 3);
        String empresaStr = (String) modeloTabla.getValueAt(filaSeleccionada, 4);
        seleccionarProveedorEnCombo(proveedorStr, empresaStr);
        txtCantidad.setText(String.valueOf(modeloTabla.getValueAt(filaSeleccionada, 5)));
        txtFecha.setText((String) modeloTabla.getValueAt(filaSeleccionada, 6));

        String costoStr = (String) modeloTabla.getValueAt(filaSeleccionada, 7);
        txtCosto.setText(costoStr.replace("$", ""));

        String precioStr = (String) modeloTabla.getValueAt(filaSeleccionada, 8);
        txtPrecio.setText(precioStr.replace("$", ""));

        txtOrdenPedido.setText((String) modeloTabla.getValueAt(filaSeleccionada, 9));

        btnRegistrar.setEnabled(false);
        btnModificar.setEnabled(true);
        btnEliminar.setEnabled(true);
        txtIdProducto.requestFocus();
    }

    private void eliminarCompra() {
        if (idCompraEdicion == null) return;

        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de eliminar la compra " + idCompraEdicion + "?",
            "Confirmar Eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirmacion == JOptionPane.YES_OPTION) {
            if (compraService.eliminarCompra(idCompraEdicion)) {
                actualizarTabla(compraService.obtenerCompras());
                actualizarEstadisticas();
                cancelarEdicion();
                JOptionPane.showMessageDialog(this,
                    "Compra eliminada exitosamente",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Error al eliminar la compra",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void modificarCompra() {
        try {
            if (!validarCampos()) return;

            String idProducto = txtIdProducto.getText().trim();
            String nombreProducto = txtNombreProducto.getText().trim();
            Proveedor provSeleccionado = (Proveedor) comboProveedor.getSelectedItem();
            String proveedor = provSeleccionado.getContacto();
            String empresa = provSeleccionado.getNombreEmpresa();
            int cantidad = Integer.parseInt(txtCantidad.getText().trim());
            LocalDate fecha = LocalDate.parse(txtFecha.getText().trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            double costo = Double.parseDouble(txtCosto.getText().trim());
            double precio = Double.parseDouble(txtPrecio.getText().trim());
            String ordenPedido = txtOrdenPedido.getText().trim();

            Compra compraActualizada = new Compra(idCompraEdicion, idProducto, nombreProducto, proveedor, empresa,
                                                  cantidad, fecha, costo, precio, ordenPedido);

            if (compraService.actualizarCompra(idCompraEdicion, compraActualizada)) {
                actualizarTabla(compraService.obtenerCompras());
                actualizarEstadisticas();
                cancelarEdicion();
                JOptionPane.showMessageDialog(this,
                    "Compra modificada exitosamente\nID: " + idCompraEdicion,
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                    "Error al modificar la compra",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Error: Verifique que Cantidad, Costo y Precio sean números válidos",
                "Error de Formato",
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al modificar compra: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void registrarCompra() {
        try {
            if (!validarCampos()) return;

            String idCompra = compraService.generarIdCompra();
            String idProducto = txtIdProducto.getText().trim();
            String nombreProducto = txtNombreProducto.getText().trim();
            Proveedor provSeleccionado = (Proveedor) comboProveedor.getSelectedItem();
            String proveedor = provSeleccionado.getContacto();
            String empresa = provSeleccionado.getNombreEmpresa();
            int cantidad = Integer.parseInt(txtCantidad.getText().trim());
            LocalDate fecha = LocalDate.parse(txtFecha.getText().trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            double costo = Double.parseDouble(txtCosto.getText().trim());
            double precio = Double.parseDouble(txtPrecio.getText().trim());
            String ordenPedido = txtOrdenPedido.getText().trim();

            Compra compra = new Compra(idCompra, idProducto, nombreProducto, proveedor, empresa,
                                      cantidad, fecha, costo, precio, ordenPedido);
            compraService.agregarCompra(compra);

            // Actualizar stock del producto si existe en catálogo
            Producto prod = productoService.buscarPorId(idProducto);
            if (prod != null) {
                productoService.aumentarStock(idProducto, cantidad);
            }

            actualizarTabla(compraService.obtenerCompras());
            actualizarEstadisticas();
            cancelarEdicion();

            JOptionPane.showMessageDialog(this,
                "Compra registrada exitosamente\nID: " + idCompra,
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Error: Verifique que Cantidad, Costo y Precio sean números válidos",
                "Error de Formato",
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error al registrar compra: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private boolean validarCampos() {
        if (!(comboProveedor.getSelectedItem() instanceof Proveedor)) {
            JOptionPane.showMessageDialog(this,
                "Por favor seleccione un proveedor",
                "Proveedor Requerido",
                JOptionPane.WARNING_MESSAGE);
            return false;
        }
        if (txtIdProducto.getText().trim().isEmpty() ||
            txtNombreProducto.getText().trim().isEmpty() ||
            txtCantidad.getText().trim().isEmpty() ||
            txtFecha.getText().trim().isEmpty() ||
            txtCosto.getText().trim().isEmpty() ||
            txtPrecio.getText().trim().isEmpty() ||
            txtOrdenPedido.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(this,
                "Por favor complete todos los campos",
                "Campos Incompletos",
                JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    private void limpiarCampos() {
        txtIdProducto.setText("");
        txtNombreProducto.setText("");
        actualizarComboProveedores();
        txtEmpresa.setText("");
        txtCantidad.setText("");
        txtFecha.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        txtCosto.setText("");
        txtPrecio.setText("");
        txtOrdenPedido.setText("");
        txtIdProducto.requestFocus();
    }

    private void cancelarEdicion() {
        limpiarCampos();
        modoEdicion = false;
        idCompraEdicion = null;
        btnRegistrar.setEnabled(true);
        btnModificar.setEnabled(false);
        btnEliminar.setEnabled(false);
        tablaCompras.clearSelection();
    }

    private void actualizarEstadisticas() {
        lblTotalCompras.setText("Total Compras: " + compraService.getTotalCompras());
        lblTotalCostos.setText(String.format("Total en Costos: $%.2f", compraService.calcularTotalCostos()));
    }

    // --- Métodos para combo de proveedores ---

    private void actualizarComboProveedores() {
        actualizandoCombo = true;
        comboProveedor.removeAllItems();
        comboProveedor.addItem(COMBO_PLACEHOLDER);
        Proveedor[] activos = proveedorService.obtenerProveedoresActivos();
        for (Proveedor p : activos) {
            comboProveedor.addItem(p);
        }
        comboProveedor.addItem(COMBO_AGREGAR_NUEVO);
        comboProveedor.setSelectedIndex(0);
        actualizandoCombo = false;
    }

    private void manejarSeleccionProveedor() {
        if (actualizandoCombo) return;
        Object selected = comboProveedor.getSelectedItem();
        if (selected instanceof Proveedor) {
            Proveedor p = (Proveedor) selected;
            txtEmpresa.setText(p.getNombreEmpresa());
        } else if (COMBO_AGREGAR_NUEVO.equals(selected)) {
            int countAntes = proveedorService.getTotalProveedores();
            panelProveedores.mostrarDialogoProveedor(null);
            actualizarComboProveedores();
            if (proveedorService.getTotalProveedores() > countAntes) {
                // Seleccionar el proveedor recién agregado (penúltimo item, antes de "Agregar nuevo")
                comboProveedor.setSelectedIndex(comboProveedor.getItemCount() - 2);
            }
        } else {
            txtEmpresa.setText("");
        }
    }

    private void seleccionarProveedorEnCombo(String contacto, String empresa) {
        for (int i = 0; i < comboProveedor.getItemCount(); i++) {
            Object item = comboProveedor.getItemAt(i);
            if (item instanceof Proveedor) {
                Proveedor p = (Proveedor) item;
                if (p.getContacto().equals(contacto) && p.getNombreEmpresa().equals(empresa)) {
                    comboProveedor.setSelectedIndex(i);
                    return;
                }
            }
        }
        // Si no se encontró, mostrar empresa manualmente
        comboProveedor.setSelectedIndex(0);
        txtEmpresa.setText(empresa);
    }
}
