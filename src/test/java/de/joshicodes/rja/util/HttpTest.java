package de.joshicodes.rja.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HttpTest {

    private final String REVOLT_API = "https://api.revolt.chat";

    @Test
    public void testHttp() {

        JsonObject object = HttpUtil.readJson(REVOLT_API);

        assertNotNull(object);
        assertNotNull(object.get("revolt"));

        JsonElement revoltElement = object.get("revolt");

        assertNotNull(revoltElement);
        assertTrue(revoltElement.isJsonPrimitive());  // String (Version) ("0.5.17")

    }

}
