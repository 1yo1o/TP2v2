module com.example.tp2v2 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.tp2v2 to javafx.fxml;
    exports com.example.tp2v2;
}