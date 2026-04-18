package org.example.energygui;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;


public class HelloController {
    @FXML
    private Label communityPool;

    @FXML
    private Label gridPool;

    @FXML
    private Label start;

    @FXML
    private Label end;

    @FXML
    private Label comminityProduced;

    @FXML
    private Label communityUsed;

    @FXML
    private Label gridUsed;

    @FXML
    private ComboBox<String> startComboBox;

    @FXML
    private ComboBox<String> endComboBox;


    @FXML
    protected void onButtonRefreshClick() {
    }

    @FXML
    protected void onButtonShowDataClick() {}

}

