package io.schell.p1;

import io.schell.p1.model.MeasurementRepository;
import io.schell.p1.model.SmartMeterMeasurement;
import io.schell.p1.parser.DatagramParser;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 */
@RestController
class P1Controller {
    private static final int maxMeasurementsPerDay = 24 * 60 * 6;
    private DatagramParser parser = new DatagramParser();
    @Autowired
    private MeasurementRepository repository;

    @RequestMapping(method = RequestMethod.POST, consumes = "text/plain", path = "/post")
    public void add(@RequestBody String body) {
        SmartMeterMeasurement meterMeasurement = parser.parse(body);
        if (meterMeasurement.getTimestamp() != null) {
            meterMeasurement.setTimestamp(DateTime.now());
        }
        repository.save(meterMeasurement);
    }

    @RequestMapping(method = RequestMethod.GET, produces = "text/plain", path = "/post")
    public String get() {
        return "PONG";
    }

    @RequestMapping(method = RequestMethod.GET, produces = "text/plain", path = "/p1")
    public String p1() {
        List<SmartMeterMeasurement> measurements = repository.findAll();
        if (measurements.isEmpty()) {
            return "no measurement received";
        }
        SmartMeterMeasurement measurement = measurements.get(measurements.size() - 1);
        return measurement.toString();
    }

    @RequestMapping(method = RequestMethod.GET, produces = "application/json", path = "/all")
    public
    @ResponseBody
    List<SmartMeterMeasurement> all() {
        List<SmartMeterMeasurement> all = repository.findAll();
        return cleanup(all);
    }

    @RequestMapping(method = RequestMethod.GET, produces = "application/json", path = "/hour")
    public
    @ResponseBody
    List<SmartMeterMeasurement> hour() {
        List<SmartMeterMeasurement> hour = repository.findByTimestampGreaterThan(DateTime.now().minusHours(1));
        return cleanup(hour);
    }

    @RequestMapping(method = RequestMethod.GET, produces = "application/json", path = "/day")
    public
    @ResponseBody
    List<SmartMeterMeasurement> day() {
        List<SmartMeterMeasurement> hour = repository.findByTimestampGreaterThan(DateTime.now().minusHours(24));
        return cleanup(hour);
    }

    private List<SmartMeterMeasurement> cleanup(List<SmartMeterMeasurement> input) {
        Iterator<SmartMeterMeasurement> iter = input.iterator();
        while (iter.hasNext()) {
            SmartMeterMeasurement m = iter.next();
            if (m.getTimestamp() == null) {
                System.out.println("remove");
                iter.remove();
            }
            // horrible hack to get timezone correct
            m.setTimestamp(m.getTimestamp().plusHours(2));
        }
        return input;
    }

    private List<SmartMeterMeasurement> filterMinutes(List<SmartMeterMeasurement> source) {
        List<SmartMeterMeasurement> result = new ArrayList<SmartMeterMeasurement>();
        Iterator<SmartMeterMeasurement> iter = source.iterator();
        SmartMeterMeasurement cursor = iter.next();
        while(iter.hasNext()) {
            SmartMeterMeasurement n = iter.next();
        }
        return result;
    }
}