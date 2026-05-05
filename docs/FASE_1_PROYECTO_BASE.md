# OMIS — Fase 1: Proyecto Base + Base de Datos

**Fecha:** 04 de Mayo de 2026
**Responsable:** Andrés Cuevas García
**Estado:** ✅ Completada

---

## Objetivo

Crear el esqueleto del proyecto Maven con JavaFX, establecer la conexión a la base de datos SQLite y verificar que las 11 tablas del modelo E/R se crean correctamente al iniciar la aplicación.

---

## Entorno Configurado

| Componente | Versión | Detalle |
|------------|---------|---------|
| Java JDK | 21.0.11 LTS | Eclipse Adoptium Temurin |
| Maven | 3.9.15 | Apache Maven |
| Sistema Operativo | Windows 11 | amd64 |
| Locale | es_MX | UTF-8 |

---

## Estructura del Proyecto

```
Omis/
├── pom.xml                                          # Configuración Maven
├── .gitignore                                       # Exclusiones de Git
├── CAMBIO_TECNOLOGICO.md                            # Justificación de cambios al SRS
├── Ing. Software - Proyecto Omis.pdf                # Documento SRS original
├── README.md
└── src/
    └── main/
        ├── java/
        │   ├── module-info.java                     # Descriptor de módulo Java
        │   └── com/
        │       └── omis/
        │           ├── Main.java                    # Punto de entrada JavaFX
        │           ├── config/
        │           │   └── DatabaseManager.java     # Conexión SQLite (Singleton)
        │           ├── controller/
        │           │   └── package-info.java         # (placeholder - Fase 2+)
        │           └── model/
        │               └── package-info.java         # (placeholder - Fase 2+)
        └── resources/
            ├── css/
            │   └── styles.css                       # Estilos globales JavaFX
            └── sql/
                └── schema.sql                       # DDL de las 11 tablas
```

---

## Archivos Creados

### 1. `pom.xml` — Configuración Maven

Define el proyecto `com.omis:omis-app:1.0.0` con las siguientes dependencias:

| Dependencia | Artifact | Versión | Propósito |
|-------------|----------|---------|----------|
| JavaFX Controls | `org.openjfx:javafx-controls` | 21.0.5 | Componentes de interfaz (botones, tablas, formularios) |
| JavaFX FXML | `org.openjfx:javafx-fxml` | 21.0.5 | Carga de vistas declarativas desde archivos `.fxml` |
| JavaFX Graphics | `org.openjfx:javafx-graphics` | 21.0.5 | Motor de renderizado gráfico |
| SQLite JDBC | `org.xerial:sqlite-jdbc` | 3.45.3.0 | Driver JDBC para base de datos SQLite |
| jBCrypt | `org.mindrot:jbcrypt` | 0.4 | Hashing seguro de contraseñas |
| Apache PDFBox | `org.apache.pdfbox:pdfbox` | 3.0.4 | Generación de reportes en PDF |
| JUnit 5 | `org.junit.jupiter:junit-jupiter` | 5.10.2 | Framework de pruebas unitarias |

**Plugins configurados:**

| Plugin | Propósito |
|--------|----------|
| `maven-compiler-plugin` | Compilación con Java 21 |
| `javafx-maven-plugin` | Ejecución de la app con `mvn javafx:run` |
| `maven-shade-plugin` | Empaquetado en JAR ejecutable con todas las dependencias |

---

### 2. `module-info.java` — Descriptor de Módulo

```java
module com.omis {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.sql;
    requires org.xerial.sqlitejdbc;

    opens com.omis to javafx.fxml;
    opens com.omis.config to javafx.fxml;

    exports com.omis;
    exports com.omis.config;
}
```

> **Nota de escalabilidad:** Las directivas `opens` y `exports` para los paquetes `controller`, `model`, `dao`, `service` y `util` se agregarán conforme se creen las clases en las fases siguientes. Esto evita errores de compilación por paquetes vacíos.

---

### 3. `Main.java` — Punto de Entrada

Clase principal que extiende `javafx.application.Application`. Gestiona el ciclo de vida completo:

| Método | Momento | Función |
|--------|---------|---------|
| `init()` | Antes de mostrar la ventana | Inicializa la base de datos SQLite (crea tablas si no existen) |
| `start(Stage)` | Al mostrar la ventana | Configura la escena (1024×768), carga CSS global, muestra vista inicial |
| `stop()` | Al cerrar la app | Cierra la conexión a la base de datos como último recurso |

**Hook de cierre (`setOnCloseRequest`):** Preparado para la Fase 7, donde se invocará `ReporteService.onAppClose()` para generar el reporte diario y, si corresponde, el reporte mensual antes de cerrar la aplicación.

**Configuración de ventana:**
- Título: `"OMIS - Papelería Omis"`
- Tamaño inicial: 1024 × 768 px
- Tamaño mínimo: 900 × 600 px

---

### 4. `DatabaseManager.java` — Conexión SQLite

Implementa el patrón **Singleton** para garantizar una única conexión a la base de datos durante toda la ejecución.

**Características:**
- **Ruta del archivo:** `./omis_data.db` (junto al ejecutable)
- **Foreign Keys habilitadas:** SQLite las tiene deshabilitadas por defecto. Se activan con `PRAGMA foreign_keys = ON` en cada conexión.
- **Inicialización automática:** Lee `schema.sql` desde los resources y ejecuta cada sentencia SQL. Usa un parser línea por línea que acumula sentencias multi-línea y ejecuta al encontrar `;` al final de una línea, evitando conflictos con caracteres especiales dentro de strings (como hashes BCrypt).
- **Cierre limpio:** Método `closeConnection()` invocado tanto en el hook de cierre como en `stop()`.

---

### 5. `schema.sql` — Definición de Base de Datos

Contiene el DDL completo adaptado del modelo E/R del documento SRS, con las siguientes adaptaciones para SQLite:

- Los tipos `ENUM` de MySQL se reemplazan con restricciones `CHECK`.
- Los `AUTO_INCREMENT` de MySQL se reemplazan con `AUTOINCREMENT` de SQLite.
- Se usan funciones SQLite para valores por defecto: `datetime('now', 'localtime')`.
- Se agregan `ON DELETE CASCADE` en las subentidades (lámina, perecedero) y detalles.

**Tablas creadas (11):**

| # | Tabla | Tipo | Descripción |
|---|-------|------|-------------|
| 1 | `categoria` | Catálogo | Clasificación: Escolar / Alimento |
| 2 | `marca` | Catálogo | Marcas de láminas didácticas |
| 3 | `proveedor` | Entidad fuerte | Empresas que surten mercancía |
| 4 | `usuario` | Entidad fuerte | Personal con roles Jefe/Empleado |
| 5 | `producto` | Entidad fuerte | Artículos del giro mixto |
| 6 | `lamina` | Subentidad (1:1) | Extensión de producto para láminas didácticas |
| 7 | `perecedero` | Subentidad (1:1) | Extensión de producto para alimentos |
| 8 | `entrada_mercancia` | Entidad | Cabecera de recepciones de mercancía |
| 9 | `detalle_entrada` | Entidad débil | Renglones de cada entrada (stock + costos) |
| 10 | `venta` | Entidad | Cabecera de transacciones de venta |
| 11 | `detalle_venta` | Entidad débil | Renglones del carrito de venta |

**Datos iniciales insertados:**
- Categoría: `"Escolar"`, `"Alimento"`
- Usuario administrador: login `admin`, rol `Jefe`, contraseña hasheada con BCrypt

---

### 6. `styles.css` — Estilos Globales

Hoja de estilos JavaFX CSS con la paleta de colores definida en los mockups del SRS.

**Variables de color definidas:**

| Variable | Valor | Uso |
|----------|-------|-----|
| `-omis-primary` | `#2196F3` | Botones principales, headers de tabla, sidebar activo |
| `-omis-primary-dark` | `#1976D2` | Hover de botones, títulos de sección |
| `-omis-primary-light` | `#BBDEFB` | Filas seleccionadas en tablas |
| `-omis-success` | `#4CAF50` | Botones de confirmación, notificaciones de éxito |
| `-omis-warning` | `#FFC107` | Alertas de stock bajo |
| `-omis-danger` | `#F44336` | Botones de eliminar, errores |
| `-omis-bg` | `#F5F5F5` | Fondo general de la aplicación |
| `-omis-bg-card` | `#FFFFFF` | Fondo de tarjetas y paneles |
| `-omis-border` | `#E0E0E0` | Bordes de campos, tablas y separadores |

**Componentes estilizados:** Botones (4 variantes), campos de texto, tablas con headers azules y filas alternadas, tarjetas con sombra, sidebar con fondo oscuro, notificaciones, pestañas (TabPane).

---

### 7. `.gitignore`

Excluye del repositorio:
- `target/` — Artefactos de compilación Maven
- `omis_data.db` — Base de datos local del cliente (datos sensibles)
- `Reportes de venta Omis/` — PDFs generados
- Archivos de IDE (`.idea/`, `nbproject/private/`)
- Archivos temporales del SO

---

## Verificación

| Prueba | Comando | Resultado |
|--------|---------|-----------|
| Compilación | `mvn compile` | ✅ BUILD SUCCESS |
| Ejecución | `mvn javafx:run` | ✅ Ventana JavaFX abierta correctamente |
| Creación de BD | Verificar `omis_data.db` | ✅ Archivo creado (64 KB), 11 tablas inicializadas |
| Consola de inicio | Mensaje en terminal | ✅ `"Base de datos inicializada correctamente: omis_data.db"` |

---

## Comandos Útiles

```bash
# Compilar el proyecto
mvn compile

# Ejecutar la aplicación
mvn javafx:run

# Limpiar y recompilar
mvn clean compile

# Empaquetar en JAR ejecutable
mvn package

# Ejecutar pruebas
mvn test
```

---

## Siguiente: Fase 2 — Login + Sistema de Roles

La siguiente fase implementará:
- Modelo `Usuario.java` con propiedades JavaFX
- `UsuarioDAO.java` para consultas SQL
- `PasswordUtil.java` con hashing BCrypt
- `SessionManager.java` para la sesión activa
- `AuthService.java` con la lógica de autenticación
- Vista `login.fxml` con su `LoginController.java`
