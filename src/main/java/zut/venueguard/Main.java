package zut.venueguard;

import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import zut.venueguard.exception.VenueClashException;
import zut.venueguard.model.Booking;
import zut.venueguard.service.BookingManager;

public class Main {
    public static void main(String[] args) {
        BookingManager manager = new BookingManager();

        Javalin app = Javalin.create(config -> {
            // serves the front end files in src/main/resources/public
            config.staticFiles.add("/public", Location.CLASSPATH);
        }).start(7070);

        app.get("/hello", ctx -> ctx.result("Hello, ZUT!"));

        // READ: list venues and bookings
        app.get("/venues", ctx -> ctx.json(manager.getVenues()));
        app.get("/bookings", ctx -> ctx.json(manager.getBookings()));

        // CREATE: add a booking
        app.post("/bookings", ctx -> {
            Booking b = ctx.bodyAsClass(Booking.class);
            try {
                manager.addBooking(b);
                ctx.status(201).json(b);
            } catch (VenueClashException e) {
                ctx.status(409).result(e.getMessage());
            } catch (IllegalArgumentException e) {
                ctx.status(400).result(e.getMessage());
            }
        });

        // UPDATE: change a booking
        app.put("/bookings/{id}", ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            Booking updated = ctx.bodyAsClass(Booking.class);
            try {
                if (manager.updateBooking(id, updated)) {
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

        // DELETE: cancel a booking
        app.delete("/bookings/{id}", ctx -> {
            int id = Integer.parseInt(ctx.pathParam("id"));
            if (manager.cancelBooking(id)) {
                ctx.status(204);
            } else {
                ctx.status(404).result("Booking not found");
            }
        });
    }
}