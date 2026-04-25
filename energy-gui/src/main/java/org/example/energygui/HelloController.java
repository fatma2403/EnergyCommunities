package org.example.energygui;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class HelloController {

    private static final String BASE_URL = "http://localhost:8080";
    private static final DateTimeFormatter ISO_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    // ── FXML fields matching fx:id values in hello-view.fxml ──

    @FXML
    private Label communityPool;

    @FXML
    private Label gridPool;

    @FXML
    private Label comminityProduced;

    @FXML
    private Label communityUsed;

    @FXML
    private Label gridUsed;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    protected void onButtonRefreshClick() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(BASE_URL + "/energy/current"))
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                JsonNode node = objectMapper.readTree(response.body());
                double depleted = node.get("communityDepleted").asDouble();
                double gridPortion = node.get("gridPortion").asDouble();

                Platform.runLater(() -> {
                    communityPool.setText(String.format("%.2f%% used", depleted));
                    gridPool.setText(String.format("%.2f%%", gridPortion));
                });
                return null;
            }
        };

        task.setOnFailed(e -> Platform.runLater(() -> {
            String errorMsg = "Error: " + task.getException().getMessage();
            communityPool.setText(errorMsg);
            gridPool.setText(errorMsg);
        }));

        new Thread(task).start();
    }

    @FXML
    protected void onButtonShowDataClick() {
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();

        if (startDate == null || endDate == null) {
            comminityProduced.setText("Please select both dates.");
            return;
        }

        String startValue = startDate.atStartOfDay().format(ISO_FMT);
        String endValue = endDate.atStartOfDay().format(ISO_FMT);

        String startEncoded = URLEncoder.encode(startValue, StandardCharsets.UTF_8);
        String endEncoded = URLEncoder.encode(endValue, StandardCharsets.UTF_8);

        String url = BASE_URL + "/energy/historical?start=" + startEncoded + "&end=" + endEncoded;

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                JsonNode array = objectMapper.readTree(response.body());
                double totalProduced = 0, totalUsed = 0, totalGrid = 0;

                for (JsonNode entry : array) {
                    totalProduced += entry.get("communityProduced").asDouble();
                    totalUsed += entry.get("communityUsed").asDouble();
                    totalGrid += entry.get("gridUsed").asDouble();
                }

                final double fp = totalProduced, fu = totalUsed, fg = totalGrid;

                Platform.runLater(() -> {
                    comminityProduced.setText(String.format("%.3f kWh", fp));
                    communityUsed.setText(String.format("%.3f kWh", fu));
                    gridUsed.setText(String.format("%.3f kWh", fg));
                });
                return null;
            }
        };

        task.setOnFailed(e -> Platform.runLater(() -> {
            String errorMsg = "Error: " + task.getException().getMessage();
            comminityProduced.setText(errorMsg);
            communityUsed.setText(errorMsg);
            gridUsed.setText(errorMsg);
        }));

        new Thread(task).start();
    }
}
