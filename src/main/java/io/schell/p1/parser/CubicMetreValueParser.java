package io.schell.p1.parser;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class CubicMetreValueParser implements ValueParser<BigDecimal> {

    private final Pattern pattern;

    CubicMetreValueParser() {
        pattern = Pattern.compile("\\(([0-9]*\\.[0-9]*)\\)$");
    }

    @Override
    public BigDecimal parse(String value) {
        BigDecimal result = null;

        Matcher matcher = pattern.matcher(value);

        if (matcher.find()) {
            result = new BigDecimal(matcher.group(1));
        }

        return result;
    }
}