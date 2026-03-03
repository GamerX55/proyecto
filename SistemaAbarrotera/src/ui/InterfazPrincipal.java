package ui;

import modelo.*;
import servicio.*;

import javax.swing.*;
import java.awt.*;

public class InterfazPrincipal extends JFrame {
    private CompraService compraService;
    private ClienteService clienteService;
    private CreditoService creditoService;
    private ProveedorService proveedorService;
    private ProductoService productoService;
    private VentaService ventaService;
    private JPanel panelCentral;
    private CardLayout cardLayout;
    private PanelReportes panelReportes;
    private PanelClientes panelClientes;
    private PanelCreditos panelCreditos;
    private PanelProveedores panelProveedores;
    private PanelProductos panelProductos;
    private PanelInventario panelInventario;
    private PanelCaja panelCaja;
    private PanelHistorialVentas panelHistorialVentas;

    public InterfazPrincipal() {
        compraService = new CompraService();
        clienteService = new ClienteService();
        creditoService = new CreditoService();
        proveedorService = new ProveedorService();
        productoService = new ProductoService();
        ventaService = new VentaService();
        configurarVentana();
        crearComponentes();
    }

    private void configurarVentana() {
        setTitle("Sistema de Gestión - Abarrotera");
        setSize(1400, 850);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
    }

    private void crearComponentes() {
        JPanel panelMenu = crearPanelMenu();
        add(panelMenu, BorderLayout.WEST);

        cardLayout = new CardLayout();
        panelCentral = new JPanel(cardLayout);
        panelCentral.setBackground(Color.WHITE);

        panelCentral.add(crearPanelInicio(), "inicio");

        panelProveedores = new PanelProveedores(proveedorService);
        panelCentral.add(panelProveedores, "proveedores");

        panelCentral.add(new PanelModuloCompras(compraService, proveedorService, productoService, panelProveedores), "compras");

        panelClientes = new PanelClientes(clienteService, creditoService);
        panelCentral.add(panelClientes, "clientes");

        panelCreditos = new PanelCreditos(creditoService, clienteService);
        panelCentral.add(panelCreditos, "creditos");

        panelProductos = new PanelProductos(productoService, proveedorService);
        panelCentral.add(panelProductos, "productos");

        panelInventario = new PanelInventario(productoService);
        panelCentral.add(panelInventario, "inventario");

        panelCaja = new PanelCaja(productoService, ventaService);
        panelCentral.add(panelCaja, "caja");

        panelHistorialVentas = new PanelHistorialVentas(ventaService);
        panelCentral.add(panelHistorialVentas, "historial");

        panelReportes = new PanelReportes(compraService, ventaService, productoService, clienteService, proveedorService);
        panelCentral.add(panelReportes, "reportes");

        add(panelCentral, BorderLayout.CENTER);

        cardLayout.show(panelCentral, "inicio");
    }

    private JPanel crearPanelMenu() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(44, 62, 80));
        panel.setPreferredSize(new Dimension(250, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        JPanel panelHeader = new JPanel();
        panelHeader.setBackground(new Color(52, 73, 94));
        panelHeader.setMaximumSize(new Dimension(250, 100));
        panelHeader.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel lblTitulo = new JLabel("<html><center>SISTEMA<br>ABARROTERA</center></html>");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelHeader.add(lblTitulo);
        
        panel.add(panelHeader);
        panel.add(Box.createRigidArea(new Dimension(0, 30)));

        panel.add(crearBotonMenu("   Inicio", "inicio"));
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(crearBotonMenu("   Caja (POS)", "caja"));
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(crearBotonMenu("   Productos", "productos"));
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(crearBotonMenu("   Inventario", "inventario"));
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(crearBotonMenu("   Compras", "compras"));
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(crearBotonMenu("   Proveedores", "proveedores"));
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(crearBotonMenu("   Clientes", "clientes"));
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(crearBotonMenu("   Creditos", "creditos"));
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(crearBotonMenu("   Historial Ventas", "historial"));
        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(crearBotonMenu("   Reportes", "reportes"));
        
        panel.add(Box.createVerticalGlue());

        JPanel panelInfo = new JPanel();
        panelInfo.setBackground(new Color(44, 62, 80));
        panelInfo.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
        panelInfo.setMaximumSize(new Dimension(250, 60));
        
        JLabel lblVersion = new JLabel("<html><center>Versión 1.0<br>© 2026</center></html>");
        lblVersion.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblVersion.setForeground(new Color(149, 165, 166));
        lblVersion.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelInfo.add(lblVersion);
        
        panel.add(panelInfo);

        return panel;
    }

    private JButton crearBotonMenu(String texto, String panel) {
        JButton boton = new JButton(texto);
        boton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        boton.setForeground(Color.WHITE);
        boton.setBackground(new Color(44, 62, 80));
        boton.setMaximumSize(new Dimension(250, 50));
        boton.setAlignmentX(Component.LEFT_ALIGNMENT);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setHorizontalAlignment(SwingConstants.LEFT);
        boton.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 20));
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                boton.setBackground(new Color(52, 73, 94));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                boton.setBackground(new Color(44, 62, 80));
            }
        });

        boton.addActionListener(e -> {
            cardLayout.show(panelCentral, panel);
            if ("reportes".equals(panel)) {
                panelReportes.actualizarReportes();
            } else if ("clientes".equals(panel)) {
                panelClientes.refrescar();
            } else if ("proveedores".equals(panel)) {
                panelProveedores.refrescar();
            } else if ("creditos".equals(panel)) {
                panelCreditos.refrescar();
            } else if ("productos".equals(panel)) {
                panelProductos.refrescar();
            } else if ("inventario".equals(panel)) {
                panelInventario.refrescar();
            } else if ("historial".equals(panel)) {
                panelHistorialVentas.refrescar();
            }
        });

        return boton;
    }

    private JPanel crearPanelInicio() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);

        JPanel contenido = new JPanel();
        contenido.setLayout(new BoxLayout(contenido, BoxLayout.Y_AXIS));
        contenido.setBackground(Color.WHITE);

        JLabel lblBienvenida = new JLabel("Bienvenido al Sistema de Abarrotera");
        lblBienvenida.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblBienvenida.setForeground(new Color(52, 73, 94));
        lblBienvenida.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblDescripcion = new JLabel("<html><center>Utiliza el menú lateral para navegar<br>entre los diferentes módulos del sistema</center></html>");
        lblDescripcion.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblDescripcion.setForeground(new Color(127, 140, 141));
        lblDescripcion.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblDescripcion.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        contenido.add(lblBienvenida);
        contenido.add(lblDescripcion);

        panel.add(contenido);
        return panel;
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            InterfazPrincipal ventana = new InterfazPrincipal();
            ventana.setVisible(true);
        });
    }
}
