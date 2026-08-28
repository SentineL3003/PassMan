package com.example.passman;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.security.SecureRandom;

public class GeneratorController {
    @FXML private PasswordField passwordField;
    @FXML private Button useButton;
    @FXML private Button closeButton;
    @FXML private ProgressBar qualityBar;

    @FXML private TextField visibleTextField;
    @FXML private Button toggleEyeButton;
    private boolean isPasswordVisible = false;

    @FXML private Slider lengthSlider;
    @FXML private Spinner<Integer> lengthSpinner;

    // Переключатели типов символов
    @FXML private ToggleButton btnUpper;
    @FXML private ToggleButton btnLower;
    @FXML private ToggleButton btnDigits;
    @FXML private ToggleButton btnSpecial;

    private AddPassController addPassController;

    private Timeline strengthAnimation;
    private final DoubleProperty animatedProgress = new SimpleDoubleProperty(0);

    public void setAddPassController(AddPassController controller) {
        this.addPassController = controller;
    }

    @FXML
    public void initialize() {
        if (passwordField != null && visibleTextField != null) {
            passwordField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (!visibleTextField.getText().equals(newVal)) {
                    visibleTextField.setText(newVal);
                }
                updateQualityBar(newVal);
            });

            visibleTextField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (!passwordField.getText().equals(newVal)) {
                    passwordField.setText(newVal);
                }
                updateQualityBar(newVal);
            });
        }

        qualityBar.progressProperty().bind(animatedProgress);

        passwordField.textProperty().addListener((obs, oldVal, newVal) -> {
            updateQualityBar(newVal);
        });

        if (lengthSpinner != null && lengthSlider != null) {
            SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory =
                    new SpinnerValueFactory.IntegerSpinnerValueFactory(6, 64, 16);
            lengthSpinner.setValueFactory(valueFactory);

            // Двусторонняя синхронизация Slider <-> Spinner:
            // Изменение ползунка меняет значение в счётчике
            lengthSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
                lengthSpinner.getValueFactory().setValue(newVal.intValue());
            });

            // Изменение счётчика двигает ползунок
            lengthSpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    lengthSlider.setValue(newVal);
                }
            });
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
        int length = lengthSlider != null ? (int) lengthSlider.getValue() : 16;

        StringBuilder pool = new StringBuilder();
        StringBuilder guaranteedChars = new StringBuilder();

        if (btnUpper != null && btnUpper.isSelected()) {
            pool.append(UPPER);
            guaranteedChars.append(getRandomChar(UPPER));
        }
        if (btnLower != null && btnLower.isSelected()) {
            pool.append(LOWER);
            guaranteedChars.append(getRandomChar(LOWER));
        }
        if (btnDigits != null && btnDigits.isSelected()) {
            pool.append(DIGITS);
            guaranteedChars.append(getRandomChar(DIGITS));
        }
        if (btnSpecial != null && btnSpecial.isSelected()) {
            pool.append(SPECIAL);
            guaranteedChars.append(getRandomChar(SPECIAL));
        }

        // если всё отключено, то строчные вкл
        if (pool.length() == 0) {
            if (btnLower != null) btnLower.setSelected(true);
            pool.append(LOWER);
            guaranteedChars.append(getRandomChar(LOWER));
        }

        String poolChars = pool.toString();
        StringBuilder sb = new StringBuilder(length);

        sb.append(guaranteedChars);

        while (sb.length() < length) {
            sb.append(getRandomChar(poolChars));
        }

        shuffle(sb);

        String generated = sb.toString();
        if (passwordField != null) {
            passwordField.setText(generated);
        }
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

    @FXML
    private void toggleVisibility() {
        isPasswordVisible = !isPasswordVisible;

        if (isPasswordVisible) {
            // Копирование в TextField
            visibleTextField.setText(passwordField.getText());
            visibleTextField.setVisible(true);
            visibleTextField.setManaged(true);

            passwordField.setVisible(false);
            passwordField.setManaged(false);

            visibleTextField.requestFocus();
            visibleTextField.positionCaret(visibleTextField.getText().length());
        } else {
            // Копирование в PasswordField
            passwordField.setText(visibleTextField.getText());
            passwordField.setVisible(true);
            passwordField.setManaged(true);

            // Скрываем TextField
            visibleTextField.setVisible(false);
            visibleTextField.setManaged(false);

            passwordField.requestFocus();
            passwordField.positionCaret(passwordField.getText().length());
        }
    }

    private String getPassword() {
        if (isPasswordVisible && visibleTextField != null) {
            return visibleTextField.getText();
        } else if (passwordField != null) {
            return passwordField.getText();
        } else {
            return "";
        }
    }

    @FXML
    private void usePassword() {
        if (passwordField != null && addPassController != null) {
            addPassController.setGeneratedPassword(getPassword());
        }
        closeWindow();
    }

    private double calculateStrength(String password) {
        if (password == null || password.isEmpty()) return 0.0;
        int score = 0;

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

    private void copyToClipboard(String text) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(text);
        clipboard.setContent(content);
    }

    @FXML
    private void copyPassword() {
        if (passwordField != null && passwordField.getText() != null) {
            copyToClipboard(getPassword());
        }
    }

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        if (stage != null) {
            stage.close();
        }
    }
}
