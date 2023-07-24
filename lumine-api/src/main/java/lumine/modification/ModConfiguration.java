package lumine.modification;

import com.google.gson.*;
import com.google.gson.annotations.SerializedName;
import lumine.util.GameProfile;
import lumine.util.IDKey;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class ModConfiguration {
    public static class ConfigValues {
        private static final List<String> REQUIRED_ARGS = Arrays.asList("version", "name", "package", "modClass");
        private HashMap<String, JsonElement> getDefaults() {
            return new HashMap<>() {{
                put("minVersion", new JsonPrimitive(GameProfile.instance().MINECRAFT_VERSION.getNumericVersion()));
                put("dependencies", new JsonArray());
            }};
        }

        // removable
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
        private String id;
        public int getMinVersion() {
            return minVersion;
        }
        public String[] getDependencies() {
            if (dependencies == null) dependencies = new String[0];
            return dependencies;
        }
        public String getId() {
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
            json = JsonParser.parseString(jsonStr).getAsJsonObject();
        } catch (JsonSyntaxException | JsonIOException | IllegalStateException | IOException e) {
            throw new IllegalArgumentException("Invalid configuration format: " + e.getMessage());
        }
        for (String arg : ConfigValues.REQUIRED_ARGS) {
            JsonElement element = json.get(arg);
            if (element == null) {
                throw new IllegalArgumentException("Invalid configuration format: missing removable argument " + arg);
            }
        }
    }
    public ConfigValues getValues() {
        return values;
    }
    public JsonObject getJson() {
        return json;
    }

    public String getValidId() {
        String baseId = this.getValues().getId().toLowerCase(Locale.ROOT);
        if (baseId.isEmpty()) {
            baseId = this.getValues().getName();
        }
        char[] chars = baseId.toCharArray();
        StringBuilder validId = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            String charString = c + "";
            Character finalChar = null;
            if (!charString.matches(IDKey.KEY_ALLOWED)) {
                if (validId.length() > 0 && validId.charAt(validId.length()-1) != '_') {
                    finalChar = '_';
                }
            } else {
                finalChar = c;
            }
            if (finalChar != null) {
                if (finalChar != '_' || (validId.length() > 0 && i != chars.length - 1)) {
                    validId.append(finalChar);
                }
            }
        }
        return validId.toString();
    }
    /*private Object getResultObject(JsonElement element) {
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
    }*/
}
