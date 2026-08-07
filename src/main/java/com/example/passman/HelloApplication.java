package com.example.passman;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        boolean isFirstRun = !MasterPasswordManager.hasMaster();

        String fxmlFile;
        if (isFirstRun)
            fxmlFile = "hello-view.fxml";
        else
            fxmlFile = "login-view.fxml";

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(fxmlFile));
        Scene scene = new Scene(fxmlLoader.load(), 500, 600);
        stage.setTitle("PassMan");
        stage.setScene(scene);

        stage.setMinWidth(500);
        stage.setMinHeight(600);

        stage.show();
    }
}
