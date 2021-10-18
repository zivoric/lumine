package conduit.modification.config;

import com.google.gson.*;
import com.google.gson.annotations.SerializedName;
import com.google.gson.stream.JsonReader;
import conduit.Conduit;

import java.io.*;
import java.util.*;

public class ModConfiguration {
    private static final List<String> REQUIRED_ARGS = Arrays.asList("version", "name", "package", "modClass");
    public static class ConfigValues {
        // required
        private String version;
        private String name;
        @SerializedName("package")
        private String pkg;
        private String modClass;
        public String getVersion() {
            return version;
        }
        public String getName() {
            return name;
        }
        public String getPackage() {
            return pkg;
        }
        public String getModClass() {
            return modClass;
        }
        // optional
        private int minVersion;
        private String[] dependencies;
        // removed dependents for now, makes things unnecessarily complicated
        //private String[] dependents;
        private String id;
        public int getMinVersion() {
            return minVersion;
        }
        public String[] getDependencies() {
            if (dependencies == null) dependencies = new String[0];
            return dependencies;
        }
        /*public String[] getDependents() {
            if (dependents == null) dependents = new String[0];
            return dependents;
        }*/
        public String getId() {
            if (id == null) id = "";
            return id;
        }
    }
    private final ConfigValues values;
    private final JsonObject json;
    public ModConfiguration(InputStream stream) throws IllegalArgumentException {
        Gson gson = new GsonBuilder().create();
        try {
            String jsonStr = new String(stream.readAllBytes());
            values = gson.fromJson(jsonStr, ConfigValues.class);
            json = new JsonParser().parse(jsonStr).getAsJsonObject();
        } catch (JsonSyntaxException | JsonIOException | IllegalStateException | IOException e) {
            throw new IllegalArgumentException("Invalid configuration format: " + e.getMessage());
        }
        for (String arg : REQUIRED_ARGS) {
            JsonElement element = json.get(arg);
            if (element == null) {
                throw new IllegalArgumentException("Invalid configuration format: missing required argument " + arg);
            }
        }
    }
    public ConfigValues getValues() {
        return values;
    }
    public JsonObject getJson() {
        return json;
    }
    private Object getResultObject(JsonElement element) {
        if (element == null) {
            return null;
        }
        if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            List<Object> obj = new ArrayList<>();
            array.forEach(el -> obj.add(getResultObject(el)));
            return obj;
        } else if (element.isJsonObject()) {
            JsonObject jsonObj = element.getAsJsonObject();
            Map<String, Object> obj = new HashMap<>();
            jsonObj.entrySet().forEach(entry -> obj.put(entry.getKey(), getResultObject(entry.getValue())));
            return obj;
        } else if (element.isJsonPrimitive()) {
            JsonPrimitive primitive = element.getAsJsonPrimitive();
            if (primitive.isString()) {
                return primitive.getAsString();
            } else if (primitive.isNumber()) {
                return primitive.getAsNumber();
            } else if (primitive.isBoolean()) {
                return primitive.getAsBoolean();
            }
        }
        return null;
    }
}
