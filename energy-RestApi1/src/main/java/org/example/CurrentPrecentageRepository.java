import org.springframework.stereotype.Repository;

@Repository
public class CurrentPercentageRepository {

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