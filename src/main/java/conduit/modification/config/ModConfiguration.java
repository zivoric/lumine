package conduit.modification.config;

import com.google.gson.*;
import com.google.gson.annotations.SerializedName;

import java.io.*;

public class ModConfiguration {
    private static class ConfigValues {
        // required
        private String version;
        private String name;
        @SerializedName("package")
        private String pkg;
        private String modClass;
        // optional
        private int minVersion;
        private String[] dependencies;
        private String[] dependents;
        private String id;
    }
    private final ConfigValues values;
    public ModConfiguration(InputStream stream) {
        Gson gson = new GsonBuilder().create();
        Reader reader = new InputStreamReader(stream);
        try {
            values = gson.fromJson(reader, ConfigValues.class);
        } catch (JsonSyntaxException|JsonIOException e) {
            throw new IllegalArgumentException("Invalid configuration format");
        }
        if (values.version == null || values.name == null || values.pkg == null || values.modClass == null) {
            throw new IllegalArgumentException("Missing required configuration value");
        }
    }
}
