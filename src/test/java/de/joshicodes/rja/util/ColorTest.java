package de.joshicodes.rja.util;

import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ColorTest {

    @Test
    public void testColor() {
        Color color = new Color(255, 255, 255);
        assertEquals("#ffffff", ColorUtil.toHex(color));
    }

}
