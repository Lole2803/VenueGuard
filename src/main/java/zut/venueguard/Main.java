
package zut.venueguard;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import zut.venueguard.exception.VenueClashException;
import zut.venueguard.model.Booking;
import zut.venueguard.service.BookingManager;
import zut.venueguard.storage.BookingStorage;

public class Main {
    public static void main(String[] args) {
        BookingManager manager = new BookingManager();

        // Load saved bookings when the application starts
        BookingStorage storage = new BookingStorage("data/bookings.json");
        manager.loadBookings(storage.load());

        Javalin app = Javalin.create(config -> {
            config.staticFiles.add("/public", Location.CLASSPATH);
        }).start(7070);

        app.get("/hello", ctx -> ctx.result("Hello, ZUT!"));

        app.get("/venues", ctx -> ctx.json(manager.getVenues()));
        app.get("/bookings", ctx -> ctx.json(manager.getBookings()));

        // CREATE
        app.post("/bookings", ctx -> {
            Booking b = ctx.bodyAsClass(Booking.class);
            try {
                manager.addBooking(b);
                storage.save(manager.getBookings());
                ctx.status(201).json(b);
            } catch (VenueClashException e) {
                ctx.status(409).result(e.getMessage());
            } catch (IllegalArgumentException e) {
                ctx.status(400).result(e.getMessage());
            }
        });

        // UPDATE
        app.put("/bookings/{id}", ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Booking updated = ctx.bodyAsClass(Booking.class);
            try {
                if (manager.updateBooking(id, updated)) {
                    storage.save(manager.getBookings());
                    updated.setId(id);
                    ctx.json(updated);
                } else {
                    ctx.status(404).result("Booking not found");
                }
            } catch (VenueClashException e) {
                ctx.status(409).result(e.getMessage());
            } catch (IllegalArgumentException e) {
                ctx.status(400).result(e.getMessage());
            }
        });

        // DELETE
        app.delete("/bookings/{id}", ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            if (manager.cancelBooking(id)) {
                storage.save(manager.getBookings());
                ctx.status(204);
            } else {
                ctx.status(404).result("Booking not found");
            }
        });
    }
}
