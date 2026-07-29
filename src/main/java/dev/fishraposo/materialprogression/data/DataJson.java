package dev.fishraposo.materialprogression.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

final class DataJson {
    private DataJson() {
    }

    static JsonObject object(Object... entries) {
        if (entries.length % 2 != 0) {
            throw new IllegalArgumentException("JSON objects require key-value pairs");
        }
        JsonObject object = new JsonObject();
        for (int index = 0; index < entries.length; index += 2) {
            object.add((String) entries[index], element(entries[index + 1]));
        }
        return object;
    }

    static JsonArray array(Object... values) {
        JsonArray array = new JsonArray();
        for (Object value : values) {
            array.add(element(value));
        }
        return array;
    }

    static JsonElement element(Object value) {
        if (value instanceof JsonElement json) {
            return json;
        }
        if (value instanceof String string) {
            return new JsonPrimitive(string);
        }
        if (value instanceof Number number) {
            return new JsonPrimitive(number);
        }
        if (value instanceof Boolean bool) {
            return new JsonPrimitive(bool);
        }
        throw new IllegalArgumentException("Unsupported JSON value " + value);
    }
}
