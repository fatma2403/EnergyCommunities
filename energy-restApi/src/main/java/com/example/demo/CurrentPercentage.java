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
}