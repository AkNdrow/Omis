package com.omis.controller;

import com.omis.model.Usuario;
import com.omis.util.SessionManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import com.omis.dao.ProductoDAO;
import com.omis.dao.UsuarioDAO;
import com.omis.dao.VentaDAO;
import com.omis.model.Producto;
import com.omis.model.Venta;
import java.time.LocalDate;
import java.util.List;

/**
 * Controlador del Dashboard principal.
 * Gestiona la navegación entre módulos cargando vistas FXML
 * dinámicamente en el área de contenido central (StackPane).
 */
public class DashboardController {

    @FXML private Label lblNombreUsuario;
    @FXML private Label lblRolUsuario;
    @FXML private Label lblBienvenida;
    @FXML private Button btnNavUsuarios;
    @FXML private Button btnNavCatalogos;
    @FXML private Button btnNavInventario;
    @FXML private Button btnNavEntradas;
    @FXML private Button btnNavPuntoVenta;
    @FXML private StackPane contentPane;

    @FXML private Label lblVentasHoy;
    @FXML private Label lblBajoStock;
    @FXML private Label lblTotalUsuarios;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final ProductoDAO productoDAO = new ProductoDAO();
    private final VentaDAO ventaDAO = new VentaDAO();

    @FXML
    public void initialize() {
        Usuario usuario = SessionManager.getInstance().getUsuarioActivo();

        if (usuario != null) {
            lblNombreUsuario.setText(usuario.getNombreCompleto());
            lblRolUsuario.setText(usuario.getRol());
            lblBienvenida.setText("Sesión iniciada como: " + usuario.getNombreCompleto()
                    + " — Rol: " + usuario.getRol());
        }

        // Ocultar opciones exclusivas de Jefe para Empleados
        if (!SessionManager.getInstance().esJefe()) {
            btnNavUsuarios.setVisible(false);
            btnNavUsuarios.setManaged(false);
            btnNavCatalogos.setVisible(false);
            btnNavCatalogos.setManaged(false);
            btnNavInventario.setVisible(false);
            btnNavInventario.setManaged(false);
            btnNavEntradas.setVisible(false);
            btnNavEntradas.setManaged(false);
        }

        cargarEstadisticas();
    }

    private void cargarEstadisticas() {
        if (lblVentasHoy == null) return; // Si no está en la vista principal

        // Ventas de Hoy
        LocalDate hoy = LocalDate.now();
        List<Venta> ventasHoy = ventaDAO.findByDateRange(hoy.toString() + " 00:00:00", hoy.toString() + " 23:59:59");
        double sumaVentas = ventasHoy.stream().mapToDouble(Venta::getMontoTotal).sum();
        lblVentasHoy.setText(String.format("$%.2f", sumaVentas));

        // Productos bajo stock (menor a 5)
        long bajos = productoDAO.findAll().stream().filter(p -> p.getStockActual() < 5).count();
        lblBajoStock.setText(String.valueOf(bajos));

        // Total Usuarios
        lblTotalUsuarios.setText(String.valueOf(usuarioDAO.count()));
    }

    /**
     * Carga una vista FXML en el área de contenido central.
     */
    private void cargarContenido(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            // Si es la vista de inicio, usamos este mismo controlador para inyectar los datos del dashboard.
            // Para el resto de módulos (Usuarios, Inventario, etc.), dejamos que usen sus propios controladores.
            if (fxmlPath.contains("dashboard_home")) {
                loader.setController(this); 
            }
            
            Node contenido = loader.load();
            contentPane.getChildren().clear();
            contentPane.getChildren().add(contenido);
            
            // Forzar que la ventana siga maximizada
            Stage stage = (Stage) contentPane.getScene().getWindow();
            if (stage != null) {
                stage.setMaximized(true);
            }
        } catch (Exception e) {
            System.err.println("Error al cargar vista " + fxmlPath + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ==================== NAVEGACIÓN ====================

    @FXML
    private void handleNavInicio() {
        try {
            // Cargamos el FXML modular del home
            cargarContenido("/fxml/dashboard_home.fxml");
            // Recargamos las estadísticas para que se vean en los labels recién inyectados
            cargarEstadisticas();
        } catch (Exception e) {
            System.err.println("Error al navegar al inicio: " + e.getMessage());
        }
    }

    @FXML
    private void handleNavReportes() {
        try {
            // Ruta a la carpeta de reportes
            java.io.File folder = new java.io.File("Reportes de venta Omis");
            if (!folder.exists()) folder.mkdirs();
            
            // Abrir el explorador de archivos del sistema
            if (java.awt.Desktop.isDesktopSupported()) {
                java.awt.Desktop.getDesktop().open(folder);
            }
            
            // Mantener pantalla completa
            Stage stage = (Stage) contentPane.getScene().getWindow();
            if (stage != null) stage.setMaximized(true);
        } catch (Exception e) {
            System.err.println("Error al abrir carpeta de reportes: " + e.getMessage());
        }
    }

    @FXML
    private void handleNavUsuarios() {
        cargarContenido("/fxml/usuarios.fxml");
    }

    @FXML
    private void handleNavCatalogos() {
        cargarContenido("/fxml/catalogos.fxml");
    }

    @FXML
    private void handleNavInventario() {
        cargarContenido("/fxml/productos.fxml");
    }

    @FXML
    private void handleNavEntradas() {
        cargarContenido("/fxml/entrada_mercancia.fxml");
    }

    @FXML
    private void handleNavPuntoVenta() {
        cargarContenido("/fxml/venta.fxml");
    }

    /**
     * Cierra la sesión y regresa a la pantalla de login.
     */
    @FXML
    private void handleCerrarSesion() {
        SessionManager.getInstance().logout();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
            Parent loginRoot = loader.load();

            Stage stage = (Stage) contentPane.getScene().getWindow();
            Scene scene = new Scene(loginRoot);

            String css = getClass().getResource("/css/styles.css").toExternalForm();
            scene.getStylesheets().add(css);

            stage.setScene(scene);
            stage.setTitle("OMIS - Iniciar Sesión");
            
            // Forzar maximizado al cerrar sesión
            stage.setMaximized(true);

        } catch (Exception e) {
            System.err.println("Error al regresar al login: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
