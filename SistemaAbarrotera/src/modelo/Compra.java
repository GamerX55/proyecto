package modelo;

import java.time.LocalDate;

public class Compra {
    private String idCompra;
    private String idProducto;
    private String nombreProducto;
    private String proveedor;
    private String empresa;
    private int cantidad;
    private LocalDate fecha;
    private double costo;
    private double precio;
    private String ordenPedido;

    public Compra(String idCompra, String idProducto, String nombreProducto, String proveedor,
                  String empresa, int cantidad, LocalDate fecha, double costo, double precio, String ordenPedido) {
        this.idCompra = idCompra;
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.proveedor = proveedor;
        this.empresa = empresa;
        this.cantidad = cantidad;
        this.fecha = fecha;
        this.costo = costo;
        this.precio = precio;
        this.ordenPedido = ordenPedido;
    }

    public String getIdCompra() { return idCompra; }
    public String getIdProducto() { return idProducto; }
    public String getNombreProducto() { return nombreProducto; }
    public String getProveedor() { return proveedor; }
    public String getEmpresa() { return empresa; }
    public int getCantidad() { return cantidad; }
    public LocalDate getFecha() { return fecha; }
    public double getCosto() { return costo; }
    public double getPrecio() { return precio; }
    public String getOrdenPedido() { return ordenPedido; }

    public void setIdCompra(String idCompra) { this.idCompra = idCompra; }
    public void setIdProducto(String idProducto) { this.idProducto = idProducto; }
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }
    public void setProveedor(String proveedor) { this.proveedor = proveedor; }
    public void setEmpresa(String empresa) { this.empresa = empresa; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public void setCosto(double costo) { this.costo = costo; }
    public void setPrecio(double precio) { this.precio = precio; }
    public void setOrdenPedido(String ordenPedido) { this.ordenPedido = ordenPedido; }

    @Override
    public String toString() {
        return "Compra{" +
                "idCompra='" + idCompra + '\'' +
                ", idProducto='" + idProducto + '\'' +
                ", nombreProducto='" + nombreProducto + '\'' +
                ", proveedor='" + proveedor + '\'' +
                ", empresa='" + empresa + '\'' +
                ", cantidad=" + cantidad +
                ", fecha=" + fecha +
                ", costo=" + costo +
                ", precio=" + precio +
                ", ordenPedido='" + ordenPedido + '\'' +
                '}';
    }
}
