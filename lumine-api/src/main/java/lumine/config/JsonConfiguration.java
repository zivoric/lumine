package lumine.config;

import com.google.gson.*;
import lumine.Lumine;
import lumine.config.type.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class JsonConfiguration {
    private final ConfigObject root;
    private final boolean readonly;
    public JsonConfiguration(JsonObject root, Map<String, ConfigEntry<?>> defaults, boolean readonly) {
        this.root = fromGsonObject(root, defaults);
        // TODO: add readonly/writable functionality to ConfigEntry objects
        this.readonly = readonly;
    }

    public JsonConfiguration(InputStream stream, Map<String, ConfigEntry<?>> defaults) throws IOException, IllegalArgumentException {
        this(readFromStream(stream), defaults, true);
    }

    public boolean saveToFile(File file, boolean overwrite) {
        try {
            if (readonly) {
                throw new UnsupportedOperationException("This configuration instance is readonly (the file must be changed directly)");
            }
            if (!file.getParentFile().exists() && !file.mkdirs()) {
                throw new IOException("Unable to create parent directories");
            }
            if (file.exists() && !overwrite) {
                throw new IOException("File already exists (overwrite set to false)");
            }
            file.createNewFile();
            PrintWriter writer = new PrintWriter(file);
            // should we validate JSON here?
            writer.write(root.toString());
            writer.close();
            return true;
        } catch (Exception e) {
            Lumine.getLogger().warn("Unable to write to file " + file.getAbsolutePath(), e);
            return false;
        }
    }

    public ConfigObject getRoot() {
        return root;
    }

    protected static JsonObject readFromStream(InputStream stream) throws IOException, IllegalArgumentException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        // TODO: write algorithm that uses JsonReader instead
        String contents;
        try {
            contents = reader.lines().collect(Collectors.joining());
        } catch (UncheckedIOException e) {
            throw e.getCause();
        }
        try {
            return JsonParser.parseString(contents).getAsJsonObject();
        } catch (JsonParseException e) {
            throw new IllegalArgumentException("Input file does not contain valid JSON");
        }
    }

    private static JsonObject toGsonObject(ConfigObject root) {
        return JsonParser.parseString(root.toString()).getAsJsonObject();
    }

    private static ConfigObject fromGsonObject(JsonObject object, Map<String, ConfigEntry<?>> defaults) {
        TypeObject type = (TypeObject) getElementType(object);
        Map<String, ConfigEntry<?>> entries = new HashMap<>();
        for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
            JsonElement element = entry.getValue();
            if (element instanceof JsonObject obj) {
                try {
                    ConfigObject sub = defaults.get(entry.getKey()).asObject();
                    entries.put(entry.getKey(), fromGsonObject(obj, sub.asMap()));
                } catch (NullPointerException e) {
                    throw new IllegalArgumentException("Defaults map does not contain value for key '" + entry.getKey() + "'");
                } catch (Exception e) {
                    throw new IllegalArgumentException("Defaults map value for key '" + entry.getKey() + "' does not match", e);
                }
            } else {
                entries.put(entry.getKey(), fromElement(element));
            }
        }
        return new ConfigObject(entries, defaults);
    }

    private static ConfigEntry<?> fromElement(JsonElement element) {
        TypeAny type = getElementType(element);
        if (type instanceof TypeObject objType) {
            Map<String, ConfigEntry<?>> entries = new HashMap<>();
            for (Map.Entry<String, JsonElement> entry : element.getAsJsonObject().entrySet()) {
                entries.put(entry.getKey(), fromElement(entry.getValue()));
            }
            return new ConfigObject(entries);
        } else if (type instanceof TypeArray<?> arrType) {
            List<ConfigEntry<?>> entries = new ArrayList<>();
            for (JsonElement el : element.getAsJsonArray()) {
                entries.add(fromElement(el));
            }
            return new ConfigArray<>(arrType, entries);
        } else if (type instanceof TypePrimitive) {
            if (type instanceof TypeNumber) {
                return new ConfigNumber(element.getAsNumber());
            } else if (type instanceof TypeBoolean) {
                return new ConfigBoolean(element.getAsBoolean());
            } else {
                return new ConfigString(element.getAsString());
            }
        }
        return new ConfigNull();
    }

    private static TypeAny getElementType(JsonElement element) {
        if (element instanceof JsonObject obj) {
            HashMap<String, TypeAny> types = new HashMap<>();
            for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                types.put(entry.getKey(), getElementType(entry.getValue()));
            }
            return new TypeObject(types);
        } else if (element instanceof JsonArray arr) {
            TypeAny type = null;
            for (JsonElement val : arr) {
                TypeAny candidate = getElementType(val);
                if (type == null) {
                    type = candidate;
                } else if (type.getClass().equals(TypeAny.class)) {
                    break;
                } else if (!type.canRetrieveFrom(candidate)) {
                    if (type instanceof TypePrimitive && candidate instanceof TypePrimitive) {
                        type = new TypePrimitive();
                    } else if (candidate.canRetrieveFrom(type)) {
                        type = candidate;
                    } else {
                        type = new TypeAny();
                    }
                } else if ((type instanceof TypeObject && candidate instanceof TypeObject) ||
                            (type instanceof TypeArray && candidate instanceof TypeArray)) {
                    if (!type.equals(candidate)) {
                        type = new TypeAny();
                    }
                }
            }
            return new TypeArray<>(type == null ? new TypeAny() : type);
        } else if (element instanceof JsonNull) {
            return new TypeNull();
        } else if (element instanceof JsonPrimitive pr) {
            if (pr.isNumber()) {
                return new TypeNumber();
            } else if (pr.isBoolean()) {
                return new TypeBoolean();
            } else {
                // String must be after all other types since it can convert any type
                return new TypeString();
            }
        }
        throw new UnsupportedOperationException("Provided element cannot be matched with a config type");
    }
}
