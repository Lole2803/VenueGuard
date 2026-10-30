package zut.venueguard.model;

public class MainHall extends Venue {

    public MainHall(String name, int capacity, String location) {
        super(name, capacity, location);
    }

    @Override
    public int maxBookingHours() {
        return 8;
    }
}