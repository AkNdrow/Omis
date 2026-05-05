package com.omis.controller;

import com.omis.dao.*;
import com.omis.model.*;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controlador de la vista de Alta de Productos.
 * Gestiona el formulario dinámico con subentidades (Lámina / Perecedero).
 */
public class ProductoController {

    // Campos base
    @FXML private TextField txtNombre;
    @FXML private ComboBox<Categoria> cmbCategoria;
    @FXML private TextField txtUbicacion;
    @FXML private CheckBox chkEsLamina;
    @FXML private CheckBox chkEsPerecedereo;

    // Panel Lámina
    @FXML private javafx.scene.layout.VBox panelLamina;
    @FXML private ComboBox<Marca> cmbMarca;
    @FXML private TextField txtNumeroSerie;
    @FXML private TextField txtTema;
    @FXML private TextField txtMateria;

    // Panel Perecedero
    @FXML private javafx.scene.layout.VBox panelPerecedereo;
    @FXML private TextField txtRefrigeracion;
    @FXML private DatePicker dpCaducidad;

    // Búsqueda y tabla
    @FXML private TextField txtBuscar;
    @FXML private TableView<Producto> tablaProductos;
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;

    private final ProductoDAO productoDAO = new ProductoDAO();
    private final LaminaDAO laminaDAO = new LaminaDAO();
    private final PerecederoDAO perecederoDAO = new PerecederoDAO();
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();
    private final MarcaDAO marcaDAO = new MarcaDAO();

    private Integer productoEditId = null;

    @FXML
    public void initialize() {
        // Cargar combos
        cmbCategoria.setItems(FXCollections.observableArrayList(categoriaDAO.findAll()));
        cmbMarca.setItems(FXCollections.observableArrayList(marcaDAO.findAll()));

        // Cargar tabla
        cargarProductos();
    }

    /**
     * Muestra/oculta los paneles de subtipo según los checkboxes.
     * Solo se puede seleccionar uno a la vez.
     */
    @FXML
    private void handleTipoChanged() {
        // Exclusividad: si se marca uno, se desmarca el otro
        if (chkEsLamina.isSelected() && chkEsPerecedereo.isSelected()) {
            // El último que se marcó gana
            if (panelLamina.isVisible()) {
                chkEsLamina.setSelected(false);
            } else {
                chkEsPerecedereo.setSelected(false);
            }
        }

        panelLamina.setVisible(chkEsLamina.isSelected());
        panelLamina.setManaged(chkEsLamina.isSelected());

        panelPerecedereo.setVisible(chkEsPerecedereo.isSelected());
        panelPerecedereo.setManaged(chkEsPerecedereo.isSelected());
    }

    @FXML
    private void handleGuardar() {
        // Validar nombre
        String nombre = txtNombre.getText().trim();
        if (nombre.isEmpty()) {
            mostrarAlerta("Error", "Ingrese el nombre del producto.");
            return;
        }

        Categoria catSel = cmbCategoria.getValue();

        // Crear/actualizar producto base
        Producto prod = new Producto();
        prod.setNombre(nombre);
        prod.setIdCategoria(catSel != null ? catSel.getIdCategoria() : null);
        prod.setUbicacionFisica(txtUbicacion.getText().trim());
        prod.setCostoAdquisicion(0);
        prod.setPrecioVenta(0);
        prod.setMargenUtilidad(0);
        prod.setStockActual(0);

        if (productoEditId == null) {
            // --- MODO AGREGAR ---
            int newId = productoDAO.create(prod);
            if (newId < 0) {
                mostrarAlerta("Error", "No se pudo crear el producto.");
                return;
            }

            // Crear subentidad si aplica
            if (chkEsLamina.isSelected()) {
                if (!guardarLamina(newId)) {
                    mostrarAlerta("Advertencia", "Producto creado pero hubo un error al guardar datos de lámina.");
                }
            } else if (chkEsPerecedereo.isSelected()) {
                if (!guardarPerecedereo(newId)) {
                    mostrarAlerta("Advertencia", "Producto creado pero hubo un error al guardar datos de perecedero.");
                }
            }

            cargarProductos();
            limpiarFormulario();

        } else {
            // --- MODO EDITAR ---
            prod.setIdProducto(productoEditId);
            if (!productoDAO.update(prod)) {
                mostrarAlerta("Error", "No se pudo actualizar el producto.");
                return;
            }

            // Actualizar/crear/eliminar subentidad
            Lamina lamExist = laminaDAO.findByProductoId(productoEditId);
            Perecedero perExist = perecederoDAO.findByProductoId(productoEditId);

            if (chkEsLamina.isSelected()) {
                if (lamExist != null) {
                    guardarLaminaUpdate(productoEditId);
                } else {
                    // Limpiar otra subentidad si existía
                    if (perExist != null) perecederoDAO.delete(productoEditId);
                    guardarLamina(productoEditId);
                }
            } else if (chkEsPerecedereo.isSelected()) {
                if (perExist != null) {
                    guardarPerecedereoUpdate(productoEditId);
                } else {
                    if (lamExist != null) laminaDAO.delete(productoEditId);
                    guardarPerecedereo(productoEditId);
                }
            } else {
                // Sin subtipo: limpiar ambos
                if (lamExist != null) laminaDAO.delete(productoEditId);
                if (perExist != null) perecederoDAO.delete(productoEditId);
            }

            cargarProductos();
            handleCancelar();
        }
    }

    @FXML
    private void handleEditar() {
        Producto sel = tablaProductos.getSelectionModel().getSelectedItem();
        if (sel == null) {
            mostrarAlerta("Aviso", "Seleccione un producto de la tabla.");
            return;
        }

        productoEditId = sel.getIdProducto();
        txtNombre.setText(sel.getNombre());
        txtUbicacion.setText(sel.getUbicacionFisica() != null ? sel.getUbicacionFisica() : "");

        // Seleccionar categoría en el combo
        for (Categoria cat : cmbCategoria.getItems()) {
            if (sel.getIdCategoria() != null && cat.getIdCategoria() == sel.getIdCategoria()) {
                cmbCategoria.setValue(cat);
                break;
            }
        }

        // Verificar si tiene subentidad
        chkEsLamina.setSelected(false);
        chkEsPerecedereo.setSelected(false);

        Lamina lam = laminaDAO.findByProductoId(productoEditId);
        if (lam != null) {
            chkEsLamina.setSelected(true);
            for (Marca m : cmbMarca.getItems()) {
                if (lam.getIdMarca() != null && m.getIdMarca() == lam.getIdMarca()) {
                    cmbMarca.setValue(m);
                    break;
                }
            }
            txtNumeroSerie.setText(lam.getNumeroSerie());
            txtTema.setText(lam.getTema());
            txtMateria.setText(lam.getMateria());
        }

        Perecedero per = perecederoDAO.findByProductoId(productoEditId);
        if (per != null) {
            chkEsPerecedereo.setSelected(true);
            txtRefrigeracion.setText(per.getRefrigeracion() != null ? per.getRefrigeracion() : "");
            if (per.getFechaCaducidad() != null && !per.getFechaCaducidad().isEmpty()) {
                try {
                    dpCaducidad.setValue(LocalDate.parse(per.getFechaCaducidad()));
                } catch (Exception ignored) {}
            }
        }

        handleTipoChanged();
        btnGuardar.setText("Actualizar Producto");
        btnCancelar.setVisible(true);
    }

    @FXML
    private void handleEliminar() {
        Producto sel = tablaProductos.getSelectionModel().getSelectedItem();
        if (sel == null) {
            mostrarAlerta("Aviso", "Seleccione un producto de la tabla.");
            return;
        }

        if (confirmar("¿Eliminar el producto \"" + sel.getNombre() + "\"?\nSe eliminarán también sus datos de lámina o perecedero.")) {
            if (productoDAO.delete(sel.getIdProducto())) {
                cargarProductos();
            } else {
                mostrarAlerta("Error", "No se pudo eliminar. El producto puede tener ventas o entradas asociadas.");
            }
        }
    }

    @FXML
    private void handleCancelar() {
        productoEditId = null;
        limpiarFormulario();
        btnGuardar.setText("Registrar Producto");
        btnCancelar.setVisible(false);
    }

    @FXML
    private void handleBuscar() {
        String termino = txtBuscar.getText().trim();
        if (termino.isEmpty()) {
            cargarProductos();
        } else {
            tablaProductos.setItems(FXCollections.observableArrayList(productoDAO.buscarPorNombre(termino)));
        }
    }

    // ==================== HELPERS ====================

    private void cargarProductos() {
        tablaProductos.setItems(FXCollections.observableArrayList(productoDAO.findAll()));
    }

    private boolean guardarLamina(int idProducto) {
        String serie = txtNumeroSerie.getText().trim();
        String tema = txtTema.getText().trim();
        String materia = txtMateria.getText().trim();

        if (serie.isEmpty() || tema.isEmpty() || materia.isEmpty()) {
            mostrarAlerta("Error", "Complete todos los campos de lámina (serie, tema, materia).");
            return false;
        }

        Lamina lam = new Lamina();
        lam.setIdProducto(idProducto);
        lam.setIdMarca(cmbMarca.getValue() != null ? cmbMarca.getValue().getIdMarca() : null);
        lam.setNumeroSerie(serie);
        lam.setTema(tema);
        lam.setMateria(materia);
        return laminaDAO.create(lam);
    }

    private void guardarLaminaUpdate(int idProducto) {
        Lamina lam = new Lamina();
        lam.setIdProducto(idProducto);
        lam.setIdMarca(cmbMarca.getValue() != null ? cmbMarca.getValue().getIdMarca() : null);
        lam.setNumeroSerie(txtNumeroSerie.getText().trim());
        lam.setTema(txtTema.getText().trim());
        lam.setMateria(txtMateria.getText().trim());
        laminaDAO.update(lam);
    }

    private boolean guardarPerecedereo(int idProducto) {
        Perecedero per = new Perecedero();
        per.setIdProducto(idProducto);
        per.setRefrigeracion(txtRefrigeracion.getText().trim());
        per.setFechaCaducidad(dpCaducidad.getValue() != null ? dpCaducidad.getValue().toString() : null);
        return perecederoDAO.create(per);
    }

    private void guardarPerecedereoUpdate(int idProducto) {
        Perecedero per = new Perecedero();
        per.setIdProducto(idProducto);
        per.setRefrigeracion(txtRefrigeracion.getText().trim());
        per.setFechaCaducidad(dpCaducidad.getValue() != null ? dpCaducidad.getValue().toString() : null);
        perecederoDAO.update(per);
    }

    private void limpiarFormulario() {
        txtNombre.clear();
        cmbCategoria.setValue(null);
        txtUbicacion.clear();
        chkEsLamina.setSelected(false);
        chkEsPerecedereo.setSelected(false);
        panelLamina.setVisible(false);
        panelLamina.setManaged(false);
        panelPerecedereo.setVisible(false);
        panelPerecedereo.setManaged(false);
        cmbMarca.setValue(null);
        txtNumeroSerie.clear();
        txtTema.clear();
        txtMateria.clear();
        txtRefrigeracion.clear();
        dpCaducidad.setValue(null);
    }

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
