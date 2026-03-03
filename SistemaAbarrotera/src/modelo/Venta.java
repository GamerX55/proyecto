package modelo;

import java.time.LocalDate;

public class Venta {
    private String idVenta;
    private LocalDate fecha;
    private String hora;
    private DetalleVenta[] detalles;
    private int totalDetalles;
    private int totalArticulos;
    private double total;
    private String metodoPago;
    private String referenciaPago;
    private String cliente;

    public Venta(String idVenta, LocalDate fecha, String hora, DetalleVenta[] detalles, int totalDetalles,
                 int totalArticulos, double total, String metodoPago, String referenciaPago, String cliente) {
        this.idVenta = idVenta;
        this.fecha = fecha;
        this.hora = hora;
        this.detalles = detalles;
        this.totalDetalles = totalDetalles;
        this.totalArticulos = totalArticulos;
        this.total = total;
        this.metodoPago = metodoPago;
        this.referenciaPago = referenciaPago;
        this.cliente = cliente;
    }

    public String getIdVenta() { return idVenta; }
    public LocalDate getFecha() { return fecha; }
    public String getHora() { return hora; }
    public DetalleVenta[] getDetalles() { return detalles; }
    public int getTotalDetalles() { return totalDetalles; }
    public int getTotalArticulos() { return totalArticulos; }
    public double getTotal() { return total; }
    public String getMetodoPago() { return metodoPago; }
    public String getReferenciaPago() { return referenciaPago; }
    public String getCliente() { return cliente; }

    public void setIdVenta(String idVenta) { this.idVenta = idVenta; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public void setHora(String hora) { this.hora = hora; }

    @Override
    public String toString() {
        return idVenta + " - $" + String.format("%.2f", total) + " (" + fecha + ")";
    }
}
