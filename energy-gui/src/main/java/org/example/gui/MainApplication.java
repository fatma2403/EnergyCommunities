package org.example.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApplication extends Application { //sie erbt von application und das ist in javafx bibliothek //ich brauch sie damit meine klasse javafx anwendung wird

    @Override //das überschreibt eine bereits vorhandene methode
    public void start(Stage stage) throws Exception { //stage stage ist das hauptfenster meiner javafx anwendung //throws exception: sie kann fehler weitergeben also nach oben werfen
        FXMLLoader loader = new FXMLLoader(MainApplication.class.getResource("MainView.fxml")); //erstellt ein neues fxml loader und lade sie
        Scene scene = new Scene(loader.load()); //ich erstelle eine neue szene und lade die fxml datei hoch
        stage.setTitle("Energy Community Monitor"); //damit setze ich den fenster titel auf energy community monitor
        stage.setScene(scene); //ich füge die szene ins huaptfenster ein
        stage.show(); //das zeigt das hauptfenster an
    }

    public static void main(String[] args) { //Da startet mein server
        launch(args); //launch startet die javafx anwendung //arg mit optionalen startparameter
    }
}
