package de.joshicodes.rja.util;

import java.awt.*;

public class ColorUtil {

    public static String toHex(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }

}
