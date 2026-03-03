package modelo;

import java.time.LocalDate;

public class Proveedor {
    private String idProveedor;
    private String nombreEmpresa;
    private String contacto;
    private String telefono;
    private String email;
    private String direccion;
    private String ciudad;
    private String rfc;
    private LocalDate fechaRegistro;
    private String notas;
    private boolean activo;

    public Proveedor(String idProveedor, String nombreEmpresa, String contacto, String telefono,
                     String email, String direccion, String ciudad, String rfc,
                     LocalDate fechaRegistro, String notas, boolean activo) {
        this.idProveedor = idProveedor;
        this.nombreEmpresa = nombreEmpresa;
        this.contacto = contacto;
        this.telefono = telefono;
        this.email = email;
        this.direccion = direccion;
        this.ciudad = ciudad;
        this.rfc = rfc;
        this.fechaRegistro = fechaRegistro;
        this.notas = notas;
        this.activo = activo;
    }

    public String getIdProveedor() { return idProveedor; }
    public String getNombreEmpresa() { return nombreEmpresa; }
    public String getContacto() { return contacto; }
    public String getTelefono() { return telefono; }
    public String getEmail() { return email; }
    public String getDireccion() { return direccion; }
    public String getCiudad() { return ciudad; }
    public String getRfc() { return rfc; }
    public LocalDate getFechaRegistro() { return fechaRegistro; }
    public String getNotas() { return notas; }
    public boolean isActivo() { return activo; }

    public void setIdProveedor(String idProveedor) { this.idProveedor = idProveedor; }
    public void setNombreEmpresa(String nombreEmpresa) { this.nombreEmpresa = nombreEmpresa; }
    public void setContacto(String contacto) { this.contacto = contacto; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public void setEmail(String email) { this.email = email; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }
    public void setRfc(String rfc) { this.rfc = rfc; }
    public void setFechaRegistro(LocalDate fechaRegistro) { this.fechaRegistro = fechaRegistro; }
    public void setNotas(String notas) { this.notas = notas; }
    public void setActivo(boolean activo) { this.activo = activo; }

    @Override
    public String toString() {
        return idProveedor + " - " + nombreEmpresa;
    }
}
