module com.actm.actm {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;

    opens com.actt.actt to javafx.fxml;
    exports com.actt.actt;
}