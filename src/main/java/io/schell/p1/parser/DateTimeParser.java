package io.schell.p1.parser;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * parses dsmr timestamp (160626195334S)
 */
public class DateTimeParser implements ValueParser<DateTime> {
    private static final DateTimeFormatter formatter = DateTimeFormat.forPattern("yyMMddHHmmss");

    @Override
    public DateTime parse(String value) {
        String dst = value.substring(22,23);
        String datePart = value.substring(10,22);
        // TODO: do I need to take dst into account? ( 'S' Summmer 'W' Winter )
        return DateTime.parse(datePart,formatter);
    }
}
