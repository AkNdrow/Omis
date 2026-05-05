# OMIS — Fase 7: Reportes PDF y Tareas Programadas

**Fecha:** 05 de Mayo de 2026
**Responsable:** Andrés Cuevas García
**Estado:** ✅ Completada

---

## Objetivo

Automatizar la generación de reportes diarios, semanales y mensuales en formato PDF (usando Apache PDFBox). Asegurar que la estructura jerárquica de carpetas respete el formato solicitado, organizando por "Año / Mes Año / Día". Resolver el problema de generación de reportes nocturnos para sistemas locales mediante un programador en segundo plano y una validación de arranque seguro ("Startup Check").

---

## Modificaciones Implementadas

### 1. Refactorización de Estructura de Directorios
Se rediseñó el mapeo de carpetas dentro de `ReporteService.java` para cumplir exactamente con el diagrama jerárquico establecido:
* Ruta Base: `Reportes de venta Omis/`
* Nivel 1: Año (`2026/`)
* Nivel 2: Nombre de Mes y Año (`Junio 2026/`)
* Nivel 3 (Días): Fecha completa con formato guionado (`01-06-2026/`) — *El uso de guiones en lugar de barras diagonales `/` garantiza la compatibilidad con el sistema de archivos de Windows.*

Dentro de la carpeta del **día**, se alojan:
- Tickets individuales (`ticket HH-mm.pdf`)
- Reporte global del día (`Reporte del dia.pdf`)

Dentro de la carpeta del **mes**, se alojan:
- Reportes Semanales (`Reporte semanal (fechaInicio al fechaFin).pdf`)
- Reporte del Mes (`Reporte del mes.pdf`)

### 2. Capa de Acceso a Datos (DAO)
Se añadió el método `findByDateRange(String fechaInicio, String fechaFin)` a `VentaDAO.java`. Este permite extraer y consolidar todas las ventas correspondientes a un lapso de tiempo específico para alimentar los PDFs de reporte.

### 3. Servicio de Reportes (PDFBox)
En `ReporteService.java` se añadió un generador modular genérico `generarReporte(titulo, nombreArchivo, ventas)` que:
- Dibuja un membrete oficial.
- Itera sobre la lista de transacciones (`Venta`).
- Genera un resumen tabular (`ID Venta`, `Fecha/Hora`, `Cajero`, `Monto Total`).
- Imprime el `Gran Total Recaudado`.

Se expusieron los métodos públicos:
- `generarReporteDiario(fecha, ventas)`
- `generarReporteSemanal(inicio, fin, ventas)`
- `generarReporteMensual(fechaMes, ventas)`

### 4. Automatización con `ReportScheduler.java`
Dado que el programa es local (de escritorio) y la máquina puede estar apagada a las 00:00, se implementó una solución híbrida sumamente robusta:

1. **Chequeo al Arranque (Startup Check):**
   Al abrir la aplicación, el sistema analiza retrospectivamente los últimos 3 días. Si detecta que falta el reporte diario de ayer, que el domingo pasado no se generó el semanal o que acabó el mes y no hay reporte mensual, el sistema hace la consulta a la BD y **los genera retroactivamente** antes de que el usuario haga nada.
2. **Cron Interno en Segundo Plano:**
   Mientras la aplicación está abierta, un `ScheduledExecutorService` se ejecuta silenciosamente cada 15 minutos. Si el reloj cruza las 00:00 (hasta las 00:15 para tener una ventana segura de acción), manda a ejecutar los cierres diarios, semanales (si es domingo) y mensuales (si es fin de mes) sin intervención humana.

Ambos ganchos fueron inyectados en `Main.java` mediante `ReportScheduler.iniciar()` (en el `init`) y `ReportScheduler.detener()` (en el `stop`).

---

## Verificaciones Realizadas

| Prueba | Resultado |
|--------|-----------|
| Compilación | ✅ Sin errores de sintaxis (`mvn compile` exitoso) |
| Creación de carpetas anidadas seguras en Windows | ✅ `Reportes de venta Omis/2026/Mayo 2026/...` |
| Hilo en segundo plano seguro | ✅ Inicializa correctamente, no bloquea el UI de JavaFX |
| Apagado limpio del hilo de ExecutorService | ✅ Hook en `Main.stop()` asegura que no se queden procesos zombies al cerrar la app |

---

## Siguiente: Fase 8 — Dashboard y Gestión de Usuarios

La siguiente y última fase de lógica engloba:
- Finalización de las estadísticas de ingresos y ventas en `dashboard.fxml`.
- Conclusión del rediseño estético y de botones solicitados.
- Preparación final para su liberación.
