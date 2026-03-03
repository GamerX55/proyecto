package servicio;

import modelo.*;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class CreditoService {
    private Credito[] creditos;
    private int totalCreditos;
    private int contadorId;

    public CreditoService() {
        this.creditos = new Credito[50];
        this.totalCreditos = 0;
        this.contadorId = 1;
    }

    public String generarIdCredito() {
        return "CRE" + String.format("%04d", contadorId++);
    }

    private void expandirArreglo() {
        Credito[] nuevo = new Credito[creditos.length * 2];
        for (int i = 0; i < totalCreditos; i++) {
            nuevo[i] = creditos[i];
        }
        creditos = nuevo;
    }

    public void agregarCredito(Credito credito) {
        if (totalCreditos >= creditos.length) {
            expandirArreglo();
        }
        creditos[totalCreditos++] = credito;
    }

    public Credito[] obtenerCreditos() {
        actualizarEstados();
        Credito[] resultado = new Credito[totalCreditos];
        for (int i = 0; i < totalCreditos; i++) {
            resultado[i] = creditos[i];
        }
        return resultado;
    }

    public Credito buscarPorId(String idCredito) {
        for (int i = 0; i < totalCreditos; i++) {
            if (creditos[i].getIdCredito().equals(idCredito)) {
                return creditos[i];
            }
        }
        return null;
    }

    public Credito[] obtenerCreditosPorCliente(String idCliente) {
        actualizarEstados();
        int count = 0;
        for (int i = 0; i < totalCreditos; i++) {
            if (creditos[i].getIdCliente().equals(idCliente)) count++;
        }
        Credito[] resultado = new Credito[count];
        int idx = 0;
        for (int i = 0; i < totalCreditos; i++) {
            if (creditos[i].getIdCliente().equals(idCliente)) {
                resultado[idx++] = creditos[i];
            }
        }
        return resultado;
    }

    public Credito obtenerCreditoActivoPorCliente(String idCliente) {
        actualizarEstados();
        for (int i = 0; i < totalCreditos; i++) {
            if (creditos[i].getIdCliente().equals(idCliente) &&
                !creditos[i].getEstado().equals(Credito.PAGADO)) {
                return creditos[i];
            }
        }
        return null;
    }

    public boolean registrarPago(String idCredito, double monto) {
        Credito credito = buscarPorId(idCredito);
        if (credito == null || monto <= 0) return false;

        double nuevoMontoPagado = credito.getMontoPagado() + monto;
        if (nuevoMontoPagado > credito.getMontoOtorgado()) {
            return false;
        }

        credito.setMontoPagado(nuevoMontoPagado);
        if (Math.abs(credito.getSaldoPendiente()) < 0.01) {
            credito.setEstado(Credito.PAGADO);
        }
        return true;
    }

    public boolean modificarMonto(String idCredito, double nuevoMonto) {
        Credito credito = buscarPorId(idCredito);
        if (credito == null || nuevoMonto < credito.getMontoPagado()) return false;

        credito.setMontoOtorgado(nuevoMonto);
        if (Math.abs(credito.getSaldoPendiente()) < 0.01) {
            credito.setEstado(Credito.PAGADO);
        } else {
            actualizarEstadoIndividual(credito);
        }
        return true;
    }

    public void actualizarEstados() {
        for (int i = 0; i < totalCreditos; i++) {
            if (!creditos[i].getEstado().equals(Credito.PAGADO)) {
                actualizarEstadoIndividual(creditos[i]);
            }
        }
    }

    private void actualizarEstadoIndividual(Credito credito) {
        LocalDate hoy = LocalDate.now();
        if (credito.getEstado().equals(Credito.PAGADO)) return;

        if (hoy.isAfter(credito.getFechaVencimiento())) {
            credito.setEstado(Credito.VENCIDO);
        } else {
            long diasRestantes = ChronoUnit.DAYS.between(hoy, credito.getFechaVencimiento());
            if (diasRestantes <= 7) {
                credito.setEstado(Credito.POR_VENCER);
            } else {
                credito.setEstado(Credito.ACTIVO);
            }
        }
    }

    public String getStatusColorCliente(String idCliente) {
        actualizarEstados();
        String peorEstado = "VERDE";
        for (int i = 0; i < totalCreditos; i++) {
            if (creditos[i].getIdCliente().equals(idCliente)) {
                String estado = creditos[i].getEstado();
                if (estado.equals(Credito.VENCIDO)) {
                    return "ROJO";
                } else if (estado.equals(Credito.POR_VENCER)) {
                    peorEstado = "AMARILLO";
                } else if (estado.equals(Credito.ACTIVO) && peorEstado.equals("VERDE")) {
                    peorEstado = "VERDE";
                }
            }
        }
        return peorEstado;
    }

    public Credito[] obtenerCreditosNoPagados() {
        actualizarEstados();
        int count = 0;
        for (int i = 0; i < totalCreditos; i++) {
            if (!creditos[i].getEstado().equals(Credito.PAGADO)) count++;
        }
        Credito[] resultado = new Credito[count];
        int idx = 0;
        for (int i = 0; i < totalCreditos; i++) {
            if (!creditos[i].getEstado().equals(Credito.PAGADO)) {
                resultado[idx++] = creditos[i];
            }
        }
        return resultado;
    }

    public Credito[] obtenerCreditosPorEstado(String estado) {
        actualizarEstados();
        int count = 0;
        for (int i = 0; i < totalCreditos; i++) {
            if (creditos[i].getEstado().equals(estado)) count++;
        }
        Credito[] resultado = new Credito[count];
        int idx = 0;
        for (int i = 0; i < totalCreditos; i++) {
            if (creditos[i].getEstado().equals(estado)) {
                resultado[idx++] = creditos[i];
            }
        }
        return resultado;
    }

    public int getTotalCreditos() { return totalCreditos; }

    public double getTotalCreditoOtorgado() {
        double total = 0;
        for (int i = 0; i < totalCreditos; i++) {
            total += creditos[i].getMontoOtorgado();
        }
        return total;
    }

    public double getTotalSaldoPendiente() {
        double total = 0;
        for (int i = 0; i < totalCreditos; i++) {
            if (!creditos[i].getEstado().equals(Credito.PAGADO)) {
                total += creditos[i].getSaldoPendiente();
            }
        }
        return total;
    }

    public double getTotalRecaudado() {
        double total = 0;
        for (int i = 0; i < totalCreditos; i++) {
            total += creditos[i].getMontoPagado();
        }
        return total;
    }

    public boolean clienteTieneCreditoActivo(String idCliente) {
        for (int i = 0; i < totalCreditos; i++) {
            if (creditos[i].getIdCliente().equals(idCliente) &&
                !creditos[i].getEstado().equals(Credito.PAGADO)) {
                return true;
            }
        }
        return false;
    }
}
