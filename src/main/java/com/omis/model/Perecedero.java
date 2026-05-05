package com.omis.model;

/**
 * Modelo que representa un producto perecedero.
 * Subentidad de Producto (relación 1:1).
 * Corresponde a la tabla 'perecedero' en la base de datos.
 */
public class Perecedero {

    private int idProducto; // PK y FK a producto
    private String refrigeracion;
    private String fechaCaducidad; // Almacenado como String (formato DATE de SQLite)

    public Perecedero() {
    }

    public Perecedero(int idProducto, String refrigeracion, String fechaCaducidad) {
        this.idProducto = idProducto;
        this.refrigeracion = refrigeracion;
        this.fechaCaducidad = fechaCaducidad;
    }

    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }

    public String getRefrigeracion() { return refrigeracion; }
    public void setRefrigeracion(String refrigeracion) { this.refrigeracion = refrigeracion; }

    public String getFechaCaducidad() { return fechaCaducidad; }
    public void setFechaCaducidad(String fechaCaducidad) { this.fechaCaducidad = fechaCaducidad; }
}
