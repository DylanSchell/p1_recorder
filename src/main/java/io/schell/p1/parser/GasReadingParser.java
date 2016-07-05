package io.schell.p1.parser;

import io.schell.p1.model.GasMeasurement;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 0-1:24.2.1(160626180000S)(02956.288*m3)
 */
public class GasReadingParser implements ValueParser<GasMeasurement> {
    private static final DateTimeFormatter formatter = DateTimeFormat.forPattern("'('yyMMddHHmmss'S')");
    private final Pattern pattern = Pattern.compile("\\(([0-9]*\\.[0-9]*)\\*m3\\)");
    @Override
    public GasMeasurement parse(String value) {
        String datePart = value.substring(10,25);
        String valuePart = value.substring(25);
        DateTime dateTime =  DateTime.parse(datePart, formatter);
        Matcher matcher = pattern.matcher(valuePart);
        BigDecimal consumption = BigDecimal.ZERO;
        if (matcher.find()) {
            consumption = new BigDecimal(matcher.group(1));
        }
        GasMeasurement gasMeasurement = new GasMeasurement();
        gasMeasurement.setTimestamp(dateTime);
        gasMeasurement.setGasConsumptionM3(consumption);
        return gasMeasurement;
    }
}
