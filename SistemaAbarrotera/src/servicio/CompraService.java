package servicio;

import modelo.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CompraService {
    private List<Compra> compras;
    private int contadorId;

    public CompraService() {
        this.compras = new ArrayList<>();
        this.contadorId = 1;
    }

    public String generarIdCompra() {
        return "C" + String.format("%04d", contadorId++);
    }

    public void agregarCompra(Compra compra) {
        compras.add(compra);
    }

    public List<Compra> obtenerCompras() {
        return new ArrayList<>(compras);
    }

    public Compra buscarCompraPorId(String idCompra) {
        for (Compra compra : compras) {
            if (compra.getIdCompra().equals(idCompra)) {
                return compra;
            }
        }
        return null;
    }

    public List<Compra> buscarComprasPorProducto(String idProducto) {
        List<Compra> resultado = new ArrayList<>();
        for (Compra compra : compras) {
            if (compra.getIdProducto().equals(idProducto)) {
                resultado.add(compra);
            }
        }
        return resultado;
    }

    public List<Compra> buscarComprasPorProveedor(String proveedor) {
        List<Compra> resultado = new ArrayList<>();
        for (Compra compra : compras) {
            if (compra.getProveedor().toLowerCase().contains(proveedor.toLowerCase())) {
                resultado.add(compra);
            }
        }
        return resultado;
    }

    public int getTotalCompras() {
        return compras.size();
    }

    public double calcularTotalCostos() {
        return compras.stream().mapToDouble(c -> c.getCosto() * c.getCantidad()).sum();
    }

    public boolean actualizarCompra(String idCompra, Compra compraActualizada) {
        for (int i = 0; i < compras.size(); i++) {
            if (compras.get(i).getIdCompra().equals(idCompra)) {
                compras.set(i, compraActualizada);
                return true;
            }
        }
        return false;
    }

    public boolean eliminarCompra(String idCompra) {
        return compras.removeIf(c -> c.getIdCompra().equals(idCompra));
    }

    public double calcularTotalPrecios() {
        return compras.stream().mapToDouble(c -> c.getPrecio() * c.getCantidad()).sum();
    }

    public List<String> obtenerProveedoresUnicos() {
        return compras.stream()
                .map(Compra::getProveedor)
                .distinct()
                .collect(Collectors.toList());
    }

    public Map<String, Integer> getComprasPorProveedor() {
        Map<String, Integer> mapa = new HashMap<>();
        for (Compra c : compras) {
            mapa.merge(c.getProveedor(), c.getCantidad(), Integer::sum);
        }
        return mapa;
    }

    public Map<String, Double> getCostosPorProveedor() {
        Map<String, Double> mapa = new HashMap<>();
        for (Compra c : compras) {
            mapa.merge(c.getProveedor(), c.getCosto() * c.getCantidad(), Double::sum);
        }
        return mapa;
    }

    public List<Compra> buscarCompras(String termino) {
        String terminoLower = termino.toLowerCase();
        List<Compra> resultado = new ArrayList<>();
        for (Compra c : compras) {
            if (c.getIdCompra().toLowerCase().contains(terminoLower) ||
                c.getIdProducto().toLowerCase().contains(terminoLower) ||
                c.getNombreProducto().toLowerCase().contains(terminoLower) ||
                c.getProveedor().toLowerCase().contains(terminoLower) ||
                c.getEmpresa().toLowerCase().contains(terminoLower) ||
                c.getOrdenPedido().toLowerCase().contains(terminoLower)) {
                resultado.add(c);
            }
        }
        return resultado;
    }
}
