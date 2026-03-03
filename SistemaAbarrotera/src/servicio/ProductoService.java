package servicio;

import modelo.*;

public class ProductoService {
    private Producto[] productos;
    private int totalProductos;
    private int contadorId;

    public ProductoService() {
        this.productos = new Producto[100];
        this.totalProductos = 0;
        this.contadorId = 1;
    }

    public String generarIdProducto() {
        return "PROD" + String.format("%04d", contadorId++);
    }

    private void expandirArreglo() {
        Producto[] nuevo = new Producto[productos.length * 2];
        for (int i = 0; i < totalProductos; i++) {
            nuevo[i] = productos[i];
        }
        productos = nuevo;
    }

    public void agregarProducto(Producto producto) {
        if (totalProductos >= productos.length) expandirArreglo();
        productos[totalProductos++] = producto;
    }

    public Producto[] obtenerProductos() {
        Producto[] resultado = new Producto[totalProductos];
        for (int i = 0; i < totalProductos; i++) resultado[i] = productos[i];
        return resultado;
    }

    public Producto[] obtenerProductosActivos() {
        int count = 0;
        for (int i = 0; i < totalProductos; i++) if (productos[i].isActivo()) count++;
        Producto[] resultado = new Producto[count];
        int idx = 0;
        for (int i = 0; i < totalProductos; i++) if (productos[i].isActivo()) resultado[idx++] = productos[i];
        return resultado;
    }

    public Producto buscarPorId(String idProducto) {
        for (int i = 0; i < totalProductos; i++) {
            if (productos[i].getIdProducto().equals(idProducto)) return productos[i];
        }
        return null;
    }

    public boolean actualizarProducto(String idProducto, Producto actualizado) {
        for (int i = 0; i < totalProductos; i++) {
            if (productos[i].getIdProducto().equals(idProducto)) {
                productos[i] = actualizado;
                return true;
            }
        }
        return false;
    }

    // --- Gestión de Stock ---

    public boolean aumentarStock(String idProducto, int cantidad) {
        Producto p = buscarPorId(idProducto);
        if (p != null) {
            p.setStock(p.getStock() + cantidad);
            return true;
        }
        return false;
    }

    public boolean reducirStock(String idProducto, int cantidad) {
        Producto p = buscarPorId(idProducto);
        if (p != null && p.getStock() >= cantidad) {
            p.setStock(p.getStock() - cantidad);
            return true;
        }
        return false;
    }

    public boolean ajustarStock(String idProducto, int nuevoStock) {
        Producto p = buscarPorId(idProducto);
        if (p != null) {
            p.setStock(nuevoStock);
            return true;
        }
        return false;
    }

    public Producto[] obtenerProductosBajoStock() {
        int count = 0;
        for (int i = 0; i < totalProductos; i++) {
            if (productos[i].isActivo() && productos[i].isBajoStock()) count++;
        }
        Producto[] resultado = new Producto[count];
        int idx = 0;
        for (int i = 0; i < totalProductos; i++) {
            if (productos[i].isActivo() && productos[i].isBajoStock()) resultado[idx++] = productos[i];
        }
        return resultado;
    }

    public Producto[] obtenerProductosSinStock() {
        int count = 0;
        for (int i = 0; i < totalProductos; i++) {
            if (productos[i].isActivo() && productos[i].isSinStock()) count++;
        }
        Producto[] resultado = new Producto[count];
        int idx = 0;
        for (int i = 0; i < totalProductos; i++) {
            if (productos[i].isActivo() && productos[i].isSinStock()) resultado[idx++] = productos[i];
        }
        return resultado;
    }

    // --- Búsqueda ---

    public Producto[] buscarProductos(String termino) {
        String t = termino.toLowerCase();
        int count = 0;
        for (int i = 0; i < totalProductos; i++) if (coincide(productos[i], t)) count++;
        Producto[] resultado = new Producto[count];
        int idx = 0;
        for (int i = 0; i < totalProductos; i++) if (coincide(productos[i], t)) resultado[idx++] = productos[i];
        return resultado;
    }

    public Producto[] buscarPorCategoria(String categoria) {
        int count = 0;
        for (int i = 0; i < totalProductos; i++) {
            if (productos[i].getCategoria().equalsIgnoreCase(categoria)) count++;
        }
        Producto[] resultado = new Producto[count];
        int idx = 0;
        for (int i = 0; i < totalProductos; i++) {
            if (productos[i].getCategoria().equalsIgnoreCase(categoria)) resultado[idx++] = productos[i];
        }
        return resultado;
    }

    public String[] obtenerCategorias() {
        String[] temp = new String[totalProductos];
        int count = 0;
        for (int i = 0; i < totalProductos; i++) {
            String cat = productos[i].getCategoria();
            boolean existe = false;
            for (int j = 0; j < count; j++) {
                if (temp[j].equalsIgnoreCase(cat)) { existe = true; break; }
            }
            if (!existe) temp[count++] = cat;
        }
        String[] resultado = new String[count];
        for (int i = 0; i < count; i++) resultado[i] = temp[i];
        return resultado;
    }

    private boolean coincide(Producto p, String termino) {
        return p.getIdProducto().toLowerCase().contains(termino) ||
               p.getNombre().toLowerCase().contains(termino) ||
               p.getDescripcion().toLowerCase().contains(termino) ||
               p.getCategoria().toLowerCase().contains(termino) ||
               p.getNombreProveedor().toLowerCase().contains(termino);
    }

    // --- Estadísticas ---

    public int getTotalProductos() { return totalProductos; }

    public int getTotalProductosActivos() {
        int count = 0;
        for (int i = 0; i < totalProductos; i++) if (productos[i].isActivo()) count++;
        return count;
    }

    public int getTotalStockGeneral() {
        int total = 0;
        for (int i = 0; i < totalProductos; i++) total += productos[i].getStock();
        return total;
    }

    public double getValorInventario() {
        double total = 0;
        for (int i = 0; i < totalProductos; i++) {
            total += productos[i].getStock() * productos[i].getPrecioCompra();
        }
        return total;
    }

    public double getValorInventarioVenta() {
        double total = 0;
        for (int i = 0; i < totalProductos; i++) {
            total += productos[i].getStock() * productos[i].getPrecioVenta();
        }
        return total;
    }
}
