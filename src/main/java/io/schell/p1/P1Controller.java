package io.schell.p1;

import io.schell.p1.model.MeasurementRepository;
import io.schell.p1.model.SmartMeterMeasurement;
import io.schell.p1.parser.DatagramParser;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 */
@RestController
class P1Controller {
    private static final BigDecimal K = BigDecimal.valueOf(1000);
    private static final BigDecimal SECONDS_PER_MINUTE = BigDecimal.valueOf(60);
    private static final int maxMeasurementsPerDay = 24 * 60 * 6;

    @Autowired
    private MeasurementRepository repository;
    private final DatagramParser parser = new DatagramParser();
    private SmartMeterMeasurement lastMeasurement;

    @Autowired
    public P1Controller(MeasurementRepository repository) {
        this.repository = repository;
    }

    @RequestMapping(method = RequestMethod.POST, consumes = "text/plain", path = "/post")
    public void add(@RequestBody String body) {
        SmartMeterMeasurement meterMeasurement = parser.parse(body);
        if (meterMeasurement.getTimestamp() != null) {
            meterMeasurement.setTimestamp(DateTime.now());
        }
        repository.save(meterMeasurement);
        lastMeasurement = meterMeasurement;
    }

    @RequestMapping(method = RequestMethod.GET, produces = "text/plain", path = "/post")
    public String get() {
        return "PONG";
    }

    @RequestMapping(method = RequestMethod.GET, produces = "application/json", path = "/p1")
    @ResponseBody
    public SmartMeterMeasurement p1() {
        if (lastMeasurement == null) {
            List<SmartMeterMeasurement> measurements = repository.findAll();
            if (measurements.isEmpty()) {
                return null;
            } else {
                lastMeasurement = measurements.get(measurements.size() - 1);
            }
        }
        return lastMeasurement;
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

    @RequestMapping(method = RequestMethod.GET, produces = "application/json", path = "/hour_minutes")
    public
    @ResponseBody
    List<SmartMeterMeasurement> hour_minutes() {
        List<SmartMeterMeasurement> hour = repository.findByTimestampGreaterThan(DateTime.now().minusHours(1));
        return filterMinutes(cleanup(hour));
    }

    @RequestMapping(method = RequestMethod.GET, produces = "application/json", path = "/day_minutes")
    public
    @ResponseBody
    List<SmartMeterMeasurement> day_minutes() {
        List<SmartMeterMeasurement> hour = repository.findByTimestampGreaterThan(DateTime.now().minusHours(24));
        return filterMinutes(cleanup(hour));
    }

    @RequestMapping(method = RequestMethod.GET, produces = "application/json", path = "/gas")
    public
    @ResponseBody
    List<SmartMeterMeasurement> gas() {
        List<SmartMeterMeasurement> day = repository.findByTimestampGreaterThan(DateTime.now().minusHours(24));
        return filterGas(cleanup(day));
    }

    private List<SmartMeterMeasurement> cleanup(List<SmartMeterMeasurement> input) {
        Iterator<SmartMeterMeasurement> iter = input.iterator();
        while (iter.hasNext()) {
            SmartMeterMeasurement m = iter.next();
            if (m.getTimestamp() == null) {
                iter.remove();
            }
            // horrible hack to get timezone correct
            m.setTimestamp(m.getTimestamp().plusHours(2));
        }
        return input;
    }

    private List<SmartMeterMeasurement> filterMinutes(List<SmartMeterMeasurement> source) {
        Iterator<SmartMeterMeasurement> iter = source.iterator();
        SmartMeterMeasurement cursor = iter.next();
        while (iter.hasNext()) {
            SmartMeterMeasurement n = iter.next();
            if (n.getTimestamp().getMinuteOfHour() == cursor.getTimestamp().getMinuteOfHour()) {
                iter.remove();
            } else {
                // set this consumption to be the n - cursor;
                BigDecimal actual = orZero(n.getElectricityConsumptionNormalRateKwh())
                        .add(orZero(n.getElectricityConsumptionLowRateKwh()))
                        .subtract(orZero(cursor.getElectricityConsumptionNormalRateKwh())
                                .add(orZero(n.getElectricityConsumptionLowRateKwh())));
                // actual is in KWh, we need to translate it to Ws?
                actual = actual.multiply(K).multiply(SECONDS_PER_MINUTE);
                if (actual.compareTo(BigDecimal.valueOf(5000)) > 0 || actual.compareTo(BigDecimal.valueOf(-5000)) < 0) {
                    iter.remove();
                } else {
                    n.setCurrentPowerConsumptionW(actual);
                    cursor = n;
                }
            }
        }
        // ok now we only have minutes left
        return source;
    }

    private List<SmartMeterMeasurement> filterGas(List<SmartMeterMeasurement> source) {
        Iterator<SmartMeterMeasurement> iter = source.iterator();
        SmartMeterMeasurement cursor = iter.next();
        while ((cursor.getGasMeasurement() == null || cursor.getGasMeasurement().getGasConsumptionM3() == null || cursor.getGasMeasurement().getGasConsumptionM3().compareTo(BigDecimal.ZERO) == 0) && iter.hasNext()) {
            iter.remove();
            cursor = iter.next();
        }
        cursor.setGasConsumptionM3(cursor.getGasMeasurement().getGasConsumptionM3());
        while (iter.hasNext()) {
            SmartMeterMeasurement n = iter.next();
            if (n.getGasMeasurement() == null || BigDecimal.ZERO.compareTo(n.getGasMeasurement().getGasConsumptionM3()) == 0) {
                iter.remove();
            } else {
                if (orZero(n.getGasMeasurement().getGasConsumptionM3()).compareTo(orZero(cursor.getGasMeasurement().getGasConsumptionM3())) == 0) {
                    iter.remove();
                } else {
                    n.setGasConsumptionM3(n.getGasMeasurement().getGasConsumptionM3());
                    System.out.println(n.getGasConsumptionM3());
                    cursor = n;
                }
            }
        }
        // ok now we only have changed gas measurements
        return source;
    }

    private BigDecimal orZero(BigDecimal input) {
        return input == null ? BigDecimal.ZERO : input;
    }
}