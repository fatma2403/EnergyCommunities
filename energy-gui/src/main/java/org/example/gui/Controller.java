package org.example.gui;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Controller {

    // Oberer Bereich: aktuelle Werte
    @FXML private Label communityLabel;
    @FXML private Label gridLabel;
    @FXML private Button refreshButton;

    // Zeitfilter
    @FXML private DatePicker startDatePicker;
    @FXML private TextField startTimeField;
    @FXML private DatePicker endDatePicker;
    @FXML private TextField endTimeField;
    @FXML private Button showDataButton;

    // Tabelle mit den historischen Daten
    @FXML private TableView<EnergyDataFX> historyTable;
    @FXML private TableColumn<EnergyDataFX, String> hourColumn;
    @FXML private TableColumn<EnergyDataFX, Number> producedColumn;
    @FXML private TableColumn<EnergyDataFX, Number> usedColumn;
    @FXML private TableColumn<EnergyDataFX, Number> gridColumn;

    // Summen am unteren Rand
    @FXML private Label producedTotalLabel;
    @FXML private Label usedTotalLabel;
    @FXML private Label gridTotalLabel;
    @FXML private Label statusLabel;

    private final RestClient restClient = new RestClient();
    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @FXML
    private void initialize() {
        // Tabellen-Spalten mit den Werten verbinden.
        hourColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getHour()));
        producedColumn.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getCommunityProduced()));
        usedColumn.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getCommunityUsed()));
        gridColumn.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getGridUsed()));

        // Formatieren der double-Werte auf 3 Nachkommastellen
        producedColumn.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format(Locale.US, "%.3f", item.doubleValue()));
                }
            }
        });
        usedColumn.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format(Locale.US, "%.3f", item.doubleValue()));
                }
            }
        });
        gridColumn.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format(Locale.US, "%.3f", item.doubleValue()));
                }
            }
        });

        refreshButton.setOnAction(e -> loadCurrent());
        showDataButton.setOnAction(e -> loadHistorical());
    }

    // Holt die Prozentwerte der aktuellen Stunde.
    private void loadCurrent() {
        try {
            String body = restClient.getCurrent();
            if (body == null || body.isBlank() || body.equals("null")) {
                communityLabel.setText("- %");
                gridLabel.setText("- %");
                statusLabel.setText("Noch keine Daten vorhanden.");
                return;
            }
            JSONObject json = new JSONObject(body);
            double depleted = json.getDouble("communityDepleted");
            double grid = json.getDouble("gridPortion");

            communityLabel.setText(String.format(Locale.US, "%.2f %% used", depleted));
            gridLabel.setText(String.format(Locale.US, "%.2f %%", grid));
            statusLabel.setText("Aktuelle Werte geladen.");
        } catch (Exception ex) {
            statusLabel.setText("Fehler beim Laden: " + ex.getMessage());
        }
    }

    // Holt die historischen Stundenwerte fuer den gewaehlten Zeitraum.
    private void loadHistorical() {
        try {
            if (startDatePicker.getValue() == null || endDatePicker.getValue() == null) {
                statusLabel.setText("Bitte Start- und Enddatum auswählen!");
                return;
            }
            if (startTimeField.getText() == null || startTimeField.getText().isBlank()) {
                statusLabel.setText("Bitte Startzeit eingeben (z.B. 00:00:00)!");
                return;
            }
            if (endTimeField.getText() == null || endTimeField.getText().isBlank()) {
                statusLabel.setText("Bitte Endzeit eingeben (z.B. 23:00:00)!");
                return;
            }
            String start = startDatePicker.getValue().atTime(LocalTime.parse(startTimeField.getText())).format(ISO);
            String end = endDatePicker.getValue().atTime(LocalTime.parse(endTimeField.getText())).format(ISO);

            String body = restClient.getHistorical(start, end);
            JSONArray array = new JSONArray(body);

            ObservableList<EnergyDataFX> rows = FXCollections.observableArrayList();
            double sumProduced = 0;
            double sumUsed = 0;
            double sumGrid = 0;

            for (int i = 0; i < array.length(); i++) {
                JSONObject o = array.getJSONObject(i);
                String hour = o.getString("hour");
                double produced = o.getDouble("communityProduced");
                double used = o.getDouble("communityUsed");
                double grid = o.getDouble("gridUsed");

                rows.add(new EnergyDataFX(hour, produced, used, grid));
                sumProduced += produced;
                sumUsed += used;
                sumGrid += grid;
            }

            historyTable.setItems(rows);
            producedTotalLabel.setText(String.format(Locale.US, "Community produced %.3f kWh", sumProduced));
            usedTotalLabel.setText(String.format(Locale.US, "Community used %.3f kWh", sumUsed));
            gridTotalLabel.setText(String.format(Locale.US, "Grid used %.3f kWh", sumGrid));
            statusLabel.setText(array.length() + " Stunden geladen.");
        } catch (Exception ex) {
            statusLabel.setText("Fehler beim Laden: " + ex.getMessage());
        }
    }
}
