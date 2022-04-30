import com.owlike.genson.annotation.JsonProperty;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import java.util.Objects;

@DataType()
public final class Flight {

    @Property()
    private final String flightNr;

    @Property()
    private final String flyFrom;

    @Property()
    private final String flyTo;

    @Property()
    private final String dateTime;

    @Property()
    private final int availablePlaces;

    public String getFlightNr() {
        return flightNr;
    }

    public String getFlyFrom() {
        return flyFrom;
    }

    public String getFlyTo() {
        return flyTo;
    }

    public String getDateTime() {
        return dateTime;
    }

    public int getAvailablePlaces() {
        return availablePlaces;
    }

    public Flight(@JsonProperty("flightNr") final String flightNr, @JsonProperty("flyFrom") final String flyFrom,
                     @JsonProperty("flyTo") final String flyTo, @JsonProperty("dateTime") final String dateTime,
                  @JsonProperty("availablePlaces") final int availablePlaces) {
        this.flightNr = flightNr;
        this.flyTo = flyTo;
        this.flyFrom = flyFrom;
        this.dateTime = dateTime;
        this.availablePlaces = availablePlaces;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        Flight other = (Flight) obj;

        return Objects.deepEquals(
                new String[] {getFlightNr(), getFlyFrom(), getFlyTo(), getDateTime()},
                new String[] {other.getFlightNr(), other.getFlyFrom(), other.getFlyTo(), other.getDateTime()})
                &&
                Objects.deepEquals(
                        new int[] {getAvailablePlaces()},
                        new int[] {other.getAvailablePlaces()});
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFlightNr(), getFlyFrom(), getFlyTo(), getDateTime(), getAvailablePlaces());
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + " [flightNr=" + flightNr + "," +
                "flyTo=" + flyTo + ", flyFrom=" + flyFrom + ", dateTime=" + dateTime + ", availablePlaces=" +
                availablePlaces + "]";
    }
}