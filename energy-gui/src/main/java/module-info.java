module org.example.energygui {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.energygui to javafx.fxml;
    exports org.example.energygui;
}