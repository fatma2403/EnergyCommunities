package org.example;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

// Liest die Tabelle "percentage", die der Percentage Service fuellt.
@Entity
@Table(name = "percentage")
public class Percentage {

    @Id
    private LocalDateTime hour;

    private double communityDepleted;
    private double gridPortion;

    public LocalDateTime getHour() {
        return hour;
    }

    public double getCommunityDepleted() {
        return communityDepleted;
    }

    public double getGridPortion() {
        return gridPortion;
    }
}
