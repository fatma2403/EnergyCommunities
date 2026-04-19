package org.example.energygui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) {

        Button btnCurrent = new Button("Aktuelle Daten holen");
        Button btnHistory = new Button("Historische Daten holen");

        TextArea output = new TextArea();

        btnCurrent.setOnAction(e -> {
            String result = callAPI("http://localhost:8080/energy/current");
            String formatted = result
                    .replace("[", "")
                    .replace("]", "")
                    .replace("{", "")
                    .replace("}", "")
                    .replace("\"", "")
                    .replace(",", "\n")
                    .replace("hour:", "Stunde: ")
                    .replace("consumption:", "Verbrauch: ");

            output.setText(formatted);
        });

        btnHistory.setOnAction(e -> {
            String result = callAPI("http://localhost:8080/energy/history?day=2024");
            String formatted = result
                    .replace("[", "")
                    .replace("]", "")
                    .replace("{", "")
                    .replace("}", "")
                    .replace("\"", "")
                    .replace(",", "\n")
                    .replace("hour:", "Stunde: ")
                    .replace("consumption:", "Verbrauch: ");

            output.setText(formatted);
        });

        VBox root = new VBox(10, btnCurrent, btnHistory, output);

        Scene scene = new Scene(root, 400, 300);
        stage.setScene(scene);
        stage.setTitle("Energy App");
        stage.show();
    }

    private String callAPI(String urlString) {
        try {
            URL url = new URL(urlString);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(url.openStream())
            );

            String line;
            StringBuilder result = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                result.append(line);
            }

            return result.toString();

        } catch (Exception e) {
            return "Fehler: " + e.getMessage();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}