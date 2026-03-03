package servicio;

import modelo.*;

public class ProveedorService {
    private Proveedor[] proveedores;
    private int totalProveedores;
    private int contadorId;

    public ProveedorService() {
        this.proveedores = new Proveedor[50];
        this.totalProveedores = 0;
        this.contadorId = 1;
    }

    public String generarIdProveedor() {
        return "PRV" + String.format("%04d", contadorId++);
    }

    private void expandirArreglo() {
        Proveedor[] nuevo = new Proveedor[proveedores.length * 2];
        for (int i = 0; i < totalProveedores; i++) {
            nuevo[i] = proveedores[i];
        }
        proveedores = nuevo;
    }

    public void agregarProveedor(Proveedor proveedor) {
        if (totalProveedores >= proveedores.length) {
            expandirArreglo();
        }
        proveedores[totalProveedores++] = proveedor;
    }

    public Proveedor[] obtenerProveedores() {
        Proveedor[] resultado = new Proveedor[totalProveedores];
        for (int i = 0; i < totalProveedores; i++) {
            resultado[i] = proveedores[i];
        }
        return resultado;
    }

    public Proveedor[] obtenerProveedoresActivos() {
        int count = 0;
        for (int i = 0; i < totalProveedores; i++) {
            if (proveedores[i].isActivo()) count++;
        }
        Proveedor[] resultado = new Proveedor[count];
        int idx = 0;
        for (int i = 0; i < totalProveedores; i++) {
            if (proveedores[i].isActivo()) {
                resultado[idx++] = proveedores[i];
            }
        }
        return resultado;
    }

    public Proveedor buscarPorId(String idProveedor) {
        for (int i = 0; i < totalProveedores; i++) {
            if (proveedores[i].getIdProveedor().equals(idProveedor)) {
                return proveedores[i];
            }
        }
        return null;
    }

    public boolean actualizarProveedor(String idProveedor, Proveedor actualizado) {
        for (int i = 0; i < totalProveedores; i++) {
            if (proveedores[i].getIdProveedor().equals(idProveedor)) {
                proveedores[i] = actualizado;
                return true;
            }
        }
        return false;
    }

    public Proveedor[] buscarProveedores(String termino) {
        String terminoLower = termino.toLowerCase();
        int count = 0;
        for (int i = 0; i < totalProveedores; i++) {
            if (coincide(proveedores[i], terminoLower)) count++;
        }
        Proveedor[] resultado = new Proveedor[count];
        int idx = 0;
        for (int i = 0; i < totalProveedores; i++) {
            if (coincide(proveedores[i], terminoLower)) {
                resultado[idx++] = proveedores[i];
            }
        }
        return resultado;
    }

    private boolean coincide(Proveedor p, String termino) {
        return p.getIdProveedor().toLowerCase().contains(termino) ||
               p.getNombreEmpresa().toLowerCase().contains(termino) ||
               p.getContacto().toLowerCase().contains(termino) ||
               p.getTelefono().toLowerCase().contains(termino) ||
               p.getEmail().toLowerCase().contains(termino) ||
               p.getCiudad().toLowerCase().contains(termino) ||
               p.getRfc().toLowerCase().contains(termino);
    }

    public int getTotalProveedores() { return totalProveedores; }

    public int getTotalProveedoresActivos() {
        int count = 0;
        for (int i = 0; i < totalProveedores; i++) {
            if (proveedores[i].isActivo()) count++;
        }
        return count;
    }
}
