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
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

import java.io.IOException;

public class LoginController {

    @FXML private PasswordField passwordField;
    @FXML private Label messageLabel;
    @FXML private Button unlockButton;
    @FXML private Button exitButton;

    private int attemptLeft = 3;
    private boolean isBlocked = false;

    @FXML
    public void initialize() {
        if (passwordField != null) {

            if (isBlocked) return;

            passwordField.textProperty().addListener(((observableValue, oldValue, newValue) -> {
                boolean isEmpty = newValue == null || newValue.trim().isEmpty();
                if (unlockButton != null) {
                    unlockButton.setDisable(isEmpty);
                }
                if (messageLabel != null && newValue != null && !newValue.isEmpty()) {
                    messageLabel.setText("");
                }
            }));
            passwordField.setOnAction(event -> unblock());
        }
    }

    @FXML
    protected void unblock() {

        if (passwordField == null) return;

        String inputPassword = passwordField.getText();
        if (inputPassword == null || inputPassword.trim().isEmpty()) {
            return;
        }

        if (MasterPasswordManager.checkMaster(inputPassword)) {
            try {
                // Создаём ключ шифрования из мастер-пароля
                CryptoPass.initKey(inputPassword);
                openMainWindow();
            } catch (Exception e) {
                e.printStackTrace();
                if (messageLabel != null) {
                    messageLabel.setText("Ошибка инициализации шифрования");
                }
            }
        } else {
            attemptLeft--;
            if (attemptLeft <= 0) {
                blockUser();
            } else {
                if (messageLabel != null) {
                    messageLabel.setText("Неверный мастер-пароль! Осталось попыток: " + attemptLeft);
                }
            }
            passwordField.clear();
            passwordField.requestFocus();
        }
    }

    private void blockUser() {
        isBlocked = true;

        if (unlockButton != null) {
            unlockButton.setDisable(true);
        }
        if (passwordField != null) {
            passwordField.setDisable(true);
            passwordField.clear();
        }
        if (messageLabel != null) {
            messageLabel.setText("Превышено число попыток. Доступ заблокирован!");
        }
    }

    private void openMainWindow() {
        try {
            FXMLLoader fxmlloader = new FXMLLoader(getClass().getResource("main-view.fxml"));
            Parent root = fxmlloader.load();
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

    @FXML protected void exit() {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        if (stage != null)
            stage.close();
        Platform.exit();
    }
}
