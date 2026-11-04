# VenueGuard

**A venue booking and clash tracker for Zambia University College of Technology.**

VenueGuard is a Java backend (Javalin) with a simple web page. It lets lecturers and class representatives book campus venues and stops two groups from being booked into the same room at the same time.

## The problem

Venues on campus, such as the Main Hall and the computer labs ICE 1 and ICE 2, are booked informally, through verbal agreements, paper timetables or messages. As a result, two lecturers or class groups often arrive for the same room at the same time. Time is lost while people argue, look for another room, or cancel the session.

## Who uses it

- **Lecturers and class representatives** book a venue for a session and need to know it is free.
- **Administrative staff** view all bookings and can change or cancel them.

In one sentence: lecturers and class representatives book venues and are stopped from double-booking a room.

## What it does

- Shows the venues, with their capacity and the maximum number of hours per booking.
- Books a venue for a date and a start and end hour.
- **Rejects a clash**: the same venue, on the same date, with overlapping hours.
- **Rejects a booking that is too long**: the Main Hall allows up to 8 hours, a computer lab up to 3 hours.
- Lets users edit and cancel a booking (with a confirmation pop-up).
- Saves bookings to a file, so they are still there after a restart.

## How to run

### You need

- JDK 17 or newer (`java -version`)
- Maven 3.9 or newer (`mvn -v`)
- Git (`git --version`)
- An internet connection the first time, so Maven can download its dependencies

### Steps

```
git clone https://github.com/Lole2803/VenueGuard.git
cd VenueGuard
mvn compile exec:java
```

When you see `Javalin started`, open **http://localhost:7070** in a browser.

To stop the server, press `Ctrl+C` in the terminal (on Windows, answer `Y` when asked).

Bookings are saved to `data/bookings.json`, which is created automatically on the first booking. The `data` folder is not stored in GitHub.

The page loads the SweetAlert2 library from the internet (a CDN) for the cancel pop-up. Without a connection, it falls back to the browser's own confirmation box.

## Classes

| Class | Responsibility |
|---|---|
| `Venue` (abstract) | Parent class. Holds a venue's name, capacity and location, and declares `maxBookingHours()`. |
| `MainHall` | Child of `Venue`. Large capacity, up to 8 hours per booking. |
| `ComputerLab` | Child of `Venue`. Used for ICE 1 and ICE 2, up to 3 hours per booking. |
| `Booking` | One reservation: id, venue name, booker name, date, start hour and end hour. Setters check the hours. |
| `BookingManager` | Keeps the venues and bookings in lists, adds, updates and cancels bookings, and does the clash check. |
| `VenueClashException` | Custom exception thrown when a booking clashes or is longer than the venue allows. |
| `BookingStorage` | Reads and writes the bookings to `data/bookings.json`. |
| `Main` | Starts Javalin, defines the endpoints and serves the web page. |

## How the OOP requirements are met

| Requirement | Where |
|---|---|
| Real classes | The classes above, each with one clear job |
| Encapsulation | `Venue` and `Booking` have private fields, with getters and setters. The `Venue` constructor rejects a non-positive capacity, and the `Booking` setters reject impossible hours |
| Inheritance | `MainHall` and `ComputerLab` extend the abstract class `Venue` |
| Polymorphism | `BookingManager` calls `venue.maxBookingHours()` on any `Venue`, and gets 8 for the Main Hall and 3 for a lab |
| Custom exception | `VenueClashException` is thrown in `BookingManager` and caught in `Main`, which answers with status 409 |
| Collections | `List<Venue>` and `List<Booking>` in `BookingManager`, and `List<Booking>` when saving and loading |
| Javalin endpoints | Five endpoints, listed below, using GET, POST, PUT and DELETE (plus a test endpoint) |
| Saving data | `BookingStorage` writes JSON to a file after every change and loads it at startup |
| Front end | `index.html`, `style.css` and `app.js`, served by Javalin, using `fetch` to call the endpoints |

### The clash rule

A new booking clashes with an existing one when it is for the **same venue**, on the **same date**, and the hours overlap: `newStart < oldEnd` and `oldStart < newEnd`. Hours are whole numbers from 0 to 24.

## REST API

| Method | Path | What it does | Status codes |
|---|---|---|---|
| GET | `/venues` | List the venues | 200 |
| GET | `/bookings` | List all bookings | 200 |
| POST | `/bookings` | Create a booking | 201 created, 400 invalid data, 409 clash or too long |
| PUT | `/bookings/{id}` | Update a booking | 200, 400, 404 not found, 409 |
| DELETE | `/bookings/{id}` | Cancel a booking | 204, 404 |

There is also `GET /hello`, a simple test endpoint.

Example request body for `POST /bookings`:

```json
{
  "venueName": "ICE 1",
  "bookerName": "Mr Banda",
  "date": "2026-11-05",
  "startHour": 9,
  "endHour": 11
}
```

## Project structure

```
VenueGuard/
|-- pom.xml
|-- README.md
|-- .gitignore
`-- src/main/
    |-- java/zut/venueguard/
    |   |-- Main.java
    |   |-- model/        Venue, MainHall, ComputerLab, Booking
    |   |-- exception/    VenueClashException
    |   |-- service/      BookingManager
    |   `-- storage/      BookingStorage
    `-- resources/public/ index.html, style.css, app.js
```

## Group members

| No. | Full name | Student number | GitHub account | Part |
|---|---|---|---|---|
| 1 | Loraine Nkuna | 2410028 | Lole2803 | `Venue`, `MainHall`, `ComputerLab` |
| 2 | Loveness Nanyinza | 2300847 | Loveness2003 | `BookingManager` and the clash check |
| 3 | Milika Muwaya | 2410072 | Milika-01 | `Booking`, `VenueClashException` |
| 4 | Niza Kowa | 2410271 | NizaKowa | The web page (HTML, CSS and JavaScript) |
| 5 | Denson Mumba | 2410189 | Denson-Jr | Javalin endpoints and saving to a file |

Built with Java, Javalin and Maven for the Object-Oriented Programming course group project.
