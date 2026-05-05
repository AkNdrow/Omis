package com.omis.controller;

import com.omis.dao.ProductoDAO;
import com.omis.dao.VentaDAO;
import com.omis.model.DetalleVenta;
import com.omis.model.Producto;
import com.omis.model.Venta;
import com.omis.service.ReporteService;
import com.omis.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador de la vista de Punto de Venta (POS).
 * Gestiona el carrito de compras, cálculo de cambio y registro de venta.
 */
public class VentaController {

    @FXML private Label lblFechaHora;
    @FXML private TextField txtBuscar;
    @FXML private TextField txtCantidadAdd;
    @FXML private TableView<Producto> tablaResultados;
    
    @FXML private Label lblSubtotal;
    @FXML private Label lblTotal;
    @FXML private TextField txtPago;
    @FXML private Label lblCambio;
    @FXML private Button btnCobrar;

    @FXML private TableView<DetalleVenta> tablaCarrito;

    private final ProductoDAO productoDAO = new ProductoDAO();
    private final VentaDAO ventaDAO = new VentaDAO();

    private final ObservableList<Producto> listaResultados = FXCollections.observableArrayList();
    private final ObservableList<DetalleVenta> carrito = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Reloj estático para la demostración
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        lblFechaHora.setText(dtf.format(LocalDateTime.now()));

        tablaResultados.setItems(listaResultados);
        tablaCarrito.setItems(carrito);

        // Listeners para cambio
        txtPago.textProperty().addListener((obs, old, val) -> calcularCambio());

        // Carga inicial
        cargarTodosLosProductos();
    }

    private void cargarTodosLosProductos() {
        listaResultados.setAll(productoDAO.findAll());
    }

    @FXML
    private void handleBuscar() {
        String termino = txtBuscar.getText().trim();
        if (termino.isEmpty()) {
            cargarTodosLosProductos();
        } else {
            listaResultados.setAll(productoDAO.buscarPorNombre(termino));
        }
    }

    @FXML
    private void handleAgregarSeleccionado() {
        Producto sel = tablaResultados.getSelectionModel().getSelectedItem();
        if (sel == null) {
            mostrarAlerta("Aviso", "Seleccione un producto de los resultados.");
            return;
        }

        int cant;
        try {
            cant = Integer.parseInt(txtCantidadAdd.getText().trim());
            if (cant <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "Cantidad inválida.");
            return;
        }

        if (cant > sel.getStockActual()) {
            mostrarAlerta("Stock Insuficiente", "Solo hay " + sel.getStockActual() + " unidades en stock.");
            return;
        }

        // Buscar si ya está en el carrito
        boolean encontrado = false;
        for (DetalleVenta dv : carrito) {
            if (dv.getIdProducto() == sel.getIdProducto()) {
                if (dv.getCantidad() + cant > sel.getStockActual()) {
                    mostrarAlerta("Stock Insuficiente", "La cantidad total excede el stock disponible.");
                    return;
                }
                dv.setCantidad(dv.getCantidad() + cant);
                encontrado = true;
                break;
            }
        }

        if (!encontrado) {
            DetalleVenta dv = new DetalleVenta();
            dv.setIdProducto(sel.getIdProducto());
            dv.setNombreProducto(sel.getNombre());
            dv.setCantidad(cant);
            dv.setPrecioUnitario(sel.getPrecioVenta());
            carrito.add(dv);
        }

        tablaCarrito.refresh(); // Para actualizar el subtotal en la vista
        actualizarTotales();
        
        // Limpiar búsqueda
        txtBuscar.clear();
        txtCantidadAdd.setText("1");
        cargarTodosLosProductos();
    }

    @FXML
    private void handleQuitarDelCarrito() {
        DetalleVenta sel = tablaCarrito.getSelectionModel().getSelectedItem();
        if (sel != null) {
            carrito.remove(sel);
            actualizarTotales();
        }
    }

    @FXML
    private void handleLimpiarCarrito() {
        carrito.clear();
        actualizarTotales();
    }

    @FXML
    private void handleAbrirBusquedaLaminas() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/laminas.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Búsqueda Avanzada de Láminas");
            stage.initModality(Modality.APPLICATION_MODAL);
            
            Scene scene = new Scene(root);
            String css = getClass().getResource("/css/styles.css").toExternalForm();
            scene.getStylesheets().add(css);
            stage.setScene(scene);

            LaminaController controller = loader.getController();

            // Mostrar el diálogo y esperar
            stage.showAndWait();

            // Al cerrar, verificar si seleccionó algo
            com.omis.model.Lamina laminaSel = controller.getLaminaSeleccionada();
            if (laminaSel != null) {
                agregarLaminaAlCarrito(laminaSel);
            }

        } catch (Exception e) {
            System.err.println("Error al abrir búsqueda de láminas: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void agregarLaminaAlCarrito(com.omis.model.Lamina lam) {
        // Buscar si ya está en el carrito
        boolean encontrado = false;
        for (DetalleVenta dv : carrito) {
            if (dv.getIdProducto() == lam.getIdProducto()) {
                if (dv.getCantidad() + 1 > lam.getStockActual()) {
                    mostrarAlerta("Stock Insuficiente", "No hay más stock disponible de esta lámina.");
                    return;
                }
                dv.setCantidad(dv.getCantidad() + 1);
                encontrado = true;
                break;
            }
        }

        if (!encontrado) {
            DetalleVenta dv = new DetalleVenta();
            dv.setIdProducto(lam.getIdProducto());
            dv.setNombreProducto(lam.getNombreProducto() + " (Serie: " + lam.getNumeroSerie() + ")");
            dv.setCantidad(1); // Por defecto agrega 1
            dv.setPrecioUnitario(lam.getPrecioVenta());
            carrito.add(dv);
        }

        tablaCarrito.refresh();
        actualizarTotales();
    }

    private void actualizarTotales() {
        double total = carrito.stream().mapToDouble(DetalleVenta::getSubtotal).sum();
        lblSubtotal.setText(String.format("$%.2f", total));
        lblTotal.setText(String.format("$%.2f", total));
        calcularCambio();
    }

    private void calcularCambio() {
        double total = carrito.stream().mapToDouble(DetalleVenta::getSubtotal).sum();
        try {
            double pago = Double.parseDouble(txtPago.getText().trim());
            double cambio = pago - total;
            if (cambio >= 0) {
                lblCambio.setText(String.format("$%.2f", cambio));
                lblCambio.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #4CAF50;");
                btnCobrar.setDisable(false);
            } else {
                lblCambio.setText("Falta pago");
                lblCambio.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #EF5350;");
                btnCobrar.setDisable(true);
            }
        } catch (NumberFormatException e) {
            lblCambio.setText("$0.00");
            btnCobrar.setDisable(true);
        }

        if (carrito.isEmpty()) {
            btnCobrar.setDisable(true);
        }
    }

    @FXML
    private void handleCobrar() {
        ejecutarCobro(false);
    }

    @FXML
    private void handleCobrarConTicket() {
        ejecutarCobro(true);
    }

    private void ejecutarCobro(boolean generarTicket) {
        if (carrito.isEmpty()) return;

        double total = carrito.stream().mapToDouble(DetalleVenta::getSubtotal).sum();

        Venta venta = new Venta();
        venta.setMontoTotal(total);
        venta.setIdUsuario(SessionManager.getInstance().getUsuarioActivo().getIdUsuario());

        List<DetalleVenta> listaDetalles = new ArrayList<>(carrito);
        int idVenta = ventaDAO.registrarVenta(venta, listaDetalles);

        if (idVenta > 0) {
            venta.setIdVenta(idVenta);
            String mensaje = "Se ha registrado la venta #" + idVenta + " correctamente.\nMonto Total: $" + String.format("%.2f", total);

            if (generarTicket) {
                boolean ticketOk = ReporteService.generarTicket(venta, listaDetalles);
                if (ticketOk) {
                    mensaje += "\n\nSe ha generado el ticket PDF en la carpeta Reportes.";
                } else {
                    mensaje += "\n\nError: No se pudo generar el ticket PDF.";
                }
            }

            mostrarAlerta("Venta Exitosa", mensaje);
            handleLimpiarCarrito();
            txtPago.clear();
            cargarTodosLosProductos(); // Actualizar stock visual
        } else {
            mostrarAlerta("Error", "No se pudo registrar la venta.");
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
