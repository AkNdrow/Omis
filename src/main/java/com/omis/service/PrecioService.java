package com.omis.service;

/**
 * Servicio de cálculo de precios de venta.
 * Fórmula placeholder — se actualizará cuando el cliente proporcione la fórmula real.
 */
public class PrecioService {

    /**
     * Calcula el precio de venta basado en costo y margen de utilidad.
     * Fórmula actual: precio = costo × (1 + margen / 100)
     *
     * Ejemplo: costo=$10, margen=30% → precio=$13.00
     *
     * @param costoAdquisicion Costo unitario del lote
     * @param margenUtilidad Porcentaje de margen (ej: 30 para 30%)
     * @return Precio de venta calculado
     */
    public static double calcularPrecioVenta(double costoAdquisicion, double margenUtilidad) {
        return costoAdquisicion * (1 + margenUtilidad / 100.0);
    }

    /**
     * Redondea el precio al peso más cercano.
     * Ejemplo: $13.47 → $13.50
     */
    public static double redondearPrecio(double precio) {
        return Math.round(precio * 2) / 2.0;
    }
}
