package modelo;

import java.time.LocalDate;

public class Credito {
    public static final String ACTIVO = "ACTIVO";
    public static final String POR_VENCER = "POR_VENCER";
    public static final String VENCIDO = "VENCIDO";
    public static final String PAGADO = "PAGADO";

    private String idCredito;
    private String idCliente;
    private String nombreCliente;
    private double montoOtorgado;
    private double montoPagado;
    private LocalDate fechaOtorgamiento;
    private LocalDate fechaVencimiento;
    private String estado;

    public Credito(String idCredito, String idCliente, String nombreCliente,
                   double montoOtorgado, double montoPagado,
                   LocalDate fechaOtorgamiento, LocalDate fechaVencimiento, String estado) {
        this.idCredito = idCredito;
        this.idCliente = idCliente;
        this.nombreCliente = nombreCliente;
        this.montoOtorgado = montoOtorgado;
        this.montoPagado = montoPagado;
        this.fechaOtorgamiento = fechaOtorgamiento;
        this.fechaVencimiento = fechaVencimiento;
        this.estado = estado;
    }

    public double getSaldoPendiente() {
        return montoOtorgado - montoPagado;
    }

    public String getIdCredito() { return idCredito; }
    public String getIdCliente() { return idCliente; }
    public String getNombreCliente() { return nombreCliente; }
    public double getMontoOtorgado() { return montoOtorgado; }
    public double getMontoPagado() { return montoPagado; }
    public LocalDate getFechaOtorgamiento() { return fechaOtorgamiento; }
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public String getEstado() { return estado; }

    public void setIdCredito(String idCredito) { this.idCredito = idCredito; }
    public void setIdCliente(String idCliente) { this.idCliente = idCliente; }
    public void setNombreCliente(String nombreCliente) { this.nombreCliente = nombreCliente; }
    public void setMontoOtorgado(double montoOtorgado) { this.montoOtorgado = montoOtorgado; }
    public void setMontoPagado(double montoPagado) { this.montoPagado = montoPagado; }
    public void setFechaOtorgamiento(LocalDate fechaOtorgamiento) { this.fechaOtorgamiento = fechaOtorgamiento; }
    public void setFechaVencimiento(LocalDate fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }
    public void setEstado(String estado) { this.estado = estado; }
}
