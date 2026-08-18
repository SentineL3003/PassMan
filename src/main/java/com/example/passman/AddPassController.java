package com.example.passman;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.security.SecureRandom;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.util.Duration;

public class AddPassController {
    @FXML private TextField titleField;
    @FXML private TextField loginField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField repeatField;
    @FXML private ProgressBar qualityBar;
    @FXML private TextField urlField;

    @FXML private Button cancelbutton;

    @FXML private Label label;

    private Timeline strengthAnimation;
    private final DoubleProperty animatedProgress = new SimpleDoubleProperty(0);

    private MainController mainController;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    public void initialize() {
        qualityBar.progressProperty().bind(animatedProgress);

        passwordField.textProperty().addListener((obs, oldVal, newVal) -> {
            updateQualityBar(newVal);
        });
    }

    @FXML
    private void addNewPassword() {
        String title = titleField.getText().trim();
        String login = loginField.getText().trim();
        String password = passwordField.getText().trim();
        String repeat = repeatField.getText().trim();
        String url = urlField.getText().trim();

        // проверка заполнения
        if (title.isEmpty()) {
            showError("Пожалуйста, введите название сервиса / сайта!");
            titleField.requestFocus();
        }

        if (login.isEmpty()) {
            showError("Пожалуйста, укажите логин!");
            loginField.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            showError("Пожалуйста, введите пароль!");
            passwordField.requestFocus();
            return;
        }

        // проверка совпадения
        if (!password.equals(repeat)) {
            showError("Пароли не совпадают! Проверьте повторный ввод.");
            repeatField.requestFocus();
            return;
        }

        if (editingPassword != null) {
            // Режим редактирования
            editingPassword.setTitle(title);
            editingPassword.setLogin(login);
            editingPassword.setPassword(password);
            editingPassword.setUrl(url);
        } else {
            // Режим создания нового пароля
            Password newPassword = new Password(title, login, password, url);
            if (mainController != null) {
                mainController.addPassword(newPassword);
            }
        }
        closeWindow();
    }

    // пароль редактирования
    private Password editingPassword;

    public void setPasswordToEdit(Password password) {
        this.editingPassword = password;

        if (password != null) {
            if (titleField != null)
                titleField.setText(password.getTitle());
            if (loginField != null)
                loginField.setText(password.getLogin());
            if (passwordField != null)
                passwordField.setText(password.getPassword());
            if (repeatField != null)
                repeatField.setText(password.getPassword());
            if (urlField != null)
                urlField.setText(password.getUrl());
        }
    }

    private static final SecureRandom RANDOM = new SecureRandom();
    // Наборы символов
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL = "!@#$%^&*()_+-=";
    private static final String ALL = UPPER + LOWER + DIGITS + SPECIAL;

    @FXML
    private void generatePassword() {
        int length = 16;
        StringBuilder sb = new StringBuilder(length);
        sb.append(getRandomChar(UPPER));
        sb.append(getRandomChar(LOWER));
        sb.append(getRandomChar(DIGITS));
        sb.append(getRandomChar(SPECIAL));

        for (int i = 4; i < length; i++) {
            sb.append(getRandomChar(ALL));
        }

        shuffle(sb);

        String generated = sb.toString();
        passwordField.setText(generated);
        repeatField.setText(generated);
    }

    private char getRandomChar(String charset) {
        return charset.charAt(RANDOM.nextInt(charset.length()));
    }

    private void shuffle(StringBuilder sb) {
        for (int i = sb.length() - 1; i > 0; i--) {
            int j = RANDOM.nextInt(i + 1);
            char temp = sb.charAt(i);
            sb.setCharAt(i, sb.charAt(j));
            sb.setCharAt(j, temp);
        }
    }

    // оценка надёжности пароля
    private double calculateStrength(String password) {
        if (password == null || password.isEmpty()) return 0.0;
        int score = 0;

        // оценка длины
        if (password.length() >= 8)  score++;
        if (password.length() >= 12) score++;
        if (password.length() >= 16) score++;

        if (password.matches(".*[a-z].*")) score++;
        if (password.matches(".*[A-Z].*")) score++;
        if (password.matches(".*\\d.*"))   score++;
        if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) score++;

        return score / 7.0;
    }

    private void updateQualityBar(String password) {
        double target = calculateStrength(password);
        String color;
        if (target == 0) {
            color = "#555555";
        } else if (target < 0.3) {
            color = "#e74c3c";
        } else if (target < 0.6) {
            color = "#e67e22";
        } else if (target < 0.85) {
            color = "#f1c40f";
        } else {
            color = "#2ecc71";
        }
        qualityBar.setStyle("-fx-accent: " + color + ";");

        if (strengthAnimation != null) {
            strengthAnimation.stop();
        }

        strengthAnimation = new Timeline(
                new KeyFrame(Duration.millis(250),
                        new KeyValue(animatedProgress, target))
        );
        strengthAnimation.play();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка ввода");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void cancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) titleField.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
    }
}
