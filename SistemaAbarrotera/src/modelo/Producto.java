package modelo;

public class Producto {
    private String idProducto;
    private String nombre;
    private String descripcion;
    private String categoria;
    private double precioCompra;
    private double precioVenta;
    private String idProveedor;
    private String nombreProveedor;
    private int stock;
    private int stockMinimo;
    private String unidadMedida;
    private boolean activo;

    public Producto(String idProducto, String nombre, String descripcion, String categoria,
                    double precioCompra, double precioVenta, String idProveedor, String nombreProveedor,
                    int stock, int stockMinimo, String unidadMedida, boolean activo) {
        this.idProducto = idProducto;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.categoria = categoria;
        this.precioCompra = precioCompra;
        this.precioVenta = precioVenta;
        this.idProveedor = idProveedor;
        this.nombreProveedor = nombreProveedor;
        this.stock = stock;
        this.stockMinimo = stockMinimo;
        this.unidadMedida = unidadMedida;
        this.activo = activo;
    }

    public String getIdProducto() { return idProducto; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public String getCategoria() { return categoria; }
    public double getPrecioCompra() { return precioCompra; }
    public double getPrecioVenta() { return precioVenta; }
    public String getIdProveedor() { return idProveedor; }
    public String getNombreProveedor() { return nombreProveedor; }
    public int getStock() { return stock; }
    public int getStockMinimo() { return stockMinimo; }
    public String getUnidadMedida() { return unidadMedida; }
    public boolean isActivo() { return activo; }

    public void setIdProducto(String idProducto) { this.idProducto = idProducto; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public void setPrecioCompra(double precioCompra) { this.precioCompra = precioCompra; }
    public void setPrecioVenta(double precioVenta) { this.precioVenta = precioVenta; }
    public void setIdProveedor(String idProveedor) { this.idProveedor = idProveedor; }
    public void setNombreProveedor(String nombreProveedor) { this.nombreProveedor = nombreProveedor; }
    public void setStock(int stock) { this.stock = stock; }
    public void setStockMinimo(int stockMinimo) { this.stockMinimo = stockMinimo; }
    public void setUnidadMedida(String unidadMedida) { this.unidadMedida = unidadMedida; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public boolean isBajoStock() { return stock <= stockMinimo; }
    public boolean isSinStock() { return stock <= 0; }

    @Override
    public String toString() {
        return idProducto + " - " + nombre;
    }
}
