package com.omis.model;

/**
 * Modelo que representa una venta (cabecera).
 * Corresponde a la tabla 'venta' en la base de datos.
 */
public class Venta {

    private int idVenta;
    private String fechaHora;
    private double montoTotal;
    private int idUsuario;

    // Campo auxiliar para display
    private String nombreUsuario;

    public Venta() {
    }

    public int getIdVenta() { return idVenta; }
    public void setIdVenta(int idVenta) { this.idVenta = idVenta; }

    public String getFechaHora() { return fechaHora; }
    public void setFechaHora(String fechaHora) { this.fechaHora = fechaHora; }

    public double getMontoTotal() { return montoTotal; }
    public void setMontoTotal(double montoTotal) { this.montoTotal = montoTotal; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }
}
