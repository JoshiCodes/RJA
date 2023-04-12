package de.joshicodes.rja;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class RJALogFormatter extends Formatter {

    @Override
    public String format(LogRecord record) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return String.format(
                "[%s] [%s] %s: %s %n",
                format.format(new Date(record.getMillis())), record.getLevel(), record.getLoggerName(), record.getMessage()
        );
    }

}
