module checkers.api {
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires jakarta.persistence;
    requires jakarta.validation;
    requires java.net.http;
    requires javafx.base;
    requires javafx.controls;
    requires javafx.graphics;
    requires static lombok;
    requires spring.boot;
    requires spring.boot.autoconfigure;
    requires spring.context;
    requires spring.data.commons;
    requires spring.data.jpa;
    requires spring.web;

    requires org.hibernate.orm.core;

    exports cz.vsb.checkers;
    exports lab;

    opens lab;
    opens cz.vsb.checkers;
    opens cz.vsb.checkers.api.web;
}