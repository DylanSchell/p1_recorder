package io.schell.p1.parser;

import io.schell.p1.model.SmartMeterMeasurement;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public class DatagramParser {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private Map<String, PropertyAndPattern> mapping;

    public DatagramParser() {
        mapping = new HashMap<>();
        mapping.put("1-0:1.8.1", new PropertyAndPattern(new KwhValueParser(), "electricityConsumptionLowRateKwh"));
        mapping.put("1-0:1.8.2", new PropertyAndPattern(new KwhValueParser(), "electricityConsumptionNormalRateKwh"));
        mapping.put("1-0:2.8.1", new PropertyAndPattern(new KwhValueParser(), "electricityProductionLowRateKwh"));
        mapping.put("1-0:2.8.2", new PropertyAndPattern(new KwhValueParser(), "electricityProductionNormalRateKwh"));
        mapping.put("1-0:1.7.0", new PropertyAndPattern(new WattValueParser(), "currentPowerConsumptionW"));
        mapping.put("1-0:2.7.0", new PropertyAndPattern(new WattValueParser(), "currentPowerProductionW"));
        mapping.put("0-1:24.3.0", new PropertyAndPattern(new CubicMetreValueParser(), "gasConsumptionM3"));
        mapping.put("0-0:1.0.0", new PropertyAndPattern(new DateTimeParser(), "timestamp"));
        mapping.put("0-1:24.2.1", new PropertyAndPattern(new GasReadingParser(),"gasMeasurement"));
        // 0-0:96.1.1 serialnumber hex -> ascii
        // 0-0:96.14.0 actuele tarief
        // 0-0:17.0.0 max stroom per fase
        // 0-1:96.1.0(4730303233353631323233363733353134) serie nummer gas meter
        // 0-1:24.1.0(03) //andere apparaten op de M-Bus
        // 0-0:96.13.1()	//bericht numeriek
        // 0-0:96.13.0()	//bericht tekst

    }

    public SmartMeterMeasurement parse(String datagram) {
        int crc = io.schell.p1.parser.CRC16.slowCrc16(datagram.substring(0,datagram.length()-7).getBytes(),0x0000, io.schell.p1.parser.CRC16.stdPoly);
        String crcString = Integer.toHexString(crc).toUpperCase();
        String crcFromTelegram = datagram.substring(datagram.length()-7,datagram.length()-3);
        logger.debug("calculated crc = {}",crcString);
        logger.debug("telegram crc = {}",datagram.substring(datagram.length()-6));
        if ( !crcString.equals(crcFromTelegram)) {
            logger.warn("Received malformed telegram");
            return null;
        }
        SmartMeterMeasurement result = new SmartMeterMeasurement();

        String[] datagramLines = DatagramCleaner.asArray(datagram);

        for (String line : datagramLines) {

            for (Map.Entry<String, PropertyAndPattern> entry : mapping.entrySet()) {
                if (line.startsWith(entry.getKey())) {
                    entry.getValue().extract(line, result);
                    break;
                }
            }
        }

        return result;
    }

    private class PropertyAndPattern {

        private ValueParser valueParser;
        private String fieldName;

        public PropertyAndPattern(ValueParser valueParser, String fieldName) {
            this.valueParser = valueParser;
            this.fieldName = fieldName;
        }

        public void extract(String line, SmartMeterMeasurement measurement) {
            Object value = valueParser.parse(line);
            try {
                PropertyUtils.setProperty(measurement, fieldName, value);
            } catch (IllegalAccessException e) {
                logger.error(e.toString(), e);
            } catch (InvocationTargetException e) {
                logger.error(e.toString(), e);
            } catch (NoSuchMethodException e) {
                logger.error(e.toString(), e);
            }
        }
    }
}