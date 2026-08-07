package com.example.passman;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.util.Arrays;

public class HelloController {

    @FXML
    private PasswordField masterField;

    @FXML
    private Button exitButton;

    @FXML
    private Button addMasterButton;

    @FXML
    public void initialize() {
        if (masterField != null) {
            masterField.textProperty().addListener((observable, oldValue, newValue) -> {
                boolean isEmpty = newValue == null || newValue.trim().isEmpty();
                if (addMasterButton != null) {
                    addMasterButton.setDisable(isEmpty);
                }
            });

            masterField.setOnAction(event -> addMaster());
        }
    }


    @FXML
    protected void addMaster() {
        if (masterField == null) return;

        String master = masterField.getText();
        if (master == null || master.trim().isEmpty()) {
            return;
        }

        MasterPasswordManager.saveMaster(master);

        openMainWindow();
    }

    // Открытие окна
    private void openMainWindow() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("main-view.fxml"));
            Parent root = fxmlLoader.load();

            Stage currentStage = (Stage) exitButton.getScene().getWindow();
            Stage mainStage = new Stage();
            mainStage.setTitle("PassMan — Менеджер паролей");
            mainStage.setScene(new Scene(root, 800, 650));
            mainStage.setMinWidth(700);
            mainStage.setMinHeight(500);

            mainStage.show();
            if (currentStage != null) {
                currentStage.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void exit() { // кнопка выход
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
        Platform.exit();
    }
}