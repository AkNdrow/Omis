package com.omis.controller;

import com.omis.dao.CategoriaDAO;
import com.omis.dao.MarcaDAO;
import com.omis.dao.ProveedorDAO;
import com.omis.model.Categoria;
import com.omis.model.Marca;
import com.omis.model.Proveedor;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * Controlador de la vista de Catálogos.
 * Gestiona CRUD de Categorías, Marcas y Proveedores con TabPane.
 */
public class CatalogosController {

    // --- Categorías ---
    @FXML private TextField txtCategoriaNombre;
    @FXML private Button btnCategoriaGuardar;
    @FXML private Button btnCategoriaCancelar;
    @FXML private TableView<Categoria> tablaCategorias;

    // --- Marcas ---
    @FXML private TextField txtMarcaNombre;
    @FXML private Button btnMarcaGuardar;
    @FXML private Button btnMarcaCancelar;
    @FXML private TableView<Marca> tablaMarcas;

    // --- Proveedores ---
    @FXML private TextField txtProveedorNombre;
    @FXML private TextField txtProveedorContacto;
    @FXML private TextField txtProveedorFrecuencia;
    @FXML private Button btnProveedorGuardar;
    @FXML private Button btnProveedorCancelar;
    @FXML private TableView<Proveedor> tablaProveedores;

    private final CategoriaDAO categoriaDAO = new CategoriaDAO();
    private final MarcaDAO marcaDAO = new MarcaDAO();
    private final ProveedorDAO proveedorDAO = new ProveedorDAO();

    // IDs para modo edición (null = modo agregar)
    private Integer categoriaEditId = null;
    private Integer marcaEditId = null;
    private Integer proveedorEditId = null;

    @FXML
    public void initialize() {
        cargarCategorias();
        cargarMarcas();
        cargarProveedores();
    }

    // ==================== CATEGORÍAS ====================

    private void cargarCategorias() {
        tablaCategorias.setItems(FXCollections.observableArrayList(categoriaDAO.findAll()));
    }

    @FXML
    private void handleCategoriaGuardar() {
        String nombre = txtCategoriaNombre.getText().trim();
        if (nombre.isEmpty()) {
            mostrarAlerta("Error", "Ingrese el nombre de la categoría.");
            return;
        }

        if (categoriaEditId == null) {
            // Modo agregar
            Categoria c = new Categoria();
            c.setNombreCategoria(nombre);
            if (categoriaDAO.create(c)) {
                cargarCategorias();
                txtCategoriaNombre.clear();
            } else {
                mostrarAlerta("Error", "No se pudo crear la categoría. Verifique que no esté duplicada.");
            }
        } else {
            // Modo editar
            Categoria c = new Categoria(categoriaEditId, nombre);
            if (categoriaDAO.update(c)) {
                cargarCategorias();
                handleCategoriaCancelar();
            } else {
                mostrarAlerta("Error", "No se pudo actualizar la categoría.");
            }
        }
    }

    @FXML
    private void handleCategoriaEditar() {
        Categoria sel = tablaCategorias.getSelectionModel().getSelectedItem();
        if (sel == null) {
            mostrarAlerta("Aviso", "Seleccione una categoría de la tabla.");
            return;
        }
        categoriaEditId = sel.getIdCategoria();
        txtCategoriaNombre.setText(sel.getNombreCategoria());
        btnCategoriaGuardar.setText("Actualizar");
        btnCategoriaCancelar.setVisible(true);
    }

    @FXML
    private void handleCategoriaEliminar() {
        Categoria sel = tablaCategorias.getSelectionModel().getSelectedItem();
        if (sel == null) {
            mostrarAlerta("Aviso", "Seleccione una categoría de la tabla.");
            return;
        }
        if (confirmar("¿Eliminar la categoría \"" + sel.getNombreCategoria() + "\"?")) {
            if (categoriaDAO.delete(sel.getIdCategoria())) {
                cargarCategorias();
            } else {
                mostrarAlerta("Error", "No se pudo eliminar. La categoría puede tener productos asociados.");
            }
        }
    }

    @FXML
    private void handleCategoriaCancelar() {
        categoriaEditId = null;
        txtCategoriaNombre.clear();
        btnCategoriaGuardar.setText("Agregar");
        btnCategoriaCancelar.setVisible(false);
    }

    // ==================== MARCAS ====================

    private void cargarMarcas() {
        tablaMarcas.setItems(FXCollections.observableArrayList(marcaDAO.findAll()));
    }

    @FXML
    private void handleMarcaGuardar() {
        String nombre = txtMarcaNombre.getText().trim();
        if (nombre.isEmpty()) {
            mostrarAlerta("Error", "Ingrese el nombre de la marca.");
            return;
        }

        if (marcaEditId == null) {
            Marca m = new Marca();
            m.setNombreMarca(nombre);
            if (marcaDAO.create(m)) {
                cargarMarcas();
                txtMarcaNombre.clear();
            } else {
                mostrarAlerta("Error", "No se pudo crear la marca. Verifique que no esté duplicada.");
            }
        } else {
            Marca m = new Marca(marcaEditId, nombre);
            if (marcaDAO.update(m)) {
                cargarMarcas();
                handleMarcaCancelar();
            } else {
                mostrarAlerta("Error", "No se pudo actualizar la marca.");
            }
        }
    }

    @FXML
    private void handleMarcaEditar() {
        Marca sel = tablaMarcas.getSelectionModel().getSelectedItem();
        if (sel == null) {
            mostrarAlerta("Aviso", "Seleccione una marca de la tabla.");
            return;
        }
        marcaEditId = sel.getIdMarca();
        txtMarcaNombre.setText(sel.getNombreMarca());
        btnMarcaGuardar.setText("Actualizar");
        btnMarcaCancelar.setVisible(true);
    }

    @FXML
    private void handleMarcaEliminar() {
        Marca sel = tablaMarcas.getSelectionModel().getSelectedItem();
        if (sel == null) {
            mostrarAlerta("Aviso", "Seleccione una marca de la tabla.");
            return;
        }
        if (confirmar("¿Eliminar la marca \"" + sel.getNombreMarca() + "\"?")) {
            if (marcaDAO.delete(sel.getIdMarca())) {
                cargarMarcas();
            } else {
                mostrarAlerta("Error", "No se pudo eliminar. La marca puede tener láminas asociadas.");
            }
        }
    }

    @FXML
    private void handleMarcaCancelar() {
        marcaEditId = null;
        txtMarcaNombre.clear();
        btnMarcaGuardar.setText("Agregar");
        btnMarcaCancelar.setVisible(false);
    }

    // ==================== PROVEEDORES ====================

    private void cargarProveedores() {
        tablaProveedores.setItems(FXCollections.observableArrayList(proveedorDAO.findAll()));
    }

    @FXML
    private void handleProveedorGuardar() {
        String nombre = txtProveedorNombre.getText().trim();
        if (nombre.isEmpty()) {
            mostrarAlerta("Error", "Ingrese el nombre de la empresa.");
            return;
        }

        String contacto = txtProveedorContacto.getText().trim();
        String frecuencia = txtProveedorFrecuencia.getText().trim();

        if (proveedorEditId == null) {
            Proveedor p = new Proveedor();
            p.setNombreEmpresa(nombre);
            p.setContacto(contacto.isEmpty() ? null : contacto);
            p.setFrecuenciaEntrega(frecuencia.isEmpty() ? null : frecuencia);
            if (proveedorDAO.create(p)) {
                cargarProveedores();
                limpiarFormProveedor();
            } else {
                mostrarAlerta("Error", "No se pudo crear el proveedor.");
            }
        } else {
            Proveedor p = new Proveedor(proveedorEditId, nombre,
                    contacto.isEmpty() ? null : contacto,
                    frecuencia.isEmpty() ? null : frecuencia);
            if (proveedorDAO.update(p)) {
                cargarProveedores();
                handleProveedorCancelar();
            } else {
                mostrarAlerta("Error", "No se pudo actualizar el proveedor.");
            }
        }
    }

    @FXML
    private void handleProveedorEditar() {
        Proveedor sel = tablaProveedores.getSelectionModel().getSelectedItem();
        if (sel == null) {
            mostrarAlerta("Aviso", "Seleccione un proveedor de la tabla.");
            return;
        }
        proveedorEditId = sel.getIdProveedor();
        txtProveedorNombre.setText(sel.getNombreEmpresa());
        txtProveedorContacto.setText(sel.getContacto() != null ? sel.getContacto() : "");
        txtProveedorFrecuencia.setText(sel.getFrecuenciaEntrega() != null ? sel.getFrecuenciaEntrega() : "");
        btnProveedorGuardar.setText("Actualizar");
        btnProveedorCancelar.setVisible(true);
    }

    @FXML
    private void handleProveedorEliminar() {
        Proveedor sel = tablaProveedores.getSelectionModel().getSelectedItem();
        if (sel == null) {
            mostrarAlerta("Aviso", "Seleccione un proveedor de la tabla.");
            return;
        }
        if (confirmar("¿Eliminar al proveedor \"" + sel.getNombreEmpresa() + "\"?")) {
            if (proveedorDAO.delete(sel.getIdProveedor())) {
                cargarProveedores();
            } else {
                mostrarAlerta("Error", "No se pudo eliminar. El proveedor puede tener entradas asociadas.");
            }
        }
    }

    @FXML
    private void handleProveedorCancelar() {
        proveedorEditId = null;
        limpiarFormProveedor();
        btnProveedorGuardar.setText("Agregar");
        btnProveedorCancelar.setVisible(false);
    }

    private void limpiarFormProveedor() {
        txtProveedorNombre.clear();
        txtProveedorContacto.clear();
        txtProveedorFrecuencia.clear();
    }

    // ==================== UTILIDADES ====================

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private boolean confirmar(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmación");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        return alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }
}
