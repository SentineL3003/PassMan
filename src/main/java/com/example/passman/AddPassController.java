package com.example.passman;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.security.SecureRandom;

public class AddPassController {
    @FXML private TextField titleField;
    @FXML private TextField loginField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField repeatField;
    @FXML private ProgressBar qualityBar;
    @FXML private TextField urlField;

    @FXML private Button cancelbutton;

    @FXML private Label label;

    private MainController mainController;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    public void initialize() {
        // работа шкалы
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

    private void updateQualityBar(String password) {
        // надежность пароля
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
