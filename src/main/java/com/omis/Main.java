package com.omis;

import com.omis.config.DatabaseManager;
import com.omis.service.AuthService;
import com.omis.util.ReportScheduler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Punto de entrada principal de la aplicación OMIS.
 * Inicializa JavaFX, la base de datos y gestiona el ciclo de vida de la app.
 */
public class Main extends Application {

    private static final String APP_TITLE = "OMIS - Iniciar Sesión";
    private static final double WINDOW_WIDTH = 1024;
    private static final double WINDOW_HEIGHT = 768;

    @Override
    public void init() {
        // Se ejecuta antes de start(), en hilo de fondo
        // 1. Inicializar la base de datos SQLite (crear tablas)
        DatabaseManager.getInstance().initDatabase();
        // 2. Asegurar que existe el usuario admin por defecto
        new AuthService().asegurarUsuarioAdmin();
        
        // 3. Iniciar el planificador de reportes
        ReportScheduler.iniciar();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Cargar la vista de Login
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/login.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT);

        // Cargar estilos CSS globales
        String css = getClass().getResource("/css/styles.css").toExternalForm();
        scene.getStylesheets().add(css);

        primaryStage.setTitle(APP_TITLE);
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);
        primaryStage.setMaximized(true);
        primaryStage.show();

        // Hook de cierre: generar reportes y cerrar BD
        primaryStage.setOnCloseRequest(event -> {
            System.out.println("Cerrando aplicación OMIS...");
            ReportScheduler.detener();
            DatabaseManager.getInstance().closeConnection();
        });
    }

    @Override
    public void stop() {
        // Último recurso para cerrar la conexión
        ReportScheduler.detener();
        DatabaseManager.getInstance().closeConnection();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
