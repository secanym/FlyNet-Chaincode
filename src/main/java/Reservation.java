import com.owlike.genson.annotation.JsonProperty;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import java.util.Arrays;
import java.util.Objects;

@DataType
public final class Reservation {

    @Property()
    private final String reservationNr;

    @Property()
    private final String[] customerNames;

    @Property()
    private final String customerEmail;

    @Property()
    private final String flightNr;

    @Property()
    private final int nrOfSeats;

    @Property
    private final String status;

    public String getReservationNr() {
        return reservationNr;
    }

    public String[] getCustomerNames() {
        return customerNames;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public String getFlightNr() {
        return flightNr;
    }

    public int getNrOfSeats() {
        return nrOfSeats;
    }

    public String getStatus() {
        return status;
    }

    public Reservation(@JsonProperty("reservationNr") final String reservationNr,
                       @JsonProperty("customerNames") final String[] customerNames,
                       @JsonProperty("customerEmail") final String customerEmail,
                       @JsonProperty("flightNr") final String flightNr,
                       @JsonProperty("nrOfSeats") final int nrOfSeats, @JsonProperty("status") final String status) {
        this.reservationNr = reservationNr;
        this.customerNames = customerNames;
        this.customerEmail = customerEmail;
        this.flightNr = flightNr;
        this.nrOfSeats = nrOfSeats;
        this.status = status;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        Reservation other = (Reservation) obj;

        return Objects.deepEquals(
                new String[] {getReservationNr(), getCustomerEmail(), getFlightNr(), getStatus()},
                new String[] {other.getReservationNr(), other.getCustomerEmail(), other.getFlightNr(),
                        other.getStatus()})
                &&
                Objects.deepEquals(
                        new int[] {getNrOfSeats()},
                        new int[] {other.getNrOfSeats()})
                &&
                Objects.deepEquals(getCustomerNames(),other.getCustomerNames());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getReservationNr(), getCustomerNames(), getCustomerEmail(), getFlightNr(), getNrOfSeats(),
                getStatus());
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + " [reservationNr=" +
                reservationNr + ", customerNames=" + Arrays.toString(customerNames) + ", customerEmail=" + customerEmail +
                ", flightNr=" + flightNr + ", nrOfSeats=" + nrOfSeats + "]";
    }
}