module com.example.compilador {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens com.example.ui to javafx.fxml;
    exports com.example.ui;
}