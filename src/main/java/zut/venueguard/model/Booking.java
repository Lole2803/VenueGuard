package zut.venueguard.model;

public class Booking {
    private int id;
    private String venueName;
    private String bookerName;
    private String date;        // for example "2026-11-05"
    private int startHour;
    private int endHour;

    // Empty constructor: Javalin needs it to build a Booking from JSON
    public Booking() { }

    public Booking(int id, String venueName, String bookerName,
                   String date, int startHour, int endHour) {
        this.id = id;
        this.venueName = venueName;
        this.bookerName = bookerName;
        this.date = date;
        setStartHour(startHour);
        setEndHour(endHour);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getVenueName() { return venueName; }
    public void setVenueName(String venueName) { this.venueName = venueName; }

    public String getBookerName() { return bookerName; }
    public void setBookerName(String bookerName) { this.bookerName = bookerName; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public int getStartHour() { return startHour; }
    public void setStartHour(int startHour) {
        if (startHour < 0 || startHour > 23) {
            throw new IllegalArgumentException("Start hour must be between 0 and 23");
        }
        this.startHour = startHour;
    }

    public int getEndHour() { return endHour; }
    public void setEndHour(int endHour) {
        if (endHour < 1 || endHour > 24) {
            throw new IllegalArgumentException("End hour must be between 1 and 24");
        }
        this.endHour = endHour;
    }
}