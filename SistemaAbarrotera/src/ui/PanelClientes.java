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

public class PanelClientes extends JPanel {
    private ClienteService clienteService;
    private CreditoService creditoService;
    private JTable tablaClientes;
    private DefaultTableModel modeloTabla;
    private JTextField txtBusqueda;
    private JLabel lblTotal, lblActivos;

    public PanelClientes(ClienteService clienteService, CreditoService creditoService) {
        this.clienteService = clienteService;
        this.creditoService = creditoService;
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
        panel.setBackground(new Color(39, 174, 96));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblTitulo = new JLabel("Gestión de Clientes");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        panel.add(lblTitulo, BorderLayout.WEST);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBotones.setOpaque(false);

        JButton btnAgregar = crearBotonHeader("+ Añadir Cliente");
        btnAgregar.addActionListener(e -> mostrarDialogoCliente(null));
        panelBotones.add(btnAgregar);

        JButton btnEditar = crearBotonHeader("Editar Seleccionado");
        btnEditar.addActionListener(e -> editarClienteSeleccionado());
        panelBotones.add(btnEditar);

        panel.add(panelBotones, BorderLayout.EAST);
        return panel;
    }

    private JButton crearBotonHeader(String texto) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(new Color(46, 204, 113));
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

        JLabel lbl = new JLabel("Buscar cliente:");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panel.add(lbl, BorderLayout.WEST);

        txtBusqueda = new JTextField();
        txtBusqueda.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        txtBusqueda.addActionListener(e -> buscarClientes());
        panel.add(txtBusqueda, BorderLayout.CENTER);

        JPanel panelBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panelBtns.setBackground(Color.WHITE);

        JButton btnBuscar = new JButton("Buscar");
        btnBuscar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnBuscar.setBackground(new Color(39, 174, 96));
        btnBuscar.setForeground(Color.WHITE);
        btnBuscar.setFocusPainted(false);
        btnBuscar.setBorderPainted(false);
        btnBuscar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBuscar.addActionListener(e -> buscarClientes());
        panelBtns.add(btnBuscar);

        JButton btnTodos = new JButton("Mostrar Todos");
        btnTodos.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnTodos.setBackground(new Color(127, 140, 141));
        btnTodos.setForeground(Color.WHITE);
        btnTodos.setFocusPainted(false);
        btnTodos.setBorderPainted(false);
        btnTodos.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnTodos.addActionListener(e -> { txtBusqueda.setText(""); actualizarTabla(clienteService.obtenerClientes()); });
        panelBtns.add(btnTodos);

        panel.add(panelBtns, BorderLayout.EAST);
        return panel;
    }

    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(39, 174, 96), 2),
                "Lista de Clientes",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 14),
                new Color(39, 174, 96)
            ),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        String[] columnas = {"ID", "Nombre", "Apellidos", "Teléfono", "Dirección", "Colonia", "RFC", "Registro", "Status", "Activo"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tablaClientes = new JTable(modeloTabla);
        tablaClientes.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaClientes.setRowHeight(28);
        tablaClientes.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaClientes.getTableHeader().setBackground(new Color(39, 174, 96));
        tablaClientes.getTableHeader().setForeground(Color.BLACK);
        tablaClientes.setSelectionBackground(new Color(171, 235, 198));

        // Renderer para la columna Status con colores
        tablaClientes.getColumnModel().getColumn(8).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(CENTER);
                String status = (String) value;
                if (!isSelected) {
                    switch (status) {
                        case "VERDE":
                            c.setBackground(new Color(212, 245, 212));
                            c.setForeground(new Color(39, 174, 96));
                            break;
                        case "AMARILLO":
                            c.setBackground(new Color(255, 248, 220));
                            c.setForeground(new Color(243, 156, 18));
                            break;
                        case "ROJO":
                            c.setBackground(new Color(255, 220, 220));
                            c.setForeground(new Color(231, 76, 60));
                            break;
                        default:
                            c.setBackground(Color.WHITE);
                            c.setForeground(Color.BLACK);
                    }
                }
                String texto = status.equals("VERDE") ? "● Al corriente" :
                               status.equals("AMARILLO") ? "● Por vencer" :
                               status.equals("ROJO") ? "● Vencido" : status;
                setText(texto);
                return c;
            }
        });

        tablaClientes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    editarClienteSeleccionado();
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tablaClientes);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199)));
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearPanelEstadisticas() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        panel.setBackground(new Color(236, 240, 241));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        lblTotal = new JLabel("Total Clientes: 0");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTotal.setForeground(new Color(52, 73, 94));
        panel.add(lblTotal);

        lblActivos = new JLabel("Activos: 0");
        lblActivos.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblActivos.setForeground(new Color(39, 174, 96));
        panel.add(lblActivos);

        return panel;
    }

    // --- Diálogo flotante para Añadir / Editar ---
    private void mostrarDialogoCliente(Cliente clienteEditar) {
        boolean esEdicion = (clienteEditar != null);
        JDialog dialogo = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
                esEdicion ? "Editar Cliente" : "Añadir Nuevo Cliente", true);
        dialogo.setSize(500, 520);
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
        JTextField txtApellidos = agregarCampoDialogo(form, gbc, "Apellidos:", 1);
        JTextField txtTelefono = agregarCampoDialogo(form, gbc, "Teléfono:", 2);
        aplicarFiltroNumerico(txtTelefono, 15);
        JTextField txtDireccion = agregarCampoDialogo(form, gbc, "Dirección:", 3);
        JTextField txtColonia = agregarCampoDialogo(form, gbc, "Colonia:", 4);
        JTextField txtRfc = agregarCampoDialogo(form, gbc, "RFC (opcional):", 5);
        JTextField txtNotas = agregarCampoDialogo(form, gbc, "Notas:", 6);
        JCheckBox chkActivo = new JCheckBox("Cliente Activo", true);
        chkActivo.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        chkActivo.setBackground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        form.add(chkActivo, gbc);

        if (esEdicion) {
            txtNombre.setText(clienteEditar.getNombre());
            txtApellidos.setText(clienteEditar.getApellidos());
            txtTelefono.setText(clienteEditar.getTelefono());
            txtDireccion.setText(clienteEditar.getDireccion());
            txtColonia.setText(clienteEditar.getColonia());
            txtRfc.setText(clienteEditar.getRfc());
            txtNotas.setText(clienteEditar.getNotas());
            chkActivo.setSelected(clienteEditar.isActivo());
        }

        dialogo.add(form, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelBotones.setBackground(new Color(236, 240, 241));

        JButton btnGuardar = new JButton(esEdicion ? "Guardar Cambios" : "Registrar Cliente");
        btnGuardar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnGuardar.setBackground(new Color(39, 174, 96));
        btnGuardar.setForeground(Color.WHITE);
        btnGuardar.setFocusPainted(false);
        btnGuardar.setBorderPainted(false);
        btnGuardar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnGuardar.addActionListener(e -> {
            if (txtNombre.getText().trim().isEmpty() || txtApellidos.getText().trim().isEmpty() ||
                txtTelefono.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialogo, "Nombre, Apellidos y Teléfono son obligatorios",
                    "Campos incompletos", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (esEdicion) {
                Cliente actualizado = new Cliente(clienteEditar.getIdCliente(),
                    txtNombre.getText().trim(), txtApellidos.getText().trim(),
                    txtTelefono.getText().trim(), txtDireccion.getText().trim(),
                    txtColonia.getText().trim(), txtRfc.getText().trim(),
                    clienteEditar.getFechaRegistro(), txtNotas.getText().trim(),
                    chkActivo.isSelected());
                clienteService.actualizarCliente(clienteEditar.getIdCliente(), actualizado);
                JOptionPane.showMessageDialog(dialogo, "Cliente actualizado exitosamente");
            } else {
                String id = clienteService.generarIdCliente();
                Cliente nuevo = new Cliente(id, txtNombre.getText().trim(), txtApellidos.getText().trim(),
                    txtTelefono.getText().trim(), txtDireccion.getText().trim(),
                    txtColonia.getText().trim(), txtRfc.getText().trim(),
                    LocalDate.now(), txtNotas.getText().trim(), chkActivo.isSelected());
                clienteService.agregarCliente(nuevo);
                JOptionPane.showMessageDialog(dialogo, "Cliente registrado con ID: " + id);
            }
            dialogo.dispose();
            actualizarTabla(clienteService.obtenerClientes());
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

    private void editarClienteSeleccionado() {
        int fila = tablaClientes.getSelectedRow();
        if (fila == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un cliente de la tabla", "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String idCliente = (String) modeloTabla.getValueAt(fila, 0);
        Cliente cliente = clienteService.buscarPorId(idCliente);
        if (cliente != null) {
            mostrarDialogoCliente(cliente);
        }
    }

    private void buscarClientes() {
        String termino = txtBusqueda.getText().trim();
        if (termino.isEmpty()) {
            actualizarTabla(clienteService.obtenerClientes());
        } else {
            Cliente[] resultados = clienteService.buscarClientes(termino);
            actualizarTabla(resultados);
            if (resultados.length == 0) {
                JOptionPane.showMessageDialog(this, "No se encontraron clientes con: " + termino,
                    "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    public void actualizarTabla(Cliente[] lista) {
        modeloTabla.setRowCount(0);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        for (Cliente c : lista) {
            String status = creditoService.getStatusColorCliente(c.getIdCliente());
            Object[] fila = {
                c.getIdCliente(), c.getNombre(), c.getApellidos(), c.getTelefono(),
                c.getDireccion(), c.getColonia(), c.getRfc(),
                c.getFechaRegistro().format(fmt), status,
                c.isActivo() ? "Sí" : "No"
            };
            modeloTabla.addRow(fila);
        }
        actualizarEstadisticas();
    }

    public void refrescar() {
        actualizarTabla(clienteService.obtenerClientes());
    }

    private void actualizarEstadisticas() {
        lblTotal.setText("Total Clientes: " + clienteService.getTotalClientes());
        lblActivos.setText("Activos: " + clienteService.getTotalClientesActivos());
    }
}
