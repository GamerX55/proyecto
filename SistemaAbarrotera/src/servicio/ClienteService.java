package servicio;

import modelo.*;

public class ClienteService {
    private Cliente[] clientes;
    private int totalClientes;
    private int contadorId;

    public ClienteService() {
        this.clientes = new Cliente[50];
        this.totalClientes = 0;
        this.contadorId = 1;
    }

    public String generarIdCliente() {
        return "CLI" + String.format("%04d", contadorId++);
    }

    private void expandirArreglo() {
        Cliente[] nuevo = new Cliente[clientes.length * 2];
        for (int i = 0; i < totalClientes; i++) {
            nuevo[i] = clientes[i];
        }
        clientes = nuevo;
    }

    public void agregarCliente(Cliente cliente) {
        if (totalClientes >= clientes.length) {
            expandirArreglo();
        }
        clientes[totalClientes++] = cliente;
    }

    public Cliente[] obtenerClientes() {
        Cliente[] resultado = new Cliente[totalClientes];
        for (int i = 0; i < totalClientes; i++) {
            resultado[i] = clientes[i];
        }
        return resultado;
    }

    public Cliente[] obtenerClientesActivos() {
        int count = 0;
        for (int i = 0; i < totalClientes; i++) {
            if (clientes[i].isActivo()) count++;
        }
        Cliente[] resultado = new Cliente[count];
        int idx = 0;
        for (int i = 0; i < totalClientes; i++) {
            if (clientes[i].isActivo()) {
                resultado[idx++] = clientes[i];
            }
        }
        return resultado;
    }

    public Cliente buscarPorId(String idCliente) {
        for (int i = 0; i < totalClientes; i++) {
            if (clientes[i].getIdCliente().equals(idCliente)) {
                return clientes[i];
            }
        }
        return null;
    }

    public boolean actualizarCliente(String idCliente, Cliente actualizado) {
        for (int i = 0; i < totalClientes; i++) {
            if (clientes[i].getIdCliente().equals(idCliente)) {
                clientes[i] = actualizado;
                return true;
            }
        }
        return false;
    }

    public Cliente[] buscarClientes(String termino) {
        String terminoLower = termino.toLowerCase();
        int count = 0;
        for (int i = 0; i < totalClientes; i++) {
            if (coincide(clientes[i], terminoLower)) count++;
        }
        Cliente[] resultado = new Cliente[count];
        int idx = 0;
        for (int i = 0; i < totalClientes; i++) {
            if (coincide(clientes[i], terminoLower)) {
                resultado[idx++] = clientes[i];
            }
        }
        return resultado;
    }

    private boolean coincide(Cliente c, String termino) {
        return c.getIdCliente().toLowerCase().contains(termino) ||
               c.getNombre().toLowerCase().contains(termino) ||
               c.getApellidos().toLowerCase().contains(termino) ||
               c.getTelefono().toLowerCase().contains(termino) ||
               c.getDireccion().toLowerCase().contains(termino) ||
               c.getColonia().toLowerCase().contains(termino) ||
               c.getRfc().toLowerCase().contains(termino);
    }

    public int getTotalClientes() {
        return totalClientes;
    }

    public int getTotalClientesActivos() {
        int count = 0;
        for (int i = 0; i < totalClientes; i++) {
            if (clientes[i].isActivo()) count++;
        }
        return count;
    }
}
