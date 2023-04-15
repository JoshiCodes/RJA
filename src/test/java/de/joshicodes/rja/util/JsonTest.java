package de.joshicodes.rja.util;

import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JsonTest {

    @Test
    public void testJson() {
        JsonObject object = new JsonObject();
        object.addProperty("test", "test");
        object.addProperty("intTest", 2);
        object.addProperty("booleanTest", true);

        assertEquals("test", JsonUtil.getString(object, "test", null));
        assertEquals(2, JsonUtil.getInt(object, "intTest", 0));
        assertTrue(JsonUtil.getBoolean(object, "booleanTest", false));

        assertNull(JsonUtil.getString(object, "test2", null));

    }

}
