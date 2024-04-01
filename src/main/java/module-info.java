module com.example {
    requires transitive javafx.controls;

    opens com.example to javafx.fxml;
    exports com.example;
}
