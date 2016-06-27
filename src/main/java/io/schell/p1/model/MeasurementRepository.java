package io.schell.p1.model;

import org.joda.time.DateTime;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

/**
 * Created by dylan on 6/27/2016.
 */
public interface MeasurementRepository extends MongoRepository<SmartMeterMeasurement, String> {
    List<SmartMeterMeasurement> findByTimestampBetween(DateTime after, DateTime before);
    List<SmartMeterMeasurement> findByTimestampGreaterThan(DateTime after);
}
