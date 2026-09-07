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

    //@fxml: sagt javafx dass ein element mit einer variable sich verbindetet
    @FXML private Label communityLabel; //der fxml label verbindet das label mit dem controller //communitylabel ist das textfeld in der gui
    @FXML private Label gridLabel; //der fxml label wird verbunden mit dem controller und gridlabel wird auf der gui angezeigt
    @FXML private Button refreshButton; //der fxml button wird mit dem controller verbunden

    @FXML private DatePicker startDatePicker; //der fxml datepicker wird mit dem controller verbunden
    @FXML private TextField startTimeField; //der fxml textfield wird mit dem controller verbunden
    @FXML private DatePicker endDatePicker;
    @FXML private TextField endTimeField;
    @FXML private Button showDataButton;

    @FXML private TableView<EnergyDataFX> historyTable; // der fxml tableview verbindet sich mit dem controller die daten sind energydatafx
    @FXML private TableColumn<EnergyDataFX, String> hourColumn; //der fxml tablecolumn also tabellenspalte verbindet sich mit dem controller //in der spalte hourcolumn wird string text aus energydatafx angezeigt
    @FXML private TableColumn<EnergyDataFX, Number> producedColumn; //der fxml tabellenspalte verbindet sich mit dem controller und in der spalte peroduktion wird eine zahl angezeigt aus energydatafx
    @FXML private TableColumn<EnergyDataFX, Number> usedColumn;
    @FXML private TableColumn<EnergyDataFX, Number> gridColumn;

    @FXML private Label producedTotalLabel;
    @FXML private Label usedTotalLabel;
    @FXML private Label gridTotalLabel;
    @FXML private Label statusLabel; //der fxml label verbindet sich mit dem controller

    private final RestClient restClient = new RestClient(); //ich erstelle einen restclient damit ich anfrage an die restapi sende und antworten zurück bekomme
    private final ObjectMapper mapper = new ObjectMapper(); //das verwende ich damit ich jason in javaobjekte umwandele und java objekte in jason
    private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_LOCAL_DATE_TIME; //da erselle ich ein datetimeformatter damit datum und uhrzeit richtig gespeichert werden

    @FXML //diese methode wird automatisch aufgerufen wenn die gui startet
    private void initialize() {
        hourColumn.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getHour())); //stundenspalte da lege ich fest welcher wert angezeigt wird  //C:zeile der tabelle //new simplestringproperty: der weert wird als text angezeigt// c.getvalue: ich hole die daten aus dieser zeile und dann die stunde aus diesen daten
        producedColumn.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getCommunityProduced())); //aus welcher tabelle kommt der wer? aus dem energydatafx objekt aber ursprünglich über die postgresql datenbank über die rest api
        usedColumn.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getCommunityUsed()));
        gridColumn.setCellValueFactory(c -> new SimpleDoubleProperty(c.getValue().getGridUsed()));

        refreshButton.setOnAction(e -> loadCurrent()); //beim klick auf den refreshbutton werden die aktuellen daten angezeigt //e: steht für den klickereignis
        showDataButton.setOnAction(e -> loadHistorical()); //beim klick auf showdata werden die historischen daten geladen und e steht für den klickereignis
    }

    private void loadCurrent() {
        try {
            String body = restClient.getCurrent(); //wir versuchen daten vom server zu holen uns sie als string text zurück zu geben
            if (body == null || body.isBlank() || body.equals("null")) {
                communityLabel.setText("- %");
                gridLabel.setText("- %");
                statusLabel.setText("Noch keine Daten vorhanden.");
                return; //dann hören wir einfach auf
            }
            Percentage p = mapper.readValue(body, Percentage.class); //wir lesen den wert und maachen aus jason text ein java objekt und nehmen den text und machen draus ein prozentwert

            communityLabel.setText(String.format(Locale.US, "%.2f %% used", p.getCommunityDepleted()));
            gridLabel.setText(String.format(Locale.US, "%.2f %%", p.getGridPortion()));
            statusLabel.setText("Aktuelle Werte geladen.");
        } catch (Exception ex) {
            statusLabel.setText("Fehler beim Laden: " + ex.getMessage()); //exception ex: es könnten mehrere fehler
        }
    }

    private void loadHistorical() {
        try {
            String start = startDatePicker.getValue().atTime(LocalTime.parse(startTimeField.getText())).format(ISO);
            String end = endDatePicker.getValue().atTime(LocalTime.parse(endTimeField.getText())).format(ISO);

            // Tabelle laden (unverändert)
            String body = restClient.getHistorical(start, end);
            HourlyUsage[] usages = mapper.readValue(body, HourlyUsage[].class);

            ObservableList<EnergyDataFX> rows = FXCollections.observableArrayList();
            for (HourlyUsage u : usages) {
                rows.add(new EnergyDataFX(u.getHour(), u.getCommunityProduced(),
                        u.getCommunityUsed(), u.getGridUsed()));
            }
            historyTable.setItems(rows);

            // Summen NICHT mehr selbst berechnen, sondern vom Backend holen.
            String summaryBody = restClient.getSummary(start, end);
            double[] sums = mapper.readValue(summaryBody, double[].class);

            producedTotalLabel.setText(String.format(Locale.US, "Community produced %.3f kWh", sums[0]));
            usedTotalLabel.setText(String.format(Locale.US, "Community used %.3f kWh", sums[1]));
            gridTotalLabel.setText(String.format(Locale.US, "Grid used %.3f kWh", sums[2]));

            statusLabel.setText(usages.length + " Stunden geladen.");
        } catch (Exception ex) {
            statusLabel.setText("Fehler beim Laden: " + ex.getMessage());
        }
    }
}