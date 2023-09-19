package core;

import com.lsd.umc.script.ScriptInterface;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class EventLogger {

    private static final String FILE_PATH = "event_log.txt";

    public static void logEvent(ScriptInterface script) {
        try {
            String event = script.getEvent() + System.lineSeparator();
            Files.write(Paths.get(FILE_PATH), event.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Failed to write to event_log.txt: " + e.getMessage());
        }
    }
}

