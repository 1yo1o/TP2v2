module com.example.tp2v2 {
    requires javafx.controls;
    requires javafx.fxml;


    opens client_fx to javafx.fxml;
    exports client_fx;
    opens client_simple;
    exports client_simple;
    opens server to javafx.base;
    exports server;
    exports server.models;
}