package org.example;

import org.springframework.stereotype.Repository;

public class CurrentPercentage {
    private String hour;
    private double communityDepleted;
    private double gridPortion;

    public CurrentPercentage(String hour, double communityDepleted, double gridPortion) {
        this.hour = hour;
        this.communityDepleted = communityDepleted;
        this.gridPortion = gridPortion;
    }

    public String getHour()              { return hour; }
    public double getCommunityDepleted() { return communityDepleted; }
    public double getGridPortion()       { return gridPortion; }

    public void setHour(String hour)                           { this.hour = hour; }
    public void setCommunityDepleted(double communityDepleted) { this.communityDepleted = communityDepleted; }
    public void setGridPortion(double gridPortion)             { this.gridPortion = gridPortion; }
    /*ss*/

    @Repository
    public static class CurrentPercentageRepository {

        private CurrentPercentage current;

        public CurrentPercentageRepository() {
            this.current = new CurrentPercentage("2025-01-10T14:00:00", 78.54, 7.23);
        }

        public CurrentPercentage findCurrent() {
            return current;
        }

        public CurrentPercentage save(CurrentPercentage cp) {
            this.current = cp;
            return this.current;
        }
    }
}