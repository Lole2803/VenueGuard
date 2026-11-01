package zut.venueguard.service;

import java.util.ArrayList;
import java.util.List;

import zut.venueguard.exception.VenueClashException;
import zut.venueguard.model.Booking;
import zut.venueguard.model.ComputerLab;
import zut.venueguard.model.MainHall;
import zut.venueguard.model.Venue;

public class BookingManager {

    private final List<Venue> venues = new ArrayList<>();
    private final List<Booking> bookings = new ArrayList<>();
    private int nextId = 1;

    public BookingManager() {
        venues.add(new MainHall("Main Hall", 300, "Main Block"));
        venues.add(new ComputerLab("ICE 1", 40, "ICE Building"));
        venues.add(new ComputerLab("ICE 2", 40, "ICE Building"));
    }

    public List<Venue> getVenues() {
        return new ArrayList<>(venues);
    }

    public List<Booking> getBookings() {
        return new ArrayList<>(bookings);
    }

    public void addBooking(Booking b) throws VenueClashException {
        checkBooking(b, -1);
        b.setId(nextId++);
        bookings.add(b);
    }

    public boolean updateBooking(int id, Booking updated) throws VenueClashException {
        Booking existing = findById(id);
        if (existing == null) {
            return false;
        }
        checkBooking(updated, id);
        existing.setVenueName(updated.getVenueName());
        existing.setBookerName(updated.getBookerName());
        existing.setDate(updated.getDate());
        existing.setStartHour(updated.getStartHour());
        existing.setEndHour(updated.getEndHour());
        return true;
    }

    public boolean cancelBooking(int id) {
        Booking existing = findById(id);
        if (existing == null) {
            return false;
        }
        bookings.remove(existing);
        return true;
    }

    private Booking findById(int id) {
        for (Booking b : bookings) {
            if (b.getId() == id) {
                return b;
            }
        }
        return null;
    }

    private Venue findVenue(String name) {
        for (Venue v : venues) {
            if (v.getName().equals(name)) {
                return v;
            }
        }
        return null;
    }

    // ignoreId: when updating, skip the booking being changed
    private void checkBooking(Booking b, int ignoreId) throws VenueClashException {
        if (b.getBookerName() == null || b.getBookerName().isBlank()) {
            throw new IllegalArgumentException("Booker name is required");
        }
        if (b.getDate() == null || b.getDate().isBlank()) {
            throw new IllegalArgumentException("Date is required");
        }
        if (b.getEndHour() <= b.getStartHour()) {
            throw new IllegalArgumentException("End hour must be after start hour");
        }
        Venue venue = findVenue(b.getVenueName());
        if (venue == null) {
            throw new IllegalArgumentException("Unknown venue: " + b.getVenueName());
        }

        // polymorphism: each type of venue gives its own limit
        int hours = b.getEndHour() - b.getStartHour();
        if (hours > venue.maxBookingHours()) {
            throw new VenueClashException(venue.getName() + " allows at most "
                    + venue.maxBookingHours() + " hours per booking");
        }

        for (Booking other : bookings) {
            if (other.getId() == ignoreId) {
                continue;
            }
            boolean sameVenue = other.getVenueName().equals(b.getVenueName());
            boolean sameDate = other.getDate().equals(b.getDate());
            boolean overlap = b.getStartHour() < other.getEndHour()
                    && other.getStartHour() < b.getEndHour();
            if (sameVenue && sameDate && overlap) {
                throw new VenueClashException(b.getVenueName() + " is already booked from "
                        + other.getStartHour() + " to " + other.getEndHour()
                        + " on " + b.getDate());
            }
        }
    }
}
