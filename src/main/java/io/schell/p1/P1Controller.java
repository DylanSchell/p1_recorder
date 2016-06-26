package io.schell.p1;

import io.schell.p1.model.SmartMeterMeasurement;
import io.schell.p1.parser.DatagramParser;
import org.joda.time.DateTime;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedList;
import java.util.List;

/**
 *
 */
@RestController
class P1Controller {
    private static final int maxMeasurementsPerDay = 24 * 60 * 6;
    private DatagramParser parser = new DatagramParser();
    private LinkedList<SmartMeterMeasurement> measurements = new LinkedList<>();

    @RequestMapping(method = RequestMethod.POST, consumes = "text/plain",path="/post")
    public void add(@RequestBody String body) {
        SmartMeterMeasurement meterMeasurement = parser.parse(body);
        if ( meterMeasurement.getTimestamp() != null ) {
            meterMeasurement.setTimestamp(DateTime.now());
        }
        if ( measurements.size() >= maxMeasurementsPerDay) {
            measurements.removeFirst();
        }
        measurements.add(meterMeasurement);
    }

    @RequestMapping(method = RequestMethod.GET, produces = "text/plain", path="/post")
    public String get() {
        return "PONG";
    }

    @RequestMapping(method = RequestMethod.GET, produces = "text/plain", path = "/p1")
    public String p1() {
        if ( measurements.isEmpty()) {
            return "no measurement received";
        }
        SmartMeterMeasurement measurement = measurements.getLast();
        return measurement.toString();
    }

    @RequestMapping(method = RequestMethod.GET, produces = "application/json", path="/all")
    public  @ResponseBody List<SmartMeterMeasurement> all() {
        return measurements;
    }
}