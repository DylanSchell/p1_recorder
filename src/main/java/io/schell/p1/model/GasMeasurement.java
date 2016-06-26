package io.schell.p1.model;

import org.joda.time.DateTime;

import java.math.BigDecimal;

/**
 * Created by dylan on 6/26/2016.
 */
public class GasMeasurement {
    private DateTime timestamp;
    private BigDecimal gasConsumptionM3;

    public DateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(DateTime timestamp) {
        this.timestamp = timestamp;
    }

    public BigDecimal getGasConsumptionM3() {
        return gasConsumptionM3;
    }

    public void setGasConsumptionM3(BigDecimal gasConsumptionM3) {
        this.gasConsumptionM3 = gasConsumptionM3;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GasMeasurement that = (GasMeasurement) o;

        if (timestamp != null ? !timestamp.equals(that.timestamp) : that.timestamp != null) return false;
        return gasConsumptionM3 != null ? gasConsumptionM3.equals(that.gasConsumptionM3) : that.gasConsumptionM3 == null;

    }

    @Override
    public int hashCode() {
        int result = timestamp != null ? timestamp.hashCode() : 0;
        result = 31 * result + (gasConsumptionM3 != null ? gasConsumptionM3.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "GasMeasurement{" +
                "timestamp=" + timestamp +
                ", gasConsumptionM3=" + gasConsumptionM3 +
                '}';
    }
}
