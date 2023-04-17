module com.example.tp2v2 {
    requires javafx.controls;
    requires javafx.fxml;


    opens client_fx to javafx.fxml;
    exports client_fx;
}