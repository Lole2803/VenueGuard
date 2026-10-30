package zut.venueguard.model;

public abstract class Venue {
    private final String name;
    private final int capacity;
    private final String location;

    public Venue(String name, int capacity, String location) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        this.name = name;
        this.capacity = capacity;
        this.location = location;
    }

    public String getName() { return name; }
    public int getCapacity() { return capacity; }
    public String getLocation() { return location; }

    // Each type of venue decides its own limit
    public abstract int maxBookingHours();
}