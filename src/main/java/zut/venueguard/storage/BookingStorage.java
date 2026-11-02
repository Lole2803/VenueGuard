
package zut.venueguard.storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import zut.venueguard.model.Booking;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BookingStorage {

    private final File file;
    private final ObjectMapper mapper = new ObjectMapper();

    public BookingStorage(String path) {
        this.file = new File(path);
    }

    public List<Booking> load() {
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try {
            return mapper.readValue(
                file,
                new TypeReference<List<Booking>>() { }
            );
        } catch (IOException e) {
            System.out.println(
                "Could not read " + file + ": " + e.getMessage()
            );
            return new ArrayList<>();
        }
    }

    public void save(List<Booking> bookings) {
        try {
            File parent = file.getParentFile();

            if (parent != null) {
                parent.mkdirs();
            }

            mapper.writerWithDefaultPrettyPrinter()
                  .writeValue(file, bookings);

        } catch (IOException e) {
            System.out.println(
                "Could not save " + file + ": " + e.getMessage()
            );
        }
    }
}
