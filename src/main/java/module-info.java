module lab {
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.fxml;
    requires static lombok;
    requires java.logging;

    requires jakarta.persistence;
    requires org.eclipse.persistence.jpa;
    requires java.sql;

    opens lab to javafx.base, eclipselink, jakarta.persistence;
    exports lab;
}