package com.omis.model;

/**
 * Modelo que representa una lámina didáctica.
 * Subentidad de Producto (relación 1:1).
 * Corresponde a la tabla 'lamina' en la base de datos.
 */
public class Lamina {

    private int idProducto; // PK y FK a producto
    private Integer idMarca;
    private String numeroSerie;
    private String tema;
    private String materia;

    // Campos auxiliares para mostrar en tabla
    private String nombreMarca;
    private String nombreProducto;
    private double precioVenta;
    private int stockActual;

    public Lamina() {
    }

    public Lamina(int idProducto, Integer idMarca, String numeroSerie, String tema, String materia) {
        this.idProducto = idProducto;
        this.idMarca = idMarca;
        this.numeroSerie = numeroSerie;
        this.tema = tema;
        this.materia = materia;
    }

    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }

    public Integer getIdMarca() { return idMarca; }
    public void setIdMarca(Integer idMarca) { this.idMarca = idMarca; }

    public String getNumeroSerie() { return numeroSerie; }
    public void setNumeroSerie(String numeroSerie) { this.numeroSerie = numeroSerie; }

    public String getTema() { return tema; }
    public void setTema(String tema) { this.tema = tema; }

    public String getMateria() { return materia; }
    public void setMateria(String materia) { this.materia = materia; }

    public String getNombreMarca() { return nombreMarca; }
    public void setNombreMarca(String nombreMarca) { this.nombreMarca = nombreMarca; }

    public String getNombreProducto() { return nombreProducto; }
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }

    public double getPrecioVenta() { return precioVenta; }
    public void setPrecioVenta(double precioVenta) { this.precioVenta = precioVenta; }

    public int getStockActual() { return stockActual; }
    public void setStockActual(int stockActual) { this.stockActual = stockActual; }
}
