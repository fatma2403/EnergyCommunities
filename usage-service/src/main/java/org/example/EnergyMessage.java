package org.example;

import java.time.LocalDateTime;

// Nachricht von Producer oder User (kommt aus der Queue).
// Gleicher Paketname wie im Producer- und User-Service, damit die JSON-Umwandlung passt.
public class EnergyMessage {

    private String type;          // "PRODUCER" oder "USER"
    private String association;   // "COMMUNITY" oder "GRID"
    private double kwh;
    private LocalDateTime datetime;

    public EnergyMessage() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAssociation() {
        return association;
    }

    public void setAssociation(String association) {
        this.association = association;
    }

    public double getKwh() {
        return kwh;
    }

    public void setKwh(double kwh) {
        this.kwh = kwh;
    }

    public LocalDateTime getDatetime() {
        return datetime;
    }

    public void setDatetime(LocalDateTime datetime) {
        this.datetime = datetime;
    }
}
