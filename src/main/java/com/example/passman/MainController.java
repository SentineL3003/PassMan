package com.example.passman;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

import java.io.IOException;
import java.util.List;

public class MainController {

    @FXML private TableView<Password> passwordTable;
    @FXML private TableColumn<Password, String> colTitle;
    @FXML private TableColumn<Password, String> colLogin;
    @FXML private TableColumn<Password, String> colPass;
    @FXML private TableColumn<Password, String> colURL;

    @FXML private TextField searchField;

    private final ObservableList<Password> passwordList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colLogin.setCellValueFactory(new PropertyValueFactory<>("login"));
        colPass.setCellValueFactory(new PropertyValueFactory<>("password"));
        colURL.setCellValueFactory(new PropertyValueFactory<>("url"));

        List<Password> savedPasswords = PasswordStorageManager.loadPasswords();
        if (!savedPasswords.isEmpty()) {
            passwordList.addAll(savedPasswords);
        }

        passwordList.addListener((javafx.collections.ListChangeListener<Password>) change -> {
            PasswordStorageManager.savePasswords(passwordList);
        });

        FilteredList<Password> filteredData = new FilteredList<>(passwordList, p -> true);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(password -> {
                if (newValue == null || newValue.trim().isEmpty()) {
                    return true;
                }
                String filterText = newValue.toLowerCase().trim();

                // Поиск по названию сервиса
                if (password.getTitle() != null && password.getTitle().toLowerCase().contains(filterText)) {
                    return true;
                }
                // Поиск по логину
                if (password.getLogin() != null && password.getLogin().toLowerCase().contains(filterText)) {
                    return true;
                }
                // Поиск по URL
                if (password.getUrl() != null && password.getUrl().toLowerCase().contains(filterText)) {
                    return true;
                }

                return false;
            });
        });

        passwordTable.setItems(filteredData);
    }

    @FXML
    private Button exitButton;

    @FXML
    protected void openAddPassportView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("addPassword-view.fxml"));
            Parent root = loader.load();

            AddPassController addController = loader.getController();
            addController.setMainController(this);

            Stage stage = new Stage();
            stage.setTitle("Добавить запись");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setMinWidth(500);
            stage.setMinHeight(400);

            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    };

    public void addPassword(Password password) {
        if (password != null) {
            passwordList.add(password); // Таблица обновится автоматически
        }
    }

    @FXML
    protected void copyLogin() {
        Password selected = passwordTable.getSelectionModel().getSelectedItem();
        if (selected != null && selected.getLogin() != null) {
            copyToClipboard(selected.getLogin());
        }
    }

    @FXML
    protected void copyPass() {
        Password selected = passwordTable.getSelectionModel().getSelectedItem();
        if (selected != null && selected.getPassword() != null) {
            copyToClipboard(selected.getPassword());
        }
    }

    @FXML
    protected void deleteEntry() {
        Password selected = passwordTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            passwordList.remove(selected);
        }
    }

    @FXML
    protected void redactPass() {
        Password selected = passwordTable.getSelectionModel().getSelectedItem();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("addPassword-view.fxml"));
            Parent root = loader.load();

            AddPassController addController = loader.getController();
            addController.setMainController(this);
            addController.setPasswordToEdit(selected);

            Stage stage = new Stage();
            stage.setTitle("Редактировать запись");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setMinWidth(500);
            stage.setMinHeight(400);

            stage.showAndWait();
            passwordTable.refresh();
            PasswordStorageManager.savePasswords(passwordList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void copyToClipboard(String text) {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent content = new ClipboardContent();
        content.putString(text);
        clipboard.setContent(content);
    }

    @FXML
    protected void exit() {
        Stage stage = (Stage) exitButton.getScene().getWindow();
        stage.close();
        Platform.exit();   // полностью закрывает приложение
    }
}
