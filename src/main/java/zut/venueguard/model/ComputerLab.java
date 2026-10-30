package zut.venueguard.model;

public class ComputerLab extends Venue {

    public ComputerLab(String name, int capacity, String location) {
        super(name, capacity, location);
    }

    @Override
    public int maxBookingHours() {
        return 3;
    }
}