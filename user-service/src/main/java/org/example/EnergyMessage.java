package org.example;

import java.time.LocalDateTime;

// Nachricht, die ueber RabbitMQ verschickt wird.
// Muss im Producer, User und Usage Service gleich aufgebaut sein (gleicher Paketname),
// damit die JSON-Umwandlung in allen Diensten passt.
public class EnergyMessage {

    private String type;          // "PRODUCER" oder "USER"
    private String association;   // "COMMUNITY" oder "GRID"
    private double kwh;           // kWh in dieser Minute
    private LocalDateTime datetime;

    public EnergyMessage() {
    }

    public EnergyMessage(String type, String association, double kwh, LocalDateTime datetime) {
        this.type = type;
        this.association = association;
        this.kwh = kwh;
        this.datetime = datetime;
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

    @Override
    public String toString() {
        return "EnergyMessage{type=" + type + ", association=" + association
                + ", kwh=" + kwh + ", datetime=" + datetime + "}";
    }
}
