package org.example.gui;

import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Controller {

    @FXML private Label communityLabel;
    @FXML private Label gridLabel;
    @FXML private Button refreshButton;

    @FXML private DatePicker startDatePicker;
    @FXML private TextField startTimeField;
    @FXML private DatePicker endDatePicker;
    @FXML private TextField endTimeField;
    @FXML private Button showDataButton;

    @FXML private TableView<EnergyDataFX> historyTable;
    @FXML private TableColumn<EnergyDataFX, String> hourColumn;
    @FXML private TableColumn<EnergyDataFX, Number> producedColumn;
    @FXML private TableColumn<EnergyDataFX, Number> usedColumn;
    @FXML private TableColumn<EnergyDataFX, Number> gridColumn;

    @FXML private Label producedTotalLabel;
    @FXML private Label usedTotalLabel;
    @FXML private Label gridTotalLabel;
    @FXML private Label statusLabel;

    private final RestClient restClient = new RestClient();
    private final ObjectMapper mapper = new ObjectMapper();
    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @FXML
    private void initialize() {
        hourColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getHour()));
        producedColumn.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getCommunityProduced()));
        usedColumn.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getCommunityUsed()));
        gridColumn.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getGridUsed()));

        refreshButton.setOnAction(e -> loadCurrent());
        showDataButton.setOnAction(e -> loadHistorical());
    }

    private void loadCurrent() {
        try {
            String body = restClient.getCurrent();
            if (body == null || body.isBlank() || body.equals("null")) {
                communityLabel.setText("- %");
                gridLabel.setText("- %");
                statusLabel.setText("Noch keine Daten vorhanden.");
                return;
            }
            Percentage p = mapper.readValue(body, Percentage.class);

            communityLabel.setText(String.format(Locale.US, "%.2f %% used", p.getCommunityDepleted()));
            gridLabel.setText(String.format(Locale.US, "%.2f %%", p.getGridPortion()));
            statusLabel.setText("Aktuelle Werte geladen.");
        } catch (Exception ex) {
            statusLabel.setText("Fehler beim Laden: " + ex.getMessage());
        }
    }

    private void loadHistorical() {
        try {
            String start = startDatePicker.getValue().atTime(LocalTime.parse(startTimeField.getText())).format(ISO);
            String end = endDatePicker.getValue().atTime(LocalTime.parse(endTimeField.getText())).format(ISO);

            String body = restClient.getHistorical(start, end);
            HourlyUsage[] usages = mapper.readValue(body, HourlyUsage[].class);

            ObservableList<EnergyDataFX> rows = FXCollections.observableArrayList();

            for (HourlyUsage u : usages) {
                rows.add(new EnergyDataFX(u.getHour(), u.getCommunityProduced(),
                        u.getCommunityUsed(), u.getGridUsed()));
            }

            historyTable.setItems(rows);
            statusLabel.setText(usages.length + " Stunden geladen.");
        } catch (Exception ex) {
            statusLabel.setText("Fehler beim Laden: " + ex.getMessage());
        }
    }
}