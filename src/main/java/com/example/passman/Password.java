package com.example.passman;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Password {

    private final StringProperty title;
    private final StringProperty login;
    private final StringProperty password;
    private final StringProperty url;

    public Password (String title, String login, String password, String url) {
        this.title = new SimpleStringProperty(title);
        this.login = new SimpleStringProperty(login);
        this.password = new SimpleStringProperty(password);
        this.url = new SimpleStringProperty(url);
    }

    public String getTitle() { return title.get(); }
    public String getLogin() { return login.get(); }
    public String getPassword() { return password.get(); }
    public String getUrl() { return url.get(); }

    public void setTitle(String title) { this.title.set(title); }
    public void setLogin(String login) { this.login.set(login); }
    public void setPassword(String password) { this.password.set(password); }
    public void setUrl(String url) { this.url.set(url); }

    public StringProperty titleProperty() { return title; }
    public StringProperty loginProperty() { return login; }
    public StringProperty passwordProperty() { return password; }
    public StringProperty urlProperty() { return url; }
}
