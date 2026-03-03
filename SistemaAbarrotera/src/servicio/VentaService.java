package servicio;

import modelo.*;
import java.time.LocalDate;

public class VentaService {
    private Venta[] ventas;
    private int totalVentas;
    private int contadorId;

    public VentaService() {
        this.ventas = new Venta[100];
        this.totalVentas = 0;
        this.contadorId = 1;
    }

    public String generarIdVenta() {
        return "VTA" + String.format("%04d", contadorId++);
    }

    private void expandirArreglo() {
        Venta[] nuevo = new Venta[ventas.length * 2];
        for (int i = 0; i < totalVentas; i++) nuevo[i] = ventas[i];
        ventas = nuevo;
    }

    public void agregarVenta(Venta venta) {
        if (totalVentas >= ventas.length) expandirArreglo();
        ventas[totalVentas++] = venta;
    }

    public Venta[] obtenerVentas() {
        Venta[] resultado = new Venta[totalVentas];
        for (int i = 0; i < totalVentas; i++) resultado[i] = ventas[i];
        return resultado;
    }

    public Venta buscarPorId(String idVenta) {
        for (int i = 0; i < totalVentas; i++) {
            if (ventas[i].getIdVenta().equals(idVenta)) return ventas[i];
        }
        return null;
    }

    // --- Ventas por período ---

    public Venta[] obtenerVentasDelDia(LocalDate fecha) {
        int count = 0;
        for (int i = 0; i < totalVentas; i++) if (ventas[i].getFecha().equals(fecha)) count++;
        Venta[] resultado = new Venta[count];
        int idx = 0;
        for (int i = 0; i < totalVentas; i++) if (ventas[i].getFecha().equals(fecha)) resultado[idx++] = ventas[i];
        return resultado;
    }

    public Venta[] obtenerVentasPorPeriodo(LocalDate desde, LocalDate hasta) {
        int count = 0;
        for (int i = 0; i < totalVentas; i++) {
            LocalDate f = ventas[i].getFecha();
            if (!f.isBefore(desde) && !f.isAfter(hasta)) count++;
        }
        Venta[] resultado = new Venta[count];
        int idx = 0;
        for (int i = 0; i < totalVentas; i++) {
            LocalDate f = ventas[i].getFecha();
            if (!f.isBefore(desde) && !f.isAfter(hasta)) resultado[idx++] = ventas[i];
        }
        return resultado;
    }

    // --- Búsqueda ---

    public Venta[] buscarVentas(String termino) {
        String t = termino.toLowerCase();
        int count = 0;
        for (int i = 0; i < totalVentas; i++) if (coincide(ventas[i], t)) count++;
        Venta[] resultado = new Venta[count];
        int idx = 0;
        for (int i = 0; i < totalVentas; i++) if (coincide(ventas[i], t)) resultado[idx++] = ventas[i];
        return resultado;
    }

    private boolean coincide(Venta v, String t) {
        if (v.getIdVenta().toLowerCase().contains(t)) return true;
        if (v.getCliente().toLowerCase().contains(t)) return true;
        if (v.getMetodoPago().toLowerCase().contains(t)) return true;
        for (int i = 0; i < v.getTotalDetalles(); i++) {
            if (v.getDetalles()[i].getNombreProducto().toLowerCase().contains(t)) return true;
        }
        return false;
    }

    // --- Estadísticas ---

    public int getTotalVentas() { return totalVentas; }

    public double calcularTotalVentas() {
        double total = 0;
        for (int i = 0; i < totalVentas; i++) total += ventas[i].getTotal();
        return total;
    }

    public double calcularVentasDelDia(LocalDate fecha) {
        double total = 0;
        for (int i = 0; i < totalVentas; i++) {
            if (ventas[i].getFecha().equals(fecha)) total += ventas[i].getTotal();
        }
        return total;
    }

    public int getArticulosVendidosHoy(LocalDate fecha) {
        int total = 0;
        for (int i = 0; i < totalVentas; i++) {
            if (ventas[i].getFecha().equals(fecha)) total += ventas[i].getTotalArticulos();
        }
        return total;
    }

    // Top productos vendidos (retorna arrays paralelos: nombres y cantidades)
    public String[] getTopProductosNombres(int top) {
        // Contar productos vendidos
        String[] nombres = new String[totalVentas * 10];
        int[] cantidades = new int[totalVentas * 10];
        int totalItems = 0;

        for (int i = 0; i < totalVentas; i++) {
            DetalleVenta[] detalles = ventas[i].getDetalles();
            for (int j = 0; j < ventas[i].getTotalDetalles(); j++) {
                String nombre = detalles[j].getNombreProducto();
                boolean encontrado = false;
                for (int k = 0; k < totalItems; k++) {
                    if (nombres[k].equals(nombre)) {
                        cantidades[k] += detalles[j].getCantidad();
                        encontrado = true;
                        break;
                    }
                }
                if (!encontrado) {
                    nombres[totalItems] = nombre;
                    cantidades[totalItems] = detalles[j].getCantidad();
                    totalItems++;
                }
            }
        }

        // Ordenar por cantidad (burbuja simple)
        for (int i = 0; i < totalItems - 1; i++) {
            for (int j = 0; j < totalItems - i - 1; j++) {
                if (cantidades[j] < cantidades[j + 1]) {
                    int tmpC = cantidades[j]; cantidades[j] = cantidades[j + 1]; cantidades[j + 1] = tmpC;
                    String tmpN = nombres[j]; nombres[j] = nombres[j + 1]; nombres[j + 1] = tmpN;
                }
            }
        }

        int resultSize = Math.min(top, totalItems);
        String[] result = new String[resultSize];
        for (int i = 0; i < resultSize; i++) result[i] = nombres[i];
        return result;
    }

    public int[] getTopProductosCantidades(int top) {
        String[] nombres = new String[totalVentas * 10];
        int[] cantidades = new int[totalVentas * 10];
        int totalItems = 0;

        for (int i = 0; i < totalVentas; i++) {
            DetalleVenta[] detalles = ventas[i].getDetalles();
            for (int j = 0; j < ventas[i].getTotalDetalles(); j++) {
                String nombre = detalles[j].getNombreProducto();
                boolean encontrado = false;
                for (int k = 0; k < totalItems; k++) {
                    if (nombres[k].equals(nombre)) {
                        cantidades[k] += detalles[j].getCantidad();
                        encontrado = true;
                        break;
                    }
                }
                if (!encontrado) {
                    nombres[totalItems] = nombre;
                    cantidades[totalItems] = detalles[j].getCantidad();
                    totalItems++;
                }
            }
        }

        for (int i = 0; i < totalItems - 1; i++) {
            for (int j = 0; j < totalItems - i - 1; j++) {
                if (cantidades[j] < cantidades[j + 1]) {
                    int tmpC = cantidades[j]; cantidades[j] = cantidades[j + 1]; cantidades[j + 1] = tmpC;
                    String tmpN = nombres[j]; nombres[j] = nombres[j + 1]; nombres[j + 1] = tmpN;
                }
            }
        }

        int resultSize = Math.min(top, totalItems);
        int[] result = new int[resultSize];
        for (int i = 0; i < resultSize; i++) result[i] = cantidades[i];
        return result;
    }

    public Venta[] obtenerUltimasVentas(int cantidad) {
        int start = Math.max(0, totalVentas - cantidad);
        int size = totalVentas - start;
        Venta[] resultado = new Venta[size];
        // Más recientes primero
        for (int i = 0; i < size; i++) {
            resultado[i] = ventas[totalVentas - 1 - i];
        }
        return resultado;
    }
}
