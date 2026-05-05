# OMIS — Fase 5: Entrada de Mercancía

**Fecha:** 04 de Mayo de 2026
**Responsable:** Andrés Cuevas García
**Estado:** ✅ Completada

---

## Objetivo

Implementar el registro de entradas de mercancía de proveedores hacia el inventario. Esto incluye la creación de lotes con múltiples productos, cálculo de precios de venta en tiempo real (según costo y margen), y el registro en la base de datos mediante transacciones atómicas para actualizar el stock y los costos/precios simultáneamente.

---

## Archivos Creados

### Capa Modelo

| Archivo | Campos | Relación |
|---------|--------|----------|
| `model/EntradaMercancia.java` | idEntrada, fechaRecepcion, observaciones, idProveedor, idUsuario + nombreProveedor, nombreUsuario (auxiliares) | Entidad fuerte (cabecera) |
| `model/DetalleEntrada.java` | idDetalleEnt, idEntrada, idProducto, cantidadRecibida, costoUnitario, precioCalculado + nombreProducto, subtotal (auxiliares) | Entidad débil (detalle) |

---

### Capa DAO y Servicios

#### `dao/EntradaMercanciaDAO.java`
Maneja tanto la cabecera como los detalles de la entrada mediante **transacciones atómicas**.

| Método | Descripción |
|--------|-------------|
| `registrarEntrada(EntradaMercancia, List<DetalleEntrada>)` | 1. Inserta `entrada_mercancia`<br>2. Inserta múltiples `detalle_entrada`<br>3. Hace `UPDATE producto` (suma `stock_actual` y actualiza precios/costos)<br>Todo en un bloque `conn.setAutoCommit(false)` con `commit`/`rollback`. |
| `findAll()` | Obtiene el historial de entradas con JOIN a `proveedor` y `usuario` para mostrar nombres legibles. |
| `findDetalles(int)` | Recupera los renglones de una entrada específica (útil para auditoría). |

#### `service/PrecioService.java`
Lógica de cálculo de precios.
- `calcularPrecioVenta(costo, margen)`: Fórmula placeholder `costo * (1 + margen / 100)`. Lista para ser reemplazada cuando el cliente proporcione su fórmula definitiva.
- `redondearPrecio(precio)`: Redondeo de precios.

---

### Capa Vista

#### `fxml/entrada_mercancia.fxml`

Formulario dividido en 3 secciones principales:
1. **Cabecera**: Selección de proveedor y notas.
2. **Constructor de Detalles (Lote)**: Formulario inline para seleccionar producto, ingresar cantidad, costo unitario, margen. Muestra el **precio calculado en tiempo real**. Botón para agregar a la tabla temporal.
3. **Resumen y Registro**: Tabla `TableView` con los productos a ingresar, botón para remover renglones seleccionados, muestra del total en $, y botón de "Registrar Entrada".
4. **Historial**: Tabla inferior que muestra las entradas registradas anteriormente.

#### `controller/EntradaMercanciaController.java`

- Uso de `ObservableList<DetalleEntrada>` como "carrito" temporal para construir el lote antes de impactar la base de datos.
- Listeners en los TextFields (`txtCostoUnitario` y `txtMargen`) que invocan a `PrecioService` para actualizar la etiqueta de precio calculado dinámicamente.
- Al hacer clic en "Registrar Entrada", se extrae el ID del usuario activo (`SessionManager`) y se llama a `EntradaMercanciaDAO.registrarEntrada`.
- Recarga automática de ComboBoxes y el historial al completar la operación con éxito.

---

### Modificaciones a Archivos Existentes

| Archivo | Cambio |
|---------|--------|
| `dashboard.fxml` | Agregado el botón `btnNavEntradas` ("📥 Entradas") en el sidebar. |
| `DashboardController.java` | Restricción de visibilidad del botón para el rol Empleado. Nuevo método `handleNavEntradas` que carga `entrada_mercancia.fxml`. |

---

## Verificaciones

| Prueba | Resultado |
|--------|-----------|
| Compilación `mvn compile` | ✅ Sin errores |
| Navegación desde Dashboard | ✅ Vista cargada correctamente |
| Restricción de acceso | ✅ Botón "Entradas" oculto si el usuario es "Empleado" |
| Cálculo de precios | ✅ Tiempo real al teclear costo y margen |
| Construcción de lote | ✅ Agregar/Quitar productos a la tabla temporal |
| Registro atómico | ✅ Inserta cabecera y detalles en la BD |
| Actualización de stock | ✅ El inventario refleja el stock sumado y el nuevo precio/costo |

---

## Siguiente: Fase 6 — Punto de Venta (POS)

La siguiente fase implementará:
- Modelos: `Venta.java`, `DetalleVenta.java`
- DAO: `VentaDAO.java` con transacciones atómicas para venta y deducción de stock
- Vistas divididas por giro: Papelería (búsqueda rápida) y Láminas (filtrado avanzado)
- Carrito de compras con cálculo de total, subtotal, pago y cambio
