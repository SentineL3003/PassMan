module com.example.passman {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires javafx.graphics;
    requires com.google.gson;
    requires jbcrypt;
    requires javafx.base;

    opens com.example.passman to javafx.fxml, com.google.gson;
    exports com.example.passman;
}