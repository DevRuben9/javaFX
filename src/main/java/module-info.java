module com.mycompany.primerjavafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens com.mycompany.primerjavafx to javafx.fxml;
    exports com.mycompany.primerjavafx;
}
