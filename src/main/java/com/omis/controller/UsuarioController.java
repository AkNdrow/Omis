package com.omis.controller;

import com.omis.dao.UsuarioDAO;
import com.omis.model.Usuario;
import com.omis.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

/**
 * Controlador de la vista de Gestión de Usuarios.
 */
public class UsuarioController {

    @FXML private TextField txtNombre;
    @FXML private TextField txtUsuarioLogin;
    @FXML private PasswordField txtPassword;
    @FXML private ComboBox<String> cmbRol;
    @FXML private Button btnGuardar;
    @FXML private TableView<Usuario> tablaUsuarios;

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final ObservableList<Usuario> listaUsuarios = FXCollections.observableArrayList();

    private Usuario usuarioEditando = null;

    @FXML
    public void initialize() {
        cmbRol.setItems(FXCollections.observableArrayList("Jefe", "Empleado"));
        tablaUsuarios.setItems(listaUsuarios);
        cargarUsuarios();
    }

    private void cargarUsuarios() {
        listaUsuarios.setAll(usuarioDAO.findAll());
    }

    @FXML
    private void handleGuardar() {
        String nombre = txtNombre.getText().trim();
        String login = txtUsuarioLogin.getText().trim();
        String password = txtPassword.getText().trim();
        String rol = cmbRol.getValue();

        if (nombre.isEmpty() || login.isEmpty() || rol == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos Incompletos", "Por favor complete todos los campos obligatorios.");
            return;
        }

        if (usuarioEditando == null && password.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Contraseña Requerida", "Debe asignar una contraseña para el nuevo usuario.");
            return;
        }

        if (usuarioEditando == null) {
            // Validar que el login no exista
            if (usuarioDAO.findByLogin(login) != null) {
                mostrarAlerta(Alert.AlertType.WARNING, "Usuario Duplicado", "El login de usuario ya existe en el sistema.");
                return;
            }

            Usuario nuevo = new Usuario();
            nuevo.setNombreCompleto(nombre);
            nuevo.setUsuarioLogin(login);
            nuevo.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
            nuevo.setRol(rol);

            if (usuarioDAO.create(nuevo)) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Usuario creado correctamente.");
                handleLimpiar();
                cargarUsuarios();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo crear el usuario.");
            }
        } else {
            // Evitar duplicados al editar login
            Usuario existente = usuarioDAO.findByLogin(login);
            if (existente != null && existente.getIdUsuario() != usuarioEditando.getIdUsuario()) {
                mostrarAlerta(Alert.AlertType.WARNING, "Usuario Duplicado", "El login pertenece a otro usuario.");
                return;
            }

            usuarioEditando.setNombreCompleto(nombre);
            usuarioEditando.setUsuarioLogin(login);
            usuarioEditando.setRol(rol);
            if (!password.isEmpty()) {
                usuarioEditando.setPassword(BCrypt.hashpw(password, BCrypt.gensalt()));
            } else {
                // Para que el DAO sepa que no debe actualizar el password
                usuarioEditando.setPassword(null);
            }

            if (usuarioDAO.update(usuarioEditando)) {
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Usuario actualizado correctamente.");
                handleLimpiar();
                cargarUsuarios();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo actualizar el usuario.");
            }
        }
    }

    @FXML
    private void handleEditarSeleccionado() {
        Usuario sel = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (sel != null) {
            usuarioEditando = sel;
            txtNombre.setText(sel.getNombreCompleto());
            txtUsuarioLogin.setText(sel.getUsuarioLogin());
            cmbRol.setValue(sel.getRol());
            txtPassword.clear();
            btnGuardar.setText("Actualizar");
        }
    }

    @FXML
    private void handleEliminarSeleccionado() {
        Usuario sel = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (sel != null) {
            // Protección contra eliminarse a sí mismo
            if (sel.getIdUsuario() == SessionManager.getInstance().getUsuarioActivo().getIdUsuario()) {
                mostrarAlerta(Alert.AlertType.WARNING, "Acción Inválida", "No puede eliminar su propio usuario mientras está en sesión.");
                return;
            }

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmar Eliminación");
            confirm.setHeaderText("¿Está seguro de eliminar al usuario '" + sel.getUsuarioLogin() + "'?");
            
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (usuarioDAO.delete(sel.getIdUsuario())) {
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Usuario eliminado.");
                    handleLimpiar();
                    cargarUsuarios();
                } else {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo eliminar el usuario (podría tener ventas vinculadas).");
                }
            }
        }
    }

    @FXML
    private void handleLimpiar() {
        usuarioEditando = null;
        txtNombre.clear();
        txtUsuarioLogin.clear();
        txtPassword.clear();
        cmbRol.getSelectionModel().clearSelection();
        btnGuardar.setText("Guardar");
        tablaUsuarios.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
