package lumine.config;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import java.io.*;
import java.util.Map;
import java.util.stream.Collectors;

public class FileConfiguration extends JsonConfiguration {
    private final File file;
    public FileConfiguration(File file, Map<String, ConfigEntry<?>> defaults, boolean readonly) throws IOException, IllegalArgumentException {
        super(readFromFile(file), defaults, readonly);
        this.file = file;
    }

    private static JsonObject readFromFile(File file) throws IOException, IllegalArgumentException {
        try {
            return readFromStream(new FileInputStream(file));
        } catch (SecurityException e) {
            throw new IOException("File not accessible", e);
        }
    }

    public boolean saveFile() {
        return super.saveToFile(file, true);
    }
}
