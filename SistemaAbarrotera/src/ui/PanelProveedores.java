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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PanelProveedores extends JPanel {
    private ProveedorService proveedorService;
    private JTable tablaProveedores;
    private DefaultTableModel modeloTabla;
    private JTextField txtBusqueda;
    private JLabel lblTotal, lblActivos;

    private static final Color COLOR_PRINCIPAL = new Color(142, 68, 173);
    private static final Color COLOR_CLARO = new Color(165, 105, 189);
    private static final Color COLOR_SELECCION = new Color(215, 189, 226);

    public PanelProveedores(ProveedorService proveedorService) {
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

        JLabel lblTitulo = new JLabel("Gestión de Proveedores");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        panel.add(lblTitulo, BorderLayout.WEST);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBotones.setOpaque(false);

        JButton btnAgregar = crearBotonHeader("+ Añadir Proveedor");
        btnAgregar.addActionListener(e -> mostrarDialogoProveedor(null));
        panelBotones.add(btnAgregar);

        JButton btnEditar = crearBotonHeader("Editar Seleccionado");
        btnEditar.addActionListener(e -> editarProveedorSeleccionado());
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

    // Filtro para permitir solo números
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

    private void aplicarFiltroNumerico(JTextField textField, int maxLength) {
        ((AbstractDocument) textField.getDocument()).setDocumentFilter(new NumericDocumentFilter(maxLength));
    }

    private JPanel crearPanelBusqueda() {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));

        JLabel lbl = new JLabel("Buscar proveedor:");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panel.add(lbl, BorderLayout.WEST);

        txtBusqueda = new JTextField();
        txtBusqueda.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtBusqueda.addActionListener(e -> buscarProveedores());
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
        btnBuscar.addActionListener(e -> buscarProveedores());
        panelBtns.add(btnBuscar);

        JButton btnTodos = new JButton("Mostrar Todos");
        btnTodos.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnTodos.setBackground(new Color(127, 140, 141));
        btnTodos.setForeground(Color.WHITE);
        btnTodos.setFocusPainted(false);
        btnTodos.setBorderPainted(false);
        btnTodos.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTodos.addActionListener(e -> { txtBusqueda.setText(""); actualizarTabla(proveedorService.obtenerProveedores()); });
        panelBtns.add(btnTodos);

        panel.add(panelBtns, BorderLayout.EAST);
        return panel;
    }

    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(COLOR_PRINCIPAL, 2),
                "Lista de Proveedores",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 14),
                COLOR_PRINCIPAL
            ),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        String[] columnas = {"ID", "Empresa", "Vendedor", "Teléfono", "Email", "Dirección", "Ciudad", "RFC", "Registro", "Activo"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tablaProveedores = new JTable(modeloTabla);
        tablaProveedores.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaProveedores.setRowHeight(28);
        tablaProveedores.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaProveedores.getTableHeader().setBackground(COLOR_PRINCIPAL);
        tablaProveedores.getTableHeader().setForeground(Color.BLACK);
        tablaProveedores.setSelectionBackground(COLOR_SELECCION);

        // Renderer para columna Activo con colores
        tablaProveedores.getColumnModel().getColumn(9).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(CENTER);
                if (!isSelected) {
                    if ("Sí".equals(value)) {
                        c.setBackground(new Color(212, 245, 212));
                        c.setForeground(new Color(39, 174, 96));
                    } else {
                        c.setBackground(new Color(255, 220, 220));
                        c.setForeground(new Color(231, 76, 60));
                    }
                }
                return c;
            }
        });

        tablaProveedores.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    editarProveedorSeleccionado();
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tablaProveedores);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199)));
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelEstadisticas() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        panel.setBackground(new Color(236, 240, 241));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        lblTotal = new JLabel("Total Proveedores: 0");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTotal.setForeground(new Color(52, 73, 94));
        panel.add(lblTotal);

        lblActivos = new JLabel("Activos: 0");
        lblActivos.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblActivos.setForeground(COLOR_PRINCIPAL);
        panel.add(lblActivos);

        return panel;
    }

    // --- Diálogo flotante para Añadir / Editar ---
    public void mostrarDialogoProveedor(Proveedor proveedorEditar) {
        boolean esEdicion = (proveedorEditar != null);
        JDialog dialogo = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                esEdicion ? "Editar Proveedor" : "Añadir Nuevo Proveedor", true);
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

        JTextField txtEmpresa = agregarCampoDialogo(form, gbc, "Nombre Empresa:", 0);
        JTextField txtContacto = agregarCampoDialogo(form, gbc, "Vendedor / Contacto:", 1);
        JTextField txtTelefono = agregarCampoDialogo(form, gbc, "Teléfono:", 2);
        aplicarFiltroNumerico(txtTelefono, 15);
        JTextField txtEmail = agregarCampoDialogo(form, gbc, "Email:", 3);
        JTextField txtDireccion = agregarCampoDialogo(form, gbc, "Dirección:", 4);
        JTextField txtCiudad = agregarCampoDialogo(form, gbc, "Ciudad:", 5);
        JTextField txtRfc = agregarCampoDialogo(form, gbc, "RFC (opcional):", 6);
        JTextField txtNotas = agregarCampoDialogo(form, gbc, "Notas:", 7);
        JCheckBox chkActivo = new JCheckBox("Proveedor Activo", true);
        chkActivo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        chkActivo.setBackground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 2;
        form.add(chkActivo, gbc);

        if (esEdicion) {
            txtEmpresa.setText(proveedorEditar.getNombreEmpresa());
            txtContacto.setText(proveedorEditar.getContacto());
            txtTelefono.setText(proveedorEditar.getTelefono());
            txtEmail.setText(proveedorEditar.getEmail());
            txtDireccion.setText(proveedorEditar.getDireccion());
            txtCiudad.setText(proveedorEditar.getCiudad());
            txtRfc.setText(proveedorEditar.getRfc());
            txtNotas.setText(proveedorEditar.getNotas());
            chkActivo.setSelected(proveedorEditar.isActivo());
        }

        dialogo.add(form, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelBotones.setBackground(new Color(236, 240, 241));

        JButton btnGuardar = new JButton(esEdicion ? "Guardar Cambios" : "Registrar Proveedor");
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnGuardar.setBackground(COLOR_PRINCIPAL);
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setBorderPainted(false);
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGuardar.addActionListener(e -> {
            if (txtEmpresa.getText().trim().isEmpty() || txtContacto.getText().trim().isEmpty() ||
                txtTelefono.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialogo, "Empresa, Contacto y Teléfono son obligatorios",
                    "Campos incompletos", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (esEdicion) {
                Proveedor actualizado = new Proveedor(proveedorEditar.getIdProveedor(),
                    txtEmpresa.getText().trim(), txtContacto.getText().trim(),
                    txtTelefono.getText().trim(), txtEmail.getText().trim(),
                    txtDireccion.getText().trim(), txtCiudad.getText().trim(),
                    txtRfc.getText().trim(), proveedorEditar.getFechaRegistro(),
                    txtNotas.getText().trim(), chkActivo.isSelected());
                proveedorService.actualizarProveedor(proveedorEditar.getIdProveedor(), actualizado);
                JOptionPane.showMessageDialog(dialogo, "Proveedor actualizado exitosamente");
            } else {
                String id = proveedorService.generarIdProveedor();
                Proveedor nuevo = new Proveedor(id, txtEmpresa.getText().trim(),
                    txtContacto.getText().trim(), txtTelefono.getText().trim(),
                    txtEmail.getText().trim(), txtDireccion.getText().trim(),
                    txtCiudad.getText().trim(), txtRfc.getText().trim(),
                    LocalDate.now(), txtNotas.getText().trim(), chkActivo.isSelected());
                proveedorService.agregarProveedor(nuevo);
                JOptionPane.showMessageDialog(dialogo, "Proveedor registrado con ID: " + id);
            }
            dialogo.dispose();
            actualizarTabla(proveedorService.obtenerProveedores());
            actualizarEstadisticas();
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

    private void editarProveedorSeleccionado() {
        int fila = tablaProveedores.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un proveedor de la tabla", "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String idProveedor = (String) modeloTabla.getValueAt(fila, 0);
        Proveedor proveedor = proveedorService.buscarPorId(idProveedor);
        if (proveedor != null) {
            mostrarDialogoProveedor(proveedor);
        }
    }

    private void buscarProveedores() {
        String termino = txtBusqueda.getText().trim();
        if (termino.isEmpty()) {
            actualizarTabla(proveedorService.obtenerProveedores());
        } else {
            Proveedor[] resultados = proveedorService.buscarProveedores(termino);
            actualizarTabla(resultados);
            if (resultados.length == 0) {
                JOptionPane.showMessageDialog(this, "No se encontraron proveedores con: " + termino,
                    "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    public void actualizarTabla(Proveedor[] lista) {
        modeloTabla.setRowCount(0);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        for (Proveedor p : lista) {
            Object[] fila = {
                p.getIdProveedor(), p.getNombreEmpresa(), p.getContacto(), p.getTelefono(),
                p.getEmail(), p.getDireccion(), p.getCiudad(), p.getRfc(),
                p.getFechaRegistro().format(fmt),
                p.isActivo() ? "Sí" : "No"
            };
            modeloTabla.addRow(fila);
        }
        actualizarEstadisticas();
    }

    public void refrescar() {
        actualizarTabla(proveedorService.obtenerProveedores());
    }

    private void actualizarEstadisticas() {
        lblTotal.setText("Total Proveedores: " + proveedorService.getTotalProveedores());
        lblActivos.setText("Activos: " + proveedorService.getTotalProveedoresActivos());
    }
}
