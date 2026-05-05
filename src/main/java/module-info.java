module com.omis {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.desktop;
    requires java.sql;
    requires org.xerial.sqlitejdbc;
    requires jbcrypt;
    requires org.apache.pdfbox;

    opens com.omis to javafx.fxml;
    opens com.omis.config to javafx.fxml;
    opens com.omis.controller to javafx.fxml;
    opens com.omis.model to javafx.base;

    exports com.omis;
    exports com.omis.config;
    exports com.omis.controller;
    exports com.omis.model;
    exports com.omis.dao;
    exports com.omis.service;
    exports com.omis.util;
}
