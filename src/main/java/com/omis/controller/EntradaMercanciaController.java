package com.omis.controller;

import com.omis.dao.EntradaMercanciaDAO;
import com.omis.dao.ProductoDAO;
import com.omis.dao.ProveedorDAO;
import com.omis.model.*;
import com.omis.service.PrecioService;
import com.omis.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Controlador de la vista de Entrada de Mercancía.
 * Gestiona la creación de lotes con múltiples productos,
 * cálculo de precios en tiempo real y registro atómico.
 */
public class EntradaMercanciaController {

    @FXML private ComboBox<Proveedor> cmbProveedor;
    @FXML private TextField txtObservaciones;
    @FXML private ComboBox<Producto> cmbProducto;
    @FXML private TextField txtCantidad;
    @FXML private TextField txtCostoUnitario;
    @FXML private TextField txtMargen;
    @FXML private Label lblPrecioCalc;
    @FXML private Label lblTotalLote;
    @FXML private TableView<DetalleEntrada> tablaDetalles;
    @FXML private TableView<EntradaMercancia> tablaHistorial;

    private final ProveedorDAO proveedorDAO = new ProveedorDAO();
    private final ProductoDAO productoDAO = new ProductoDAO();
    private final EntradaMercanciaDAO entradaDAO = new EntradaMercanciaDAO();

    private final ObservableList<DetalleEntrada> detallesLote = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Cargar combos
        cmbProveedor.setItems(FXCollections.observableArrayList(proveedorDAO.findAll()));
        cmbProducto.setItems(FXCollections.observableArrayList(productoDAO.findAll()));

        // Vincular tabla de detalles
        tablaDetalles.setItems(detallesLote);

        // Cargar historial
        cargarHistorial();

        // Listener para calcular precio en tiempo real
        txtCostoUnitario.textProperty().addListener((obs, old, val) -> calcularPrecio());
        txtMargen.textProperty().addListener((obs, old, val) -> calcularPrecio());
    }

    /**
     * Calcula y muestra el precio de venta en tiempo real.
     */
    private void calcularPrecio() {
        try {
            double costo = parseDouble(txtCostoUnitario.getText());
            double margen = parseDouble(txtMargen.getText());
            double precio = PrecioService.calcularPrecioVenta(costo, margen);
            lblPrecioCalc.setText(String.format("$%.2f", precio));
        } catch (Exception e) {
            lblPrecioCalc.setText("$0.00");
        }
    }

    /**
     * Agrega un producto al lote actual.
     */
    @FXML
    private void handleAgregarDetalle() {
        Producto prodSel = cmbProducto.getValue();
        if (prodSel == null) {
            mostrarAlerta("Error", "Seleccione un producto.");
            return;
        }

        int cantidad;
        double costoUnit;
        double margen;

        try {
            cantidad = Integer.parseInt(txtCantidad.getText().trim());
            if (cantidad <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Ingrese una cantidad válida (número entero positivo).");
            return;
        }

        try {
            costoUnit = parseDouble(txtCostoUnitario.getText());
            if (costoUnit <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Ingrese un costo unitario válido.");
            return;
        }

        try {
            margen = parseDouble(txtMargen.getText());
        } catch (NumberFormatException e) {
            margen = 0;
        }

        double precioCalc = PrecioService.calcularPrecioVenta(costoUnit, margen);

        // Crear detalle
        DetalleEntrada det = new DetalleEntrada();
        det.setIdProducto(prodSel.getIdProducto());
        det.setNombreProducto(prodSel.getNombre());
        det.setCantidadRecibida(cantidad);
        det.setCostoUnitario(costoUnit);
        det.setPrecioCalculado(Math.round(precioCalc * 100.0) / 100.0);

        detallesLote.add(det);
        actualizarTotal();

        // Limpiar campos para siguiente producto
        cmbProducto.setValue(null);
        txtCantidad.clear();
        txtCostoUnitario.clear();
        txtMargen.clear();
        lblPrecioCalc.setText("$0.00");
    }

    /**
     * Quita el detalle seleccionado del lote.
     */
    @FXML
    private void handleQuitarDetalle() {
        DetalleEntrada sel = tablaDetalles.getSelectionModel().getSelectedItem();
        if (sel != null) {
            detallesLote.remove(sel);
            actualizarTotal();
        }
    }

    /**
     * Registra la entrada completa (cabecera + detalles + stock + precios).
     */
    @FXML
    private void handleRegistrarEntrada() {
        Proveedor provSel = cmbProveedor.getValue();
        if (provSel == null) {
            mostrarAlerta("Error", "Seleccione un proveedor.");
            return;
        }

        if (detallesLote.isEmpty()) {
            mostrarAlerta("Error", "Agregue al menos un producto al lote.");
            return;
        }

        // Construir cabecera
        EntradaMercancia entrada = new EntradaMercancia();
        entrada.setIdProveedor(provSel.getIdProveedor());
        entrada.setIdUsuario(SessionManager.getInstance().getUsuarioActivo().getIdUsuario());
        entrada.setObservaciones(txtObservaciones.getText().trim());

        // Registrar con transacción atómica
        List<DetalleEntrada> listaDetalles = new ArrayList<>(detallesLote);
        int idEntrada = entradaDAO.registrarEntrada(entrada, listaDetalles);

        if (idEntrada > 0) {
            mostrarAlerta("Éxito", "Entrada #" + idEntrada + " registrada correctamente.\n"
                    + listaDetalles.size() + " producto(s) actualizados en inventario.");

            // Limpiar todo
            detallesLote.clear();
            cmbProveedor.setValue(null);
            txtObservaciones.clear();
            actualizarTotal();
            cargarHistorial();

            // Recargar combo de productos (stock actualizado)
            cmbProducto.setItems(FXCollections.observableArrayList(productoDAO.findAll()));
        } else {
            mostrarAlerta("Error", "No se pudo registrar la entrada. La transacción fue revertida.");
        }
    }

    private void actualizarTotal() {
        double total = detallesLote.stream()
                .mapToDouble(DetalleEntrada::getSubtotal)
                .sum();
        lblTotalLote.setText(String.format("$%.2f", total));
    }

    private void cargarHistorial() {
        tablaHistorial.setItems(FXCollections.observableArrayList(entradaDAO.findAll()));
    }

    private double parseDouble(String text) {
        if (text == null || text.trim().isEmpty()) return 0;
        return Double.parseDouble(text.trim());
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
