package io.schell.p1.parser;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * parses dsmr timestamp (160626195334S)
 */
public class DateTimeParser implements ValueParser<DateTime> {
    private static final DateTimeFormatter formatter = DateTimeFormat.forPattern("'('yyMMddHHmmss'S')");

    @Override
    public DateTime parse(String value) {
        return DateTime.parse(value.substring(9), formatter);
    }
}
