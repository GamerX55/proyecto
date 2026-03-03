package modelo;

import java.time.LocalDate;

public class Cliente {
    private String idCliente;
    private String nombre;
    private String apellidos;
    private String telefono;
    private String direccion;
    private String colonia;
    private String rfc;
    private LocalDate fechaRegistro;
    private String notas;
    private boolean activo;

    public Cliente(String idCliente, String nombre, String apellidos, String telefono,
                   String direccion, String colonia, String rfc, LocalDate fechaRegistro,
                   String notas, boolean activo) {
        this.idCliente = idCliente;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.telefono = telefono;
        this.direccion = direccion;
        this.colonia = colonia;
        this.rfc = rfc;
        this.fechaRegistro = fechaRegistro;
        this.notas = notas;
        this.activo = activo;
    }

    public String getIdCliente() { return idCliente; }
    public String getNombre() { return nombre; }
    public String getApellidos() { return apellidos; }
    public String getNombreCompleto() { return nombre + " " + apellidos; }
    public String getTelefono() { return telefono; }
    public String getDireccion() { return direccion; }
    public String getColonia() { return colonia; }
    public String getRfc() { return rfc; }
    public LocalDate getFechaRegistro() { return fechaRegistro; }
    public String getNotas() { return notas; }
    public boolean isActivo() { return activo; }

    public void setIdCliente(String idCliente) { this.idCliente = idCliente; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public void setColonia(String colonia) { this.colonia = colonia; }
    public void setRfc(String rfc) { this.rfc = rfc; }
    public void setFechaRegistro(LocalDate fechaRegistro) { this.fechaRegistro = fechaRegistro; }
    public void setNotas(String notas) { this.notas = notas; }
    public void setActivo(boolean activo) { this.activo = activo; }

    @Override
    public String toString() {
        return idCliente + " - " + getNombreCompleto();
    }
}
