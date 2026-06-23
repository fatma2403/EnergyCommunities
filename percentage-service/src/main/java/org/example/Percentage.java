package org.example;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

// Eine Zeile pro Stunde in der Tabelle "percentage".
@Entity
@Table(name = "percentage")
public class Percentage {

    @Id
    private LocalDateTime hour;

    private double communityDepleted; // wie viel Prozent der Community-Energie verbraucht wurde
    private double gridPortion;       // Anteil der Netz-Energie an der gesamten Nutzung in Prozent

    public Percentage() {
    }

    public Percentage(LocalDateTime hour, double communityDepleted, double gridPortion) {
        this.hour = hour;
        this.communityDepleted = communityDepleted;
        this.gridPortion = gridPortion;
    }

    public LocalDateTime getHour() {
        return hour;
    }

    public void setHour(LocalDateTime hour) {
        this.hour = hour;
    }

    public double getCommunityDepleted() {
        return communityDepleted;
    }

    public void setCommunityDepleted(double communityDepleted) {
        this.communityDepleted = communityDepleted;
    }

    public double getGridPortion() {
        return gridPortion;
    }

    public void setGridPortion(double gridPortion) {
        this.gridPortion = gridPortion;
    }
}
