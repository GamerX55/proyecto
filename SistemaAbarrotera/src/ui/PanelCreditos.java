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

public class PanelCreditos extends JPanel {
    private CreditoService creditoService;
    private ClienteService clienteService;
    private JTable tablaCreditos;
    private DefaultTableModel modeloTabla;
    private JLabel lblTotal, lblPendiente, lblRecaudado, lblVencidos;
    private JComboBox<String> comboFiltro;

    public PanelCreditos(CreditoService creditoService, ClienteService clienteService) {
        this.creditoService = creditoService;
        this.clienteService = clienteService;
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        crearComponentes();
    }

    private void crearComponentes() {
        add(crearPanelTitulo(), BorderLayout.NORTH);

        JPanel panelCentral = new JPanel(new BorderLayout(0, 10));
        panelCentral.setBackground(Color.WHITE);
        panelCentral.add(crearPanelResumen(), BorderLayout.NORTH);
        panelCentral.add(crearPanelTabla(), BorderLayout.CENTER);

        add(panelCentral, BorderLayout.CENTER);
        add(crearPanelEstadisticas(), BorderLayout.SOUTH);
    }

    private JPanel crearPanelTitulo() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(211, 84, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel lblTitulo = new JLabel("Gestión de Créditos");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitulo.setForeground(Color.WHITE);
        panel.add(lblTitulo, BorderLayout.WEST);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panelBotones.setOpaque(false);

        JButton btnOtorgar = crearBotonHeader("Otorgar Crédito", new Color(46, 204, 113));
        btnOtorgar.addActionListener(e -> mostrarDialogoOtorgar());
        panelBotones.add(btnOtorgar);

        JButton btnPagar = crearBotonHeader("Registrar Pago", new Color(52, 152, 219));
        btnPagar.addActionListener(e -> mostrarDialogoPagar());
        panelBotones.add(btnPagar);

        JButton btnModificar = crearBotonHeader("Modificar Monto", new Color(243, 156, 18));
        btnModificar.addActionListener(e -> mostrarDialogoModificar());
        panelBotones.add(btnModificar);

        panel.add(panelBotones, BorderLayout.EAST);
        return panel;
    }

    private JButton crearBotonHeader(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
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

    private JPanel crearPanelResumen() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 15, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        lblTotal = crearTarjeta("Créditos Activos", "0", new Color(52, 152, 219));
        lblPendiente = crearTarjeta("Saldo Pendiente", "$0.00", new Color(231, 76, 60));
        lblRecaudado = crearTarjeta("Total Recaudado", "$0.00", new Color(46, 204, 113));
        lblVencidos = crearTarjeta("Vencidos", "0", new Color(192, 57, 43));

        panel.add(obtenerPanelTarjeta(lblTotal, new Color(52, 152, 219)));
        panel.add(obtenerPanelTarjeta(lblPendiente, new Color(231, 76, 60)));
        panel.add(obtenerPanelTarjeta(lblRecaudado, new Color(46, 204, 113)));
        panel.add(obtenerPanelTarjeta(lblVencidos, new Color(192, 57, 43)));

        return panel;
    }

    private JLabel crearTarjeta(String titulo, String valor, Color color) {
        JLabel lbl = new JLabel("<html><center><span style='font-size:10px;'>" + titulo +
                "</span><br><span style='font-size:16px; font-weight:bold;'>" + valor + "</span></center></html>");
        lbl.setForeground(Color.WHITE);
        lbl.setHorizontalAlignment(SwingConstants.CENTER);
        return lbl;
    }

    private JPanel obtenerPanelTarjeta(JLabel label, Color color) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(color);
        p.setBorder(BorderFactory.createEmptyBorder(12, 10, 12, 10));
        p.add(label, BorderLayout.CENTER);
        return p;
    }

    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(211, 84, 0), 2),
                "Créditos Registrados",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 14),
                new Color(211, 84, 0)
            ),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Filtro
        JPanel panelFiltro = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panelFiltro.setBackground(Color.WHITE);
        JLabel lblFiltro = new JLabel("Filtrar por estado:");
        lblFiltro.setFont(new Font("Segoe UI", Font.BOLD, 13));
        panelFiltro.add(lblFiltro);

        comboFiltro = new JComboBox<>(new String[]{"Todos", "No Pagados", "Activos", "Por Vencer", "Vencidos", "Pagados"});
        comboFiltro.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        comboFiltro.addActionListener(e -> filtrarCreditos());
        panelFiltro.add(comboFiltro);

        JButton btnRefrescar = new JButton("Refrescar");
        btnRefrescar.setFont(new Font("Segoe UI", Font.BOLD, 12));
        btnRefrescar.setBackground(new Color(211, 84, 0));
        btnRefrescar.setForeground(Color.WHITE);
        btnRefrescar.setFocusPainted(false);
        btnRefrescar.setBorderPainted(false);
        btnRefrescar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRefrescar.addActionListener(e -> refrescar());
        panelFiltro.add(btnRefrescar);

        panel.add(panelFiltro, BorderLayout.NORTH);

        // Tabla
        String[] columnas = {"ID Crédito", "Cliente", "Monto Otorgado", "Pagado", "Saldo Pendiente",
                            "Otorgamiento", "Vencimiento", "Estado"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tablaCreditos = new JTable(modeloTabla);
        tablaCreditos.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablaCreditos.setRowHeight(30);
        tablaCreditos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));
        tablaCreditos.getTableHeader().setBackground(new Color(211, 84, 0));
        tablaCreditos.getTableHeader().setForeground(Color.BLACK);

        // Renderer de colores por estado en TODA la fila
        DefaultTableCellRenderer rendererColor = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    String estado = (String) table.getModel().getValueAt(row, 7);
                    switch (estado) {
                        case "ACTIVO":
                            c.setBackground(new Color(212, 245, 212));
                            c.setForeground(new Color(39, 100, 39));
                            break;
                        case "POR_VENCER":
                            c.setBackground(new Color(255, 248, 210));
                            c.setForeground(new Color(150, 100, 0));
                            break;
                        case "VENCIDO":
                            c.setBackground(new Color(255, 215, 215));
                            c.setForeground(new Color(150, 30, 30));
                            break;
                        case "PAGADO":
                            c.setBackground(new Color(230, 240, 255));
                            c.setForeground(new Color(60, 60, 120));
                            break;
                        default:
                            c.setBackground(Color.WHITE);
                            c.setForeground(Color.BLACK);
                    }
                }
                // Formato legible para la columna Estado
                if (column == 7) {
                    String estado = (String) value;
                    String texto = estado.equals("ACTIVO") ? "● Activo" :
                                   estado.equals("POR_VENCER") ? "● Por Vencer" :
                                   estado.equals("VENCIDO") ? "● Vencido" :
                                   estado.equals("PAGADO") ? "● Pagado" : estado;
                    setText(texto);
                    setHorizontalAlignment(CENTER);
                }
                return c;
            }
        };

        for (int i = 0; i < tablaCreditos.getColumnCount(); i++) {
            tablaCreditos.getColumnModel().getColumn(i).setCellRenderer(rendererColor);
        }

        JScrollPane scroll = new JScrollPane(tablaCreditos);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(189, 195, 199)));
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelEstadisticas() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 10));
        panel.setBackground(new Color(236, 240, 241));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblLeyenda = new JLabel("● Verde = Al corriente    ● Amarillo = Por vencer (≤7 días)    ● Rojo = Vencido");
        lblLeyenda.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblLeyenda.setForeground(new Color(52, 73, 94));
        panel.add(lblLeyenda);

        return panel;
    }

    // ==================== DIÁLOGOS FLOTANTES ====================

    // --- Otorgar Crédito ---
    private void mostrarDialogoOtorgar() {
        Cliente[] clientesActivos = clienteService.obtenerClientesActivos();
        if (clientesActivos.length == 0) {
            JOptionPane.showMessageDialog(this, "No hay clientes activos registrados.\nRegistre un cliente primero.",
                "Sin Clientes", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog dialogo = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Otorgar Crédito", true);
        dialogo.setSize(450, 350);
        dialogo.setLocationRelativeTo(this);
        dialogo.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Combo de clientes
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        form.add(new JLabel("Cliente:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        JComboBox<String> comboClientes = new JComboBox<>();
        for (Cliente c : clientesActivos) {
            if (!creditoService.clienteTieneCreditoActivo(c.getIdCliente())) {
                comboClientes.addItem(c.getIdCliente() + " - " + c.getNombreCompleto());
            }
        }
        if (comboClientes.getItemCount() == 0) {
            JOptionPane.showMessageDialog(this, "Todos los clientes activos ya tienen un crédito vigente.",
                "Sin Disponibles", JOptionPane.WARNING_MESSAGE);
            return;
        }
        comboClientes.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        form.add(comboClientes, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        form.add(new JLabel("Monto a otorgar ($):"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        JTextField txtMonto = new JTextField();
        txtMonto.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        form.add(txtMonto, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        form.add(new JLabel("Días de plazo:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        JTextField txtDias = new JTextField("30");
        txtDias.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        form.add(txtDias, gbc);

        dialogo.add(form, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelBotones.setBackground(new Color(236, 240, 241));

        JButton btnOtorgar = crearBotonDialogo("Otorgar Crédito", new Color(46, 204, 113));
        btnOtorgar.addActionListener(e -> {
            try {
                String seleccion = (String) comboClientes.getSelectedItem();
                String idCliente = seleccion.split(" - ")[0];
                String nombreCliente = seleccion.substring(seleccion.indexOf(" - ") + 3);
                double monto = Double.parseDouble(txtMonto.getText().trim());
                int dias = Integer.parseInt(txtDias.getText().trim());
                if (monto <= 0 || dias <= 0) throw new NumberFormatException();

                String idCredito = creditoService.generarIdCredito();
                LocalDate hoy = LocalDate.now();
                Credito nuevo = new Credito(idCredito, idCliente, nombreCliente, monto, 0,
                        hoy, hoy.plusDays(dias), Credito.ACTIVO);
                creditoService.agregarCredito(nuevo);

                JOptionPane.showMessageDialog(dialogo,
                    "Crédito otorgado exitosamente\nID: " + idCredito +
                    "\nCliente: " + nombreCliente +
                    "\nMonto: $" + String.format("%.2f", monto) +
                    "\nVencimiento: " + hoy.plusDays(dias).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                dialogo.dispose();
                refrescar();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialogo, "Ingrese un monto y días válidos",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panelBotones.add(btnOtorgar);

        JButton btnCancelar = crearBotonDialogo("Cancelar", new Color(149, 165, 166));
        btnCancelar.addActionListener(e -> dialogo.dispose());
        panelBotones.add(btnCancelar);

        dialogo.add(panelBotones, BorderLayout.SOUTH);
        dialogo.setVisible(true);
    }

    // --- Registrar Pago ---
    private void mostrarDialogoPagar() {
        int filaSeleccionada = tablaCreditos.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un crédito de la tabla",
                "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String idCredito = (String) modeloTabla.getValueAt(filaSeleccionada, 0);
        Credito credito = creditoService.buscarPorId(idCredito);
        if (credito == null || credito.getEstado().equals(Credito.PAGADO)) {
            JOptionPane.showMessageDialog(this, "Este crédito ya está pagado",
                "Crédito Pagado", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JDialog dialogo = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Registrar Pago", true);
        dialogo.setSize(480, 520);
        dialogo.setLocationRelativeTo(this);
        dialogo.setLayout(new BorderLayout());

        JPanel panelPrincipal = new JPanel();
        panelPrincipal.setLayout(new BoxLayout(panelPrincipal, BoxLayout.Y_AXIS));
        panelPrincipal.setBackground(Color.WHITE);
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(15, 20, 10, 20));

        // === Info del crédito ===
        JPanel info = new JPanel(new GridBagLayout());
        info.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 5, 4, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; gbc.weightx = 0.4;

        gbc.gridy = 0; info.add(new JLabel("Crédito:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.6;
        info.add(new JLabel(idCredito + " - " + credito.getNombreCliente()), gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.4;
        info.add(new JLabel("Monto Otorgado:"), gbc);
        gbc.gridx = 1;
        info.add(new JLabel(String.format("$%.2f", credito.getMontoOtorgado())), gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.4;
        info.add(new JLabel("Ya Pagado:"), gbc);
        gbc.gridx = 1;
        info.add(new JLabel(String.format("$%.2f", credito.getMontoPagado())), gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.4;
        JLabel lblSaldo = new JLabel("Saldo Pendiente:");
        lblSaldo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblSaldo.setForeground(new Color(231, 76, 60));
        info.add(lblSaldo, gbc);
        gbc.gridx = 1;
        JLabel lblSaldoVal = new JLabel(String.format("$%.2f", credito.getSaldoPendiente()));
        lblSaldoVal.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblSaldoVal.setForeground(new Color(231, 76, 60));
        info.add(lblSaldoVal, gbc);

        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.4;
        info.add(new JLabel("Monto a pagar ($):"), gbc);
        gbc.gridx = 1;
        JTextField txtPago = new JTextField();
        txtPago.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        info.add(txtPago, gbc);

        gbc.gridx = 1; gbc.gridy = 5;
        JButton btnPagarTodo = new JButton("Pagar todo ($" + String.format("%.2f", credito.getSaldoPendiente()) + ")");
        btnPagarTodo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        btnPagarTodo.setForeground(new Color(52, 152, 219));
        btnPagarTodo.setBorderPainted(false);
        btnPagarTodo.setContentAreaFilled(false);
        btnPagarTodo.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnPagarTodo.addActionListener(e -> txtPago.setText(String.format("%.2f", credito.getSaldoPendiente())));
        info.add(btnPagarTodo, gbc);

        // Método de pago
        gbc.gridx = 0; gbc.gridy = 6; gbc.weightx = 0.4;
        JLabel lblMetodo = new JLabel("Método de pago:");
        lblMetodo.setFont(new Font("Segoe UI", Font.BOLD, 13));
        info.add(lblMetodo, gbc);
        gbc.gridx = 1; gbc.weightx = 0.6;
        JComboBox<String> comboMetodoPago = new JComboBox<>(new String[]{
            "Efectivo", "Transferencia", "Tarjeta de Crédito", "Tarjeta de Débito"});
        comboMetodoPago.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        info.add(comboMetodoPago, gbc);

        panelPrincipal.add(info);

        // === Panel dinámico para campos extra según método ===
        CardLayout cardMetodo = new CardLayout();
        JPanel panelMetodoExtra = new JPanel(cardMetodo);
        panelMetodoExtra.setBackground(Color.WHITE);

        // Efectivo: sin campos extra
        JPanel panelEfectivo = new JPanel();
        panelEfectivo.setBackground(Color.WHITE);
        panelMetodoExtra.add(panelEfectivo, "Efectivo");

        // Transferencia
        JPanel panelTransferencia = new JPanel(new GridBagLayout());
        panelTransferencia.setBackground(Color.WHITE);
        panelTransferencia.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(52, 152, 219), 1),
                "Datos de Transferencia", 0, 0,
                new Font("Segoe UI", Font.BOLD, 12), new Color(52, 152, 219)),
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
        JTextField txtReferencia = new JTextField();
        txtReferencia.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panelTransferencia.add(txtReferencia, gt);

        gt.gridx = 0; gt.gridy = 2; gt.weightx = 0.4;
        panelTransferencia.add(new JLabel("Fecha transferencia:"), gt);
        gt.gridx = 1; gt.weightx = 0.6;
        JTextField txtFechaTransf = new JTextField(LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        txtFechaTransf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        panelTransferencia.add(txtFechaTransf, gt);

        panelMetodoExtra.add(panelTransferencia, "Transferencia");

        // Tarjeta de Crédito
        JPanel panelTCredito = new JPanel(new GridBagLayout());
        panelTCredito.setBackground(Color.WHITE);
        panelTCredito.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(243, 156, 18), 1),
                "Datos de Tarjeta de Crédito", 0, 0,
                new Font("Segoe UI", Font.BOLD, 12), new Color(243, 156, 18)),
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

        // Tarjeta de Débito
        JPanel panelTDebito = new JPanel(new GridBagLayout());
        panelTDebito.setBackground(Color.WHITE);
        panelTDebito.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(46, 204, 113), 1),
                "Datos de Tarjeta de Débito", 0, 0,
                new Font("Segoe UI", Font.BOLD, 12), new Color(46, 204, 113)),
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

        comboMetodoPago.addActionListener(ev -> {
            String sel = (String) comboMetodoPago.getSelectedItem();
            cardMetodo.show(panelMetodoExtra, sel);
            dialogo.revalidate();
            dialogo.repaint();
        });

        panelPrincipal.add(panelMetodoExtra);

        JScrollPane scroll = new JScrollPane(panelPrincipal);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(10);
        dialogo.add(scroll, BorderLayout.CENTER);

        // === Botones ===
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelBotones.setBackground(new Color(236, 240, 241));

        JButton btnPagar = crearBotonDialogo("Registrar Pago", new Color(52, 152, 219));
        btnPagar.addActionListener(e -> {
            try {
                double monto = Double.parseDouble(txtPago.getText().trim());
                if (monto <= 0) throw new NumberFormatException();
                if (monto > credito.getSaldoPendiente()) {
                    JOptionPane.showMessageDialog(dialogo,
                        "El monto no puede ser mayor al saldo pendiente ($" +
                        String.format("%.2f", credito.getSaldoPendiente()) + ")",
                        "Monto excedido", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String metodoPago = (String) comboMetodoPago.getSelectedItem();
                String detalleMetodo = "";

                // Validar campos según método
                if ("Transferencia".equals(metodoPago)) {
                    if (txtBancoOrigen.getText().trim().isEmpty() ||
                        txtReferencia.getText().trim().isEmpty()) {
                        JOptionPane.showMessageDialog(dialogo,
                            "Complete Banco origen y Nº de referencia",
                            "Campos incompletos", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    detalleMetodo = "\nBanco: " + txtBancoOrigen.getText().trim() +
                        "\nReferencia: " + txtReferencia.getText().trim() +
                        "\nFecha: " + txtFechaTransf.getText().trim();
                } else if ("Tarjeta de Crédito".equals(metodoPago)) {
                    if (txtUltimos4TC.getText().trim().isEmpty() ||
                        txtTitularTC.getText().trim().isEmpty() ||
                        txtAutorizacionTC.getText().trim().isEmpty()) {
                        JOptionPane.showMessageDialog(dialogo,
                            "Complete todos los datos de la tarjeta de crédito",
                            "Campos incompletos", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    // Validación adicional de longitud
                    if (txtUltimos4TC.getText().trim().length() != 4) {
                        JOptionPane.showMessageDialog(dialogo,
                            "Los últimos 4 dígitos deben ser exactamente 4 números",
                            "Validación de Tarjeta", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    detalleMetodo = "\nTarjeta: ****" + txtUltimos4TC.getText().trim() +
                        "\nTitular: " + txtTitularTC.getText().trim() +
                        "\nAutorización: " + txtAutorizacionTC.getText().trim();
                } else if ("Tarjeta de Débito".equals(metodoPago)) {
                    if (txtUltimos4TD.getText().trim().isEmpty() ||
                        txtTitularTD.getText().trim().isEmpty() ||
                        txtAutorizacionTD.getText().trim().isEmpty()) {
                        JOptionPane.showMessageDialog(dialogo,
                            "Complete todos los datos de la tarjeta de débito",
                            "Campos incompletos", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    // Validación adicional de longitud
                    if (txtUltimos4TD.getText().trim().length() != 4) {
                        JOptionPane.showMessageDialog(dialogo,
                            "Los últimos 4 dígitos deben ser exactamente 4 números",
                            "Validación de Tarjeta", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    detalleMetodo = "\nTarjeta: ****" + txtUltimos4TD.getText().trim() +
                        "\nTitular: " + txtTitularTD.getText().trim() +
                        "\nAutorización: " + txtAutorizacionTD.getText().trim();
                }

                if (creditoService.registrarPago(idCredito, monto)) {
                    String msg = "Pago registrado exitosamente" +
                        "\nMonto: $" + String.format("%.2f", monto) +
                        "\nMétodo: " + metodoPago + detalleMetodo;
                    if (credito.getEstado().equals(Credito.PAGADO)) {
                        msg += "\n\n¡Crédito LIQUIDADO completamente!";
                    } else {
                        msg += "\nSaldo restante: $" + String.format("%.2f", credito.getSaldoPendiente());
                    }
                    JOptionPane.showMessageDialog(dialogo, msg, "Pago Exitoso", JOptionPane.INFORMATION_MESSAGE);
                    dialogo.dispose();
                    refrescar();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialogo, "Ingrese un monto válido",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panelBotones.add(btnPagar);

        JButton btnCancelar = crearBotonDialogo("Cancelar", new Color(149, 165, 166));
        btnCancelar.addActionListener(e -> dialogo.dispose());
        panelBotones.add(btnCancelar);

        dialogo.add(panelBotones, BorderLayout.SOUTH);
        dialogo.setVisible(true);
    }

    // --- Modificar Monto ---
    private void mostrarDialogoModificar() {
        int filaSeleccionada = tablaCreditos.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un crédito de la tabla",
                "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String idCredito = (String) modeloTabla.getValueAt(filaSeleccionada, 0);
        Credito credito = creditoService.buscarPorId(idCredito);
        if (credito == null || credito.getEstado().equals(Credito.PAGADO)) {
            JOptionPane.showMessageDialog(this, "No se puede modificar un crédito ya pagado",
                "Crédito Pagado", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JDialog dialogo = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Modificar Monto de Crédito", true);
        dialogo.setSize(420, 280);
        dialogo.setLocationRelativeTo(this);
        dialogo.setLayout(new BorderLayout());

        JPanel info = new JPanel(new GridBagLayout());
        info.setBackground(Color.WHITE);
        info.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 5, 8, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.4;
        info.add(new JLabel("Crédito:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.6;
        info.add(new JLabel(idCredito + " - " + credito.getNombreCliente()), gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        info.add(new JLabel("Monto Actual:"), gbc);
        gbc.gridx = 1;
        info.add(new JLabel(String.format("$%.2f", credito.getMontoOtorgado())), gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        info.add(new JLabel("Ya Pagado:"), gbc);
        gbc.gridx = 1;
        info.add(new JLabel(String.format("$%.2f (mínimo permitido)", credito.getMontoPagado())), gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        info.add(new JLabel("Nuevo Monto ($):"), gbc);
        gbc.gridx = 1;
        JTextField txtNuevoMonto = new JTextField(String.format("%.2f", credito.getMontoOtorgado()));
        txtNuevoMonto.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        info.add(txtNuevoMonto, gbc);

        dialogo.add(info, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panelBotones.setBackground(new Color(236, 240, 241));

        JButton btnGuardar = crearBotonDialogo("Guardar Cambio", new Color(243, 156, 18));
        btnGuardar.addActionListener(e -> {
            try {
                double nuevoMonto = Double.parseDouble(txtNuevoMonto.getText().trim());
                if (nuevoMonto < credito.getMontoPagado()) {
                    JOptionPane.showMessageDialog(dialogo,
                        "El nuevo monto no puede ser menor a lo ya pagado ($" +
                        String.format("%.2f", credito.getMontoPagado()) + ")",
                        "Monto inválido", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                if (creditoService.modificarMonto(idCredito, nuevoMonto)) {
                    JOptionPane.showMessageDialog(dialogo,
                        "Monto modificado exitosamente\nNuevo monto: $" + String.format("%.2f", nuevoMonto) +
                        "\nSaldo pendiente: $" + String.format("%.2f", credito.getSaldoPendiente()),
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    dialogo.dispose();
                    refrescar();
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialogo, "Ingrese un monto válido",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        panelBotones.add(btnGuardar);

        JButton btnCancelar = crearBotonDialogo("Cancelar", new Color(149, 165, 166));
        btnCancelar.addActionListener(e -> dialogo.dispose());
        panelBotones.add(btnCancelar);

        dialogo.add(panelBotones, BorderLayout.SOUTH);
        dialogo.setVisible(true);
    }

    private JButton crearBotonDialogo(String texto, Color color) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    // ==================== ACTUALIZACIÓN ====================

    private void filtrarCreditos() {
        String filtro = (String) comboFiltro.getSelectedItem();
        Credito[] lista;
        switch (filtro) {
            case "No Pagados": lista = creditoService.obtenerCreditosNoPagados(); break;
            case "Activos": lista = creditoService.obtenerCreditosPorEstado(Credito.ACTIVO); break;
            case "Por Vencer": lista = creditoService.obtenerCreditosPorEstado(Credito.POR_VENCER); break;
            case "Vencidos": lista = creditoService.obtenerCreditosPorEstado(Credito.VENCIDO); break;
            case "Pagados": lista = creditoService.obtenerCreditosPorEstado(Credito.PAGADO); break;
            default: lista = creditoService.obtenerCreditos(); break;
        }
        actualizarTabla(lista);
    }

    public void actualizarTabla(Credito[] lista) {
        modeloTabla.setRowCount(0);
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        for (Credito c : lista) {
            Object[] fila = {
                c.getIdCredito(), c.getNombreCliente(),
                String.format("$%.2f", c.getMontoOtorgado()),
                String.format("$%.2f", c.getMontoPagado()),
                String.format("$%.2f", c.getSaldoPendiente()),
                c.getFechaOtorgamiento().format(fmt),
                c.getFechaVencimiento().format(fmt),
                c.getEstado()
            };
            modeloTabla.addRow(fila);
        }
        actualizarResumen();
    }

    private void actualizarResumen() {
        Credito[] noPagados = creditoService.obtenerCreditosNoPagados();
        Credito[] vencidos = creditoService.obtenerCreditosPorEstado(Credito.VENCIDO);

        lblTotal.setText("<html><center><span style='font-size:10px;'>Créditos Activos</span><br>" +
                "<span style='font-size:16px; font-weight:bold;'>" + noPagados.length + "</span></center></html>");
        lblPendiente.setText("<html><center><span style='font-size:10px;'>Saldo Pendiente</span><br>" +
                "<span style='font-size:16px; font-weight:bold;'>" + String.format("$%.2f", creditoService.getTotalSaldoPendiente()) + "</span></center></html>");
        lblRecaudado.setText("<html><center><span style='font-size:10px;'>Total Recaudado</span><br>" +
                "<span style='font-size:16px; font-weight:bold;'>" + String.format("$%.2f", creditoService.getTotalRecaudado()) + "</span></center></html>");
        lblVencidos.setText("<html><center><span style='font-size:10px;'>Vencidos</span><br>" +
                "<span style='font-size:16px; font-weight:bold;'>" + vencidos.length + "</span></center></html>");
    }

    public void refrescar() {
        filtrarCreditos();
    }
}
