package com.omis.controller;

import com.omis.model.Usuario;
import com.omis.service.AuthService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

/**
 * Controlador de la vista de Login.
 * Gestiona la autenticación del usuario.
 */
public class LoginController {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;
    @FXML private Label lblError;
    @FXML private Button btnLogin;

    private final AuthService authService = new AuthService();

    @FXML
    public void initialize() {
        // Limpiar el mensaje de error al iniciar
        lblError.setText("");
        // Enfocar el campo de usuario al abrir
        txtUsuario.requestFocus();
    }

    /**
     * Maneja el evento de click en "Ingresar" o Enter en el campo de contraseña.
     */
    @FXML
    private void handleLogin() {
        String usuario = txtUsuario.getText();
        String password = txtPassword.getText();

        // Validaciones básicas
        if (usuario == null || usuario.trim().isEmpty()) {
            mostrarError("Ingrese su nombre de usuario.");
            txtUsuario.requestFocus();
            return;
        }

        if (password == null || password.isEmpty()) {
            mostrarError("Ingrese su contraseña.");
            txtPassword.requestFocus();
            return;
        }

        // Deshabilitar botón mientras se procesa
        btnLogin.setDisable(true);
        lblError.setText("");

        // Intentar autenticación
        Usuario usuarioAutenticado = authService.autenticar(usuario, password);

        if (usuarioAutenticado != null) {
            System.out.println("Login exitoso: " + usuarioAutenticado.getNombreCompleto()
                    + " (" + usuarioAutenticado.getRol() + ")");
            navegarAlDashboard();
        } else {
            mostrarError("Usuario o contraseña incorrectos.");
            txtPassword.clear();
            txtPassword.requestFocus();
            btnLogin.setDisable(false);
        }
    }

    /**
     * Muestra un mensaje de error en la vista.
     */
    private void mostrarError(String mensaje) {
        lblError.setText(mensaje);
    }

    /**
     * Navega a la vista del Dashboard tras un login exitoso.
     */
    private void navegarAlDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));
            Parent dashboardRoot = loader.load();

            Stage stage = (Stage) btnLogin.getScene().getWindow();
            Scene scene = new Scene(dashboardRoot);

            // Cargar estilos CSS
            String css = getClass().getResource("/css/styles.css").toExternalForm();
            scene.getStylesheets().add(css);

            stage.setScene(scene);
            stage.setTitle("OMIS - Panel de Control");
            
            // Forzar maximizado inmediatamente después del cambio de escena
            stage.setMaximized(true);

        } catch (Exception e) {
            System.err.println("Error al cargar el Dashboard: " + e.getMessage());
            e.printStackTrace();
            mostrarError("Error interno al cargar el sistema.");
            btnLogin.setDisable(false);
        }
    }
}
