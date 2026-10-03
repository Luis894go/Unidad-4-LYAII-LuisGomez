package com.luis.codelab;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.net.URL;

public class App extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        URL fxml = App.class.getResource("/com/luis/codelab/main.fxml");
        if (fxml == null) throw new RuntimeException("No se encontró main.fxml");
        Parent root = new FXMLLoader(fxml).load();
        Scene scene = new Scene(root, 1100, 660);
        URL css = App.class.getResource("/com/luis/codelab/style.css");
        if (css != null) scene.getStylesheets().add(css.toExternalForm());
        stage.setTitle("CODE OBJECT • Generación de Código Objeto");
        stage.setScene(scene);
        stage.setMinWidth(950);
        stage.setMinHeight(650);
        stage.centerOnScreen();
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
