module com.symonssoftware.pumpkinbot {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.symonssoftware.pumpkinbot to javafx.fxml;
    exports com.symonssoftware.pumpkinbot;
}
