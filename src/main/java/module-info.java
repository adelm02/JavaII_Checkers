module lab {
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;
    requires static lombok;
    requires java.logging;

    requires jakarta.persistence;
    requires jakarta.activation;
    requires eclipselink;
    requires java.sql;

    opens lab to javafx.base, eclipselink;
    exports lab;
}