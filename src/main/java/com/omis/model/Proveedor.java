package com.omis.model;

/**
 * Modelo que representa un proveedor de mercancía.
 * Corresponde a la tabla 'proveedor' en la base de datos.
 */
public class Proveedor {

    private int idProveedor;
    private String nombreEmpresa;
    private String contacto;
    private String frecuenciaEntrega;

    public Proveedor() {
    }

    public Proveedor(int idProveedor, String nombreEmpresa, String contacto, String frecuenciaEntrega) {
        this.idProveedor = idProveedor;
        this.nombreEmpresa = nombreEmpresa;
        this.contacto = contacto;
        this.frecuenciaEntrega = frecuenciaEntrega;
    }

    public int getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(int idProveedor) {
        this.idProveedor = idProveedor;
    }

    public String getNombreEmpresa() {
        return nombreEmpresa;
    }

    public void setNombreEmpresa(String nombreEmpresa) {
        this.nombreEmpresa = nombreEmpresa;
    }

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    public String getFrecuenciaEntrega() {
        return frecuenciaEntrega;
    }

    public void setFrecuenciaEntrega(String frecuenciaEntrega) {
        this.frecuenciaEntrega = frecuenciaEntrega;
    }

    @Override
    public String toString() {
        return nombreEmpresa;
    }
}
