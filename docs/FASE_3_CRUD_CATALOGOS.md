# OMIS — Fase 3: CRUD Catálogos

**Fecha:** 04 de Mayo de 2026
**Responsable:** Andrés Cuevas García
**Estado:** ✅ Completada

---

## Objetivo

Implementar las pantallas de gestión de los catálogos base del sistema: Categorías, Marcas y Proveedores. Estos catálogos son dependencias previas al módulo de Productos (Fase 4).

---

## Archivos Creados

### Capa Modelo

| Archivo | Campos | Descripción |
|---------|--------|-------------|
| `model/Categoria.java` | idCategoria, nombreCategoria | Clasificación del giro mixto (Escolar/Alimento) |
| `model/Marca.java` | idMarca, nombreMarca | Marcas de láminas didácticas |
| `model/Proveedor.java` | idProveedor, nombreEmpresa, contacto, frecuenciaEntrega | Empresas proveedoras de mercancía |

Todos implementan `toString()` que retorna el nombre principal (usado en ComboBox de fases posteriores).

---

### Capa DAO

Cada DAO implementa 4 operaciones CRUD con `PreparedStatement`:

| DAO | Métodos |
|-----|---------|
| `CategoriaDAO` | `findAll()`, `create()`, `update()`, `delete()` |
| `MarcaDAO` | `findAll()`, `create()`, `update()`, `delete()` |
| `ProveedorDAO` | `findAll()`, `create()`, `update()`, `delete()` |

---

### Capa Vista

#### `fxml/catalogos.fxml`

Vista con **TabPane** y 3 pestañas, cada una con:
- Formulario inline para agregar/editar (campos + botones)
- `TableView` con columnas configuradas vía `PropertyValueFactory`
- Botones "Editar" y "Eliminar" bajo la tabla

| Pestaña | Campos del formulario | Columnas de tabla |
|---------|----------------------|-------------------|
| Categorías | Nombre | ID, Nombre de Categoría |
| Marcas | Nombre | ID, Nombre de Marca |
| Proveedores | Empresa, Contacto, Entregas | ID, Empresa, Contacto, Frecuencia de Entrega |

#### `controller/CatalogosController.java`

Controlador unificado que gestiona las 3 pestañas con patrón **agregar/editar toggle**:

- **Modo Agregar**: botón muestra "Agregar", crea registro nuevo
- **Modo Editar**: al seleccionar fila y click "Editar", el formulario se llena con los datos, botón cambia a "Actualizar", aparece botón "Cancelar"
- **Eliminar**: diálogo de confirmación antes de borrar
- Los mensajes de error y avisos usan `Alert` nativo de JavaFX

---

### Modificaciones a Archivos Existentes

| Archivo | Cambio |
|---------|--------|
| `dashboard.fxml` | Centro cambiado de `VBox` a `StackPane` (`contentPane`) para carga dinámica de módulos. Botón "Catálogos" conectado a `handleNavCatalogos` |
| `DashboardController.java` | Nuevo método `cargarContenido(String fxmlPath)` que carga FXMLs en el `contentPane`. Navegación funcional a Catálogos y Panel de Control. Botones Catálogos e Inventario ocultos para rol Empleado |

#### Patrón de navegación implementado:

```
Dashboard (BorderPane)
├── left: Sidebar (VBox con botones)
└── center: StackPane (contentPane)
            ├── [default] VBox con mensaje de bienvenida
            ├── [catálogos] catalogos.fxml ← cargado dinámicamente
            ├── [inventario] (Fase 4)
            └── ...
```

---

## Verificaciones

| Prueba | Resultado |
|--------|-----------|
| Compilación `mvn compile` | ✅ Sin errores |
| Login → Dashboard → click "Catálogos" | ✅ Vista con TabPane cargada |
| Categorías: ver Escolar y Alimento precargadas | ✅ |
| Categorías: agregar nueva | ✅ |
| Categorías: editar existente (toggle a "Actualizar") | ✅ |
| Categorías: eliminar con confirmación | ✅ |
| Marcas: agregar, editar, eliminar | ✅ |
| Proveedores: agregar con 3 campos | ✅ |
| Proveedores: editar y eliminar | ✅ |
| "Panel de Control" regresa al inicio | ✅ |

---

## Siguiente: Fase 4 — Alta de Productos

La siguiente fase implementará:
- Modelos: `Producto.java`, `Lamina.java`, `Perecedero.java`
- DAOs: `ProductoDAO.java`, `LaminaDAO.java`
- Vista `producto.fxml` con formulario dinámico (campos extra según categoría/tipo)
- Búsqueda y listado de productos existentes
