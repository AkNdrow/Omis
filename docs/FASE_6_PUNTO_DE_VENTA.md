# OMIS — Fase 6: Punto de Venta (POS)

**Fecha:** 04 de Mayo de 2026
**Responsable:** Andrés Cuevas García
**Estado:** ✅ Completada

---

## Objetivo

Implementar el módulo principal de ventas al mostrador de la Papelería OMIS. Permitir búsquedas rápidas para artículos en general y búsquedas avanzadas para Láminas Didácticas. Gestionar un carrito de compras, cálculo en tiempo real de totales y cambio, y la posterior deducción automática del stock en el inventario de manera atómica.

---

## Archivos Creados

### Capa Modelo

| Archivo | Campos | Descripción |
|---------|--------|-------------|
| `model/Venta.java` | idVenta, fechaHora, montoTotal, idUsuario | Cabecera del ticket de venta |
| `model/DetalleVenta.java` | idDetalle, idVenta, idProducto, cantidad, precioUnitario | Cada renglón del carrito. Incorpora un método `getSubtotal()` que multiplica cantidad por precio. |

### Modificaciones en Modelos Previos
- **`Lamina.java`**: Se añadieron los campos auxiliares `nombreProducto`, `precioVenta`, y `stockActual` para que la búsqueda avanzada retorne datos completos al POS listos para agregar al carrito.

---

### Capa DAO

| DAO | Modificación |
|-----|--------------|
| `VentaDAO.java` | Implementa `registrarVenta(Venta, List<DetalleVenta>)` mediante una **transacción atómica** (`conn.setAutoCommit(false)`). Inserta la venta, los detalles y actualiza `stock_actual = stock_actual - cantidad` en la tabla `producto`. Todo es devuelto mediante un `rollback` si falla alguna inserción. |
| `LaminaDAO.java` | Añadido el método `buscarAvanzado` que hace un JOIN con `producto` y `marca`, permitiendo buscar láminas simultáneamente por serie, materia y tema/nombre. |

---

### Capa Vista y Controladores

#### 1. Módulo Principal: `venta.fxml` + `VentaController.java`
- Interfaz dividida en dos paneles:
  - **Izquierda**: Barra de búsqueda en tiempo real (por nombre del producto) y tabla de resultados con stock en vivo. Botón inferior para abrir la búsqueda especializada de láminas.
  - **Derecha y Abajo**: Carrito de compras (`TableView<DetalleVenta>`) y panel de cobro.
- **Validaciones**: Comprobación estricta de stock al intentar agregar un producto al carrito, impidiendo la venta en negativo.
- **Gestión de Cobro**: Campo de "Pago en efectivo" con cálculo de cambio en tiempo real y coloreado en rojo si el monto es insuficiente.

#### 2. Pop-up de Láminas: `laminas.fxml` + `LaminaController.java`
- Ventana modal (bloquea la interfaz principal) dedicada a la búsqueda compleja de láminas.
- Contiene tres campos de texto que aplican el filtro a medida que el usuario escribe: *Tema/Palabra Clave*, *Materia* y *Serie*.
- Selecciona el objeto completo y, al hacer click en "Agregar al Carrito", lo transfiere al `VentaController` para integrarlo de forma transparente junto al resto de productos de la papelería.

#### 3. Integración en `dashboard.fxml`
- Añadido un botón "🛒 Punto de Venta" en el panel lateral, conectado a `handleNavPuntoVenta` del `DashboardController`. Disponible para todos los roles (Jefe y Empleado).

---

## Verificaciones Realizadas

| Prueba | Resultado |
|--------|-----------|
| Compilación `mvn compile` | ✅ Sin errores |
| Carga de la vista desde el Dashboard | ✅ |
| Búsqueda rápida de productos | ✅ Búsqueda funcional al teclear |
| Validación de stock insuficiente | ✅ Bloquea agregar más de lo disponible |
| Integración pop-up láminas didácticas | ✅ Devuelve la lámina al carrito con serie incluida en nombre |
| Cálculo de Totales y Cambio | ✅ En tiempo real, previene cobrar si no alcanza |
| Venta y Descuento de Stock | ✅ Transacción atómica descuenta inventario |

---

## Siguiente: Fase 7 — Reportes PDF

La siguiente fase implementará:
- Generación de Tickets de Venta (Notas de remisión).
- Generación de reportes de venta consolidados (Diarios, Semanales, Mensuales) usando Apache PDFBox.
- Hook de cierre de caja / aplicación para generar los PDFs de manera automática en la estructura jerárquica de carpetas especificada.
