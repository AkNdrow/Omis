package com.omis.model;

/**
 * Modelo que representa una entrada de mercancía (cabecera).
 * Corresponde a la tabla 'entrada_mercancia' en la base de datos.
 */
public class EntradaMercancia {

    private int idEntrada;
    private String fechaRecepcion;
    private String observaciones;
    private int idProveedor;
    private int idUsuario;

    // Campos auxiliares para display
    private String nombreProveedor;
    private String nombreUsuario;

    public EntradaMercancia() {
    }

    public int getIdEntrada() { return idEntrada; }
    public void setIdEntrada(int idEntrada) { this.idEntrada = idEntrada; }

    public String getFechaRecepcion() { return fechaRecepcion; }
    public void setFechaRecepcion(String fechaRecepcion) { this.fechaRecepcion = fechaRecepcion; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public int getIdProveedor() { return idProveedor; }
    public void setIdProveedor(int idProveedor) { this.idProveedor = idProveedor; }

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }

    public String getNombreProveedor() { return nombreProveedor; }
    public void setNombreProveedor(String nombreProveedor) { this.nombreProveedor = nombreProveedor; }

    public String getNombreUsuario() { return nombreUsuario; }
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario; }
}
