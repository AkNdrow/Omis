package com.omis.controller;

import com.omis.dao.LaminaDAO;
import com.omis.model.Lamina;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controlador de la búsqueda avanzada de láminas.
 */
public class LaminaController {

    @FXML private TextField txtBusqueda;
    @FXML private TextField txtMateria;
    @FXML private TextField txtSerie;
    @FXML private TableView<Lamina> tablaLaminas;

    private final LaminaDAO laminaDAO = new LaminaDAO();
    private final ObservableList<Lamina> resultados = FXCollections.observableArrayList();

    private Lamina laminaSeleccionada;

    @FXML
    public void initialize() {
        tablaLaminas.setItems(resultados);
        handleFiltrar();
    }

    @FXML
    private void handleFiltrar() {
        String temaOCve = txtBusqueda.getText().trim();
        String materia = txtMateria.getText().trim();
        String serie = txtSerie.getText().trim();

        resultados.setAll(laminaDAO.buscarAvanzado(temaOCve, materia, serie));
    }

    @FXML
    private void handleLimpiarFiltros() {
        txtBusqueda.clear();
        txtMateria.clear();
        txtSerie.clear();
        handleFiltrar();
    }

    @FXML
    private void handleAgregar() {
        laminaSeleccionada = tablaLaminas.getSelectionModel().getSelectedItem();
        if (laminaSeleccionada == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Atención");
            alert.setHeaderText(null);
            alert.setContentText("Seleccione una lámina de la tabla.");
            alert.showAndWait();
            return;
        }

        if (laminaSeleccionada.getStockActual() <= 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Sin Stock");
            alert.setHeaderText(null);
            alert.setContentText("La lámina seleccionada no tiene stock disponible.");
            alert.showAndWait();
            return;
        }

        cerrarVentana();
    }

    @FXML
    private void handleCerrar() {
        laminaSeleccionada = null;
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) txtBusqueda.getScene().getWindow();
        stage.close();
    }

    /**
     * Permite al VentaController obtener la lámina que el usuario seleccionó (si la hubo).
     */
    public Lamina getLaminaSeleccionada() {
        return laminaSeleccionada;
    }
}
