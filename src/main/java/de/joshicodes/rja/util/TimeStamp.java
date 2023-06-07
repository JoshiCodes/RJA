package de.joshicodes.rja.util;

public class TimeStamp {

    private final long time;
    private final Format format;

    /**
     * Creates a new TimeStamp with the current time and no format.
     * @param time The time in milliseconds since the epoch.
     */
    public TimeStamp(long time) {
        this(time, null);
    }

    /**
     * Creates a new TimeStamp with the current time and the specified format.
     * @param time The time in milliseconds since the epoch.
     * @param format The format to use.
     */
    public TimeStamp(long time, Format format) {
        this.time = time;
        this.format = format;
    }

    public String format() {
        return format(time, format);
    }

    /**
     * Prints the formatted time.
     * @param time The time in milliseconds since the epoch.
     *             If the time is 0, the current time will be used.
     * @return The formatted time.
     */
    public static String format(long time) {
        return format(time, null);
    }

    /**
     * Prints the formatted time.
     * @param time The time in milliseconds since the epoch.
     *             If the time is 0, the current time will be used.
     * @param format The format to use.
     * @return The formatted time.
     */
    public static String format(long time, Format format) {

        if(time <= 0) time = System.currentTimeMillis() / 1000;

        StringBuilder builder = new StringBuilder();
        builder.append("<t:");
        builder.append(time);
        if (format != null) {
            builder.append(":");
            builder.append(format.getStyle());
        }
        builder.append(">");
        return builder.toString();

    }

    @Override
    public String toString() {
        return format();
    }

    public static enum Format {
        SHORT_TIME("t"),
        LONG_TIME("T"),

        SHORT_DATE("d"),
        LONG_DATE("D"),

        SHORT_DATE_TIME("f"),
        LONG_DATE_TIME("F"),

        RELATIVE_TIME("R");

        private final String style;

        Format(String style) {
            this.style = style;
        }

        public String getStyle() {
            return style;
        }

    }

}
