package io.schell.p1;

import io.schell.p1.model.MeasurementRepository;
import io.schell.p1.model.SmartMeterMeasurement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;

@SpringBootApplication
public class P1RecorderApplication  implements CommandLineRunner {
    @Autowired
    private MeasurementRepository repository;

    public static void main(String[] args) {
        SpringApplication.run(P1RecorderApplication.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {
//        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.registerModule(new JodaModule());
//        SmartMeterMeasurement[] myObjects = objectMapper.readValue(new File("all.json"), SmartMeterMeasurement[].class);
//        for(SmartMeterMeasurement measurement: myObjects) {
//            repository.save(measurement);
//        }
//        List<SmartMeterMeasurement> readings = repository.findAll();
//        for(SmartMeterMeasurement m: readings) {
//            if (m.getTimestamp() == null) {
//                repository.delete(m.getId());
//            }
//        }

//        System.out.println("number of objects in database "+readings.size());
    }
}
