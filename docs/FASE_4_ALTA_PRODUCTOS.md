# OMIS — Fase 4: Alta de Productos

**Fecha:** 04 de Mayo de 2026
**Responsable:** Andrés Cuevas García
**Estado:** ✅ Completada

---

## Objetivo

Implementar el registro de productos del giro mixto (escolar + alimentos) con formulario dinámico que muestra campos adicionales según el subtipo: Lámina Didáctica o Perecedero.

---

## Archivos Creados

### Capa Modelo

| Archivo | Campos | Relación |
|---------|--------|----------|
| `model/Producto.java` | idProducto, nombre, costoAdquisicion, precioVenta, margenUtilidad, stockActual, idCategoria, ubicacionFisica + nombreCategoria (auxiliar) | Entidad fuerte |
| `model/Lamina.java` | idProducto (PK/FK), idMarca, numeroSerie, tema, materia + nombreMarca (auxiliar) | Subentidad 1:1 de Producto |
| `model/Perecedero.java` | idProducto (PK/FK), refrigeracion, fechaCaducidad | Subentidad 1:1 de Producto |

---

### Capa DAO

#### `dao/ProductoDAO.java`

| Método | Descripción |
|--------|-------------|
| `findAll()` | JOIN con categoría para mostrar nombre en tabla |
| `buscarPorNombre(String)` | Búsqueda parcial con LIKE |
| `findById(int)` | Busca por ID con JOIN a categoría |
| `create(Producto)` | Retorna ID generado (para vincular subentidad) |
| `update(Producto)` | Actualiza nombre, categoría, ubicación |
| `delete(int)` | Elimina con CASCADE a subentidades |

#### `dao/LaminaDAO.java` y `dao/PerecederoDAO.java`

CRUD completo vinculado al `id_producto`. Lámina hace JOIN a `marca` para obtener nombre de marca.

---

### Capa Vista

#### `fxml/productos.fxml`

**Formulario dinámico con 3 niveles:**

1. **Campos base** (siempre visibles): Nombre, Categoría (ComboBox), Ubicación física
2. **Panel Lámina** (borde azul `#BBDEFB`): aparece al marcar "Es Lámina Didáctica"
   - Marca (ComboBox), Número de Serie, Tema, Materia
3. **Panel Perecedero** (borde naranja `#FFCC80`): aparece al marcar "Es Perecedero"
   - Refrigeración, Fecha de Caducidad (DatePicker)

**Exclusividad:** Los checkboxes son mutuamente excluyentes (un producto no puede ser lámina y perecedero a la vez).

**Tabla inferior** con columnas: ID, Nombre, Categoría, Stock, Costo, Precio Venta, Ubicación.

**Barra de búsqueda** con filtrado en tiempo real (`onKeyReleased`).

#### `controller/ProductoController.java`

Controlador con lógica de:
- Toggle agregar/editar con gestión de subentidades
- Al editar: carga datos del producto + detecta si tiene lámina o perecedero y llena los campos
- Al guardar en edición: actualiza/crea/elimina subentidades según los checkboxes
- Validaciones: nombre obligatorio, campos de lámina obligatorios si marcada

---

### Modificaciones a Archivos Existentes

| Archivo | Cambio |
|---------|--------|
| `dashboard.fxml` | Botón "📦 Inventario" conectado a `handleNavInventario` |
| `DashboardController.java` | Nuevo método `handleNavInventario()` → carga `productos.fxml` |

---

## Verificaciones

| Prueba | Resultado |
|--------|-----------|
| Compilación `mvn compile` | ✅ Sin errores |
| Navegar a Inventario desde sidebar | ✅ Vista cargada |
| Crear producto normal (sin subtipo) | ✅ |
| Crear lámina didáctica (panel azul aparece) | ✅ |
| Crear perecedero (panel naranja aparece) | ✅ |
| Exclusividad de checkboxes | ✅ Solo uno activo |
| Búsqueda en tiempo real | ✅ Filtra mientras se escribe |
| Editar producto con lámina (carga datos) | ✅ |
| Eliminar producto (CASCADE a subentidad) | ✅ |
| ComboBox de categorías y marcas poblados | ✅ |

---

## Siguiente: Fase 5 — Entrada de Mercancía

La siguiente fase implementará:
- Modelos: `EntradaMercancia.java`, `DetalleEntrada.java`
- DAOs con transacciones atómicas (cabecera + detalles + actualización de stock)
- `PrecioService.java` con fórmula placeholder
- Vista con tabla editable para agregar múltiples productos por entrada
