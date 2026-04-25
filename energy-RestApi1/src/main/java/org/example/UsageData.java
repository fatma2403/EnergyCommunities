package org.example;

public class UsageData {
    private String hour;
    private double communityProduced;
    private double communityUsed;
    private double gridUsed;

    public UsageData(String hour, double communityProduced, double communityUsed, double gridUsed) {
        this.hour = hour;
        this.communityProduced = communityProduced;
        this.communityUsed = communityUsed;
        this.gridUsed = gridUsed;
    }

    public String getHour()              { return hour; }
    public double getCommunityProduced() { return communityProduced; }
    public double getCommunityUsed()     { return communityUsed; }
    public double getGridUsed()          { return gridUsed; }

    public void setHour(String hour)                           { this.hour = hour; }
    public void setCommunityProduced(double communityProduced) { this.communityProduced = communityProduced; }
    public void setCommunityUsed(double communityUsed)         { this.communityUsed = communityUsed; }
    public void setGridUsed(double gridUsed)                   { this.gridUsed = gridUsed; }
}