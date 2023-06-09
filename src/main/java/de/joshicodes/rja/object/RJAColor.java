package de.joshicodes.rja.object;

import de.joshicodes.rja.util.ColorUtil;

import java.awt.*;

public class RJAColor {

    private final Color color;
    private boolean isGradient = false;
    private Color gradientColor;
    private int gradientAngle;
    private GradientType gradientType;

    public RJAColor(String color) {
        this(Color.WHITE);  // TODO: Parse Regex
    }

    /**
     * Creates a new EmbedColor with the given color.
     * @param color The color of the EmbedColor.
     */
    public RJAColor(Color color) {
        this.color = color;
    }

    /**
     * Creates a new EmbedColor with a gradient.
     * @param color The first color of the gradient.
     * @param gradientColor The second gradient color.
     * @param gradientAngle The angle of the gradient.
     * @param gradientType The type of the gradient.
     */
    public RJAColor(Color color, Color gradientColor, int gradientAngle, GradientType gradientType) {
        this.color = color;
        this.isGradient = true;
        this.gradientColor = gradientColor;
        this.gradientAngle = gradientAngle;
        this.gradientType = gradientType;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if(isGradient) {
            builder.append(gradientType.name().toLowerCase());
            builder.append("-gradient");
            builder.append("(");
            builder.append(ColorUtil.toHex(color));
            builder.append(" ");
            builder.append(gradientAngle != 0 ? gradientAngle + "%" : "0");
            builder.append(",");
            builder.append(ColorUtil.toHex(gradientColor));
            builder.append(")");
            return builder.toString();
        }
        return ColorUtil.toHex(color);
    }

    public static enum GradientType {
        LINEAR,
        CONIC,
        RADIAL
    }

}
