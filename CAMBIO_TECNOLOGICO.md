# Justificación del Cambio Tecnológico — Proyecto OMIS

## Contexto

El documento SRS original del proyecto OMIS establece como stack tecnológico principal **Java NetBeans + MySQL** para el desarrollo de una aplicación de escritorio. Tras el análisis técnico de factibilidad y las restricciones operativas del cliente (Papelería Omis), se identificaron oportunidades de mejora en tres componentes clave que permiten simplificar la instalación, el mantenimiento y la experiencia visual del sistema sin alterar los requerimientos funcionales ni el alcance del proyecto.

---

## 1. Base de Datos: MySQL → SQLite

### Motivo del cambio

MySQL requiere la instalación y ejecución permanente de un **servidor de base de datos** como servicio en segundo plano dentro del equipo del local. Esto implica:

- Instalar software adicional (MySQL Server, XAMPP o WampServer) en la computadora del cliente.
- Configurar el servicio para que inicie automáticamente con Windows.
- Si el servicio se detiene o falla, la aplicación completa deja de funcionar.
- Los respaldos a memorias USB requieren ejecutar el comando técnico `mysqldump`, lo cual está fuera del alcance del perfil operativo del personal.

### Tecnología adoptada: SQLite

SQLite es un motor de base de datos relacional embebido que almacena toda la información en **un único archivo** dentro del disco duro (por ejemplo: `omis_data.db`).

| Aspecto | MySQL | SQLite |
|---------|-------|--------|
| Instalación en el local | Requiere instalar MySQL Server | No requiere instalación adicional |
| Servicio en segundo plano | Sí (debe estar corriendo siempre) | No (se integra directamente en la app) |
| Respaldo a USB | Ejecutar `mysqldump` (técnico) | Copiar el archivo `.db` a la USB |
| Restauración desde USB | Importar `.sql` desde terminal | Copiar el archivo `.db` de vuelta |
| Soporte SQL estándar | Completo | Completo (compatible con las 11 tablas del modelo E/R) |
| Concurrencia | Alta (múltiples conexiones) | Limitada (1 escritura a la vez) |
| Relevancia para el proyecto | Sobredimensionado (solo 2 usuarios) | Ideal para un establecimiento con máximo 2 usuarios simultáneos |

### Impacto en el desarrollo

- El driver JDBC cambia de `mysql-connector-java` a `sqlite-jdbc` (org.xerial).
- La sintaxis SQL se mantiene prácticamente igual; los tipos de datos `ENUM` se reemplazan por restricciones `CHECK`.
- Se elimina la dependencia de infraestructura externa en la máquina del cliente.

---

## 2. Interfaz Gráfica: Java Swing → JavaFX

### Motivo del cambio

Java Swing es el framework clásico de interfaces gráficas de Java. Sin embargo, su apariencia visual por defecto es anticuada y no refleja los diseños modernos que fueron aprobados durante la fase de Mockup en Figma. Lograr una interfaz visualmente similar a los mockups con Swing requeriría un esfuerzo significativo de personalización manual de cada componente.

### Tecnología adoptada: JavaFX

JavaFX es el framework sucesor de Swing para interfaces gráficas en Java. Ofrece capacidades modernas de diseño y renderizado.

| Aspecto | Swing | JavaFX |
|---------|-------|--------|
| Apariencia por defecto | Estilo Windows clásico (anticuado) | Moderno y limpio |
| Personalización visual | Programática (código Java para cada estilo) | **Archivos CSS** (igual que en diseño web) |
| Gráficas y charts | Requiere librería externa (JFreeChart) | **Incluidas nativamente** (BarChart, PieChart, LineChart) |
| Diseñador visual | NetBeans GUI Builder | **Scene Builder** (herramienta visual gratuita de Gluon) |
| Layouts responsivos | Limitados | Completos (VBox, HBox, GridPane, BorderPane) |
| Tablas de datos | JTable (funcional pero básico) | TableView (con filtros, ordenamiento y binding) |
| Compatibilidad con mockups de Figma | Baja | Alta |

### Impacto en el desarrollo

- Las vistas se construyen con archivos **FXML** (estructura XML) + **CSS** (estilos), separando la lógica del diseño.
- Se puede utilizar **Scene Builder** como herramienta visual para construir las interfaces sin escribir código de layout manualmente.
- Se agrega la dependencia de JavaFX al proyecto Maven (`org.openjfx:javafx-controls`, `org.openjfx:javafx-fxml`).
- El patrón de arquitectura se adapta naturalmente a **MVC**: los archivos FXML representan la Vista, los Controllers manejan eventos y los Models encapsulan los datos.

---

## 3. Build Tool: Ant (NetBeans clásico) → Maven

### Motivo del cambio

El proyecto será desarrollado por un equipo de **5 integrantes** que colaborarán a través de **GitHub**. Con el sistema de build clásico de NetBeans (Ant), cada desarrollador necesita descargar manualmente las librerías (archivos `.jar`) y configurar las rutas en su IDE de forma individual. Esto genera:

- Inconsistencias entre entornos de desarrollo ("a mí no me compila").
- Necesidad de subir archivos binarios `.jar` al repositorio Git.
- Dificultad para agregar o actualizar dependencias.

### Tecnología adoptada: Maven

Maven es un gestor de proyectos y dependencias que estandariza la estructura del proyecto y automatiza la descarga de librerías.

| Aspecto | Ant (NetBeans clásico) | Maven |
|---------|----------------------|-------|
| Agregar una librería | Descargar `.jar`, copiarlo a `lib/`, configurar classpath | Agregar 3 líneas en `pom.xml` |
| Clonar el repo en otra PC | Falla si faltan `.jar` | `mvn install` descarga todo automáticamente |
| Estructura del proyecto | Variable según cada desarrollador | **Estandarizada** (`src/main/java`, `src/main/resources`) |
| Generar ejecutable `.jar` | Configuración manual | `mvn package` (un comando) |
| Soporte en NetBeans | Nativo | Nativo (Archivo → Nuevo Proyecto → Maven) |

### Impacto en el desarrollo

- El proyecto se crea como **Maven Java Application** en NetBeans.
- Todas las dependencias (SQLite-JDBC, JavaFX, jBCrypt, PDFBox) se declaran en el archivo `pom.xml`.
- Cualquier integrante del equipo que clone el repositorio tendrá el mismo entorno funcional con un solo comando.

---

## Resumen del Stack Tecnológico Final

| Componente | Original (SRS) | Actualizado | Razón principal |
|------------|----------------|-------------|-----------------|
| Base de datos | MySQL | **SQLite** | Elimina servidor local, respaldo por copia de archivo |
| Interfaz gráfica | Swing | **JavaFX** | CSS nativo, gráficas incluidas, compatible con mockups |
| Build tool | Ant | **Maven** | Gestión automática de dependencias para 5 desarrolladores |
| IDE | NetBeans | **NetBeans** | Sin cambio — soporta Maven y JavaFX nativamente |
| Lenguaje | Java | **Java** | Sin cambio |
| Control de versiones | GitHub | **GitHub** | Sin cambio |
| Sistema operativo | Windows 10 | **Windows 10** | Sin cambio |

---

## Nota sobre compatibilidad

Estos cambios **no alteran** los requerimientos funcionales, las historias de usuario, el modelo entidad-relación ni las políticas de la empresa definidas en el documento SRS. Las 11 tablas del modelo de datos, los 2 roles de usuario (Jefe y Empleado), las 9 interfaces y los 4 bloques funcionales se mantienen íntegramente.
