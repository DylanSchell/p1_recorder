package io.schell.p1;

import io.schell.p1.model.SmartMeterMeasurement;
import io.schell.p1.parser.DatagramParser;
import org.junit.Test;

/**
 * Created by dylan on 6/26/2016.
 */
public class TestParser {
    private String reading = "/XMX5LGBBFFB231176969\n" +
            "\n" +
            "1-3:0.2.8(42)\n" +
            "0-0:1.0.0(160626185210S)\n" +
            "0-0:96.1.1(4530303034303031353836323639363134)\n" +
            "1-0:1.8.1(003578.591*kWh)\n" +
            "1-0:2.8.1(000000.000*kWh)\n" +
            "1-0:1.8.2(003435.785*kWh)\n" +
            "1-0:2.8.2(000000.000*kWh)\n" +
            "0-0:96.14.0(0001)\n" +
            "1-0:1.7.0(00.754*kW)\n" +
            "1-0:2.7.0(00.000*kW)\n" +
            "0-0:96.7.21(00002)\n" +
            "0-0:96.7.9(00000)\n" +
            "1-0:99.97.0(0)(0-0:96.7.19)\n" +
            "1-0:32.32.0(00013)\n" +
            "1-0:32.36.0(00000)\n" +
            "0-0:96.13.1()\n" +
            "0-0:96.13.0()\n" +
            "1-0:31.7.0(004*A)\n" +
            "1-0:21.7.0(00.754*kW)\n" +
            "1-0:22.7.0(00.000*kW)\n" +
            "0-1:24.1.0(003)\n" +
            "0-1:96.1.0(4730303233353631323233363733353134)\n" +
            "0-1:24.2.1(160626180000S)(02956.288*m3)\n" +
            "!B768\n";

    @Test
    public void testParser() {
        DatagramParser parser = new DatagramParser();
        SmartMeterMeasurement measurement = parser.parse(reading);
    }
}
