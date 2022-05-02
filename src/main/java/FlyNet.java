import com.owlike.genson.Genson;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.*;
import org.hyperledger.fabric.shim.Chaincode;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Contract(
        name = "FlyNet",
        info = @Info(
                title = "FlyNet contract",
                description = "A java chaincode for flights & reservations",
                version = "0.0.1"))
@Default
public final class FlyNet implements ContractInterface {

    private final Genson genson = new Genson();

    @Transaction()
    public Flight createFlight(final Context ctx, final String flyFrom, final String flyTo, final String dateTime,
                               final int seats, final String airline) {
        ChaincodeStub stub = ctx.getStub();
        String airlineCode;

        if(airline.equals("EconFly")){
            airlineCode = "EC";
        }

        if(airline.equals("BusiFly")){
            airlineCode = "BS";
        }
        Random random = new Random();
        String flightNr;
        String flightState;

        do {
            flightNr = airline + String.format("%03d", random.nextInt(999)+1);
            flightState = stub.getStringState(flightNr);
        }while(!flightState.isEmpty());

        Flight flight = new Flight(flightNr, flyFrom, flyTo, dateTime, seats);

        flightState = genson.serialize(flight);
        stub.putStringState(flightNr, flightState);

        return flight;
    }

    @Transaction()
    public String getAllFlights(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();

        List<Flight> queryResults = new ArrayList<Flight>();
        QueryResultsIterator<KeyValue> results = stub.getStateByRange("", "");

        for (KeyValue result: results) {
            Flight asset = genson.deserialize(result.getStringValue(), Flight.class);
            System.out.println(asset);
            queryResults.add(asset);
        }

        final String response = genson.serialize(queryResults);

        return response;
    }

    @Transaction()
    public Flight getFlight(final Context ctx, final String flightNr) {
        ChaincodeStub stub = ctx.getStub();
        String flightState = stub.getStringState(flightNr);

        if (flightState == null || flightState.isEmpty() ) {
            String errorMessage = String.format("Flight %s does not exist", flightNr);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, "Flight not found");
        }

        Flight flight = genson.deserialize(flightState, Flight.class);

        return flight;
    }

    @Transaction()
    public Reservation reserveSeats(final Context ctx, final String flightNr, final int seats,
                                    final String[] customerNames, final String customerEmail) {
        ChaincodeStub stub = ctx.getStub();

        String flightState = stub.getStringState(flightNr);

        if (flightState == null || flightState.isEmpty() ) {
            String errorMessage = String.format("Flight %s does not exist", flightNr);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, "Flight not found");
        }

        String reservationNr;
        String reservationState;
        Random random = new Random();

        do {
            reservationNr = flightNr + "-" + String.format("%03d", random.nextInt(999)+1);
            reservationState = stub.getStringState(reservationNr);
        }while(!reservationState.isEmpty());


        Reservation reservation = new Reservation(reservationNr, customerNames, customerEmail, flightNr, seats,
                "Pending");
        reservationState = genson.serialize(reservation);
        stub.putStringState(reservationNr, reservationState);
        return reservation;
    }

    @Transaction()
    public Reservation bookSeats(final Context ctx, final String reservationNr) {
        ChaincodeStub stub = ctx.getStub();
        String reservationState = stub.getStringState(reservationNr);

        if (reservationState == null || reservationState.isEmpty()) {
            String errorMessage = String.format("Reservation %s does not exist", reservationNr);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, "Reservation not found");
        }

        Reservation reservation = genson.deserialize(reservationState, Reservation.class);

        String flightState = stub.getStringState(reservation.getFlightNr());
        Flight flight = genson.deserialize(flightState, Flight.class);

        if (flight.getAvailablePlaces() < reservation.getNrOfSeats()) {
            String errorMessage = String.format("There is not enough seats available");
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, "Not enough seats available");
        }

        Flight newFlight = new Flight(flight.getFlightNr(), flight.getFlyFrom(), flight.getFlyTo(), flight.getDateTime(),
                flight.getAvailablePlaces() - reservation.getNrOfSeats());
        String newFlightState = genson.serialize(newFlight);
        stub.putStringState(flight.getFlightNr(), newFlightState);

        Reservation newReservation = new Reservation(reservation.getReservationNr(),reservation.getCustomerNames(),
                reservation.getCustomerEmail(),reservation.getFlightNr(),reservation.getNrOfSeats(), "Completed");
        String newReservationState = genson.serialize(newReservation);
        stub.putStringState(reservation.getReservationNr(),newReservationState);

        return reservation;
    }

    @Transaction()
    public Reservation checkIn(final Context ctx, final String reservationNr, final String[] passportIDs){
        ChaincodeStub stub = ctx.getStub();

        String reservationState = stub.getStringState(reservationNr);

        if (reservationState == null || reservationState.isEmpty()) {
            String errorMessage = String.format("Reservation %s does not exist", reservationNr);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, "Reservation not found");
        }

        Reservation reservation = genson.deserialize(reservationState, Reservation.class);

        for(int i = 1; i <= reservation.getNrOfSeats(); i++){
            String[] passenger = passportIDs[i-1].split("/");
            String msg1 = String.format("Planeticket for %s is ",passenger[0]);
            String msg2 = String.format(reservation.getFlightNr()+"%03d",i);
            String msg = msg1 + msg2;
            System.out.println(msg);
        }

        Reservation newReservation = new Reservation(reservation.getReservationNr(),reservation.getCustomerNames(),
                reservation.getCustomerEmail(),reservation.getFlightNr(),reservation.getNrOfSeats(), "Checked-In");
        String newReservationState = genson.serialize(newReservation);
        stub.putStringState(reservation.getReservationNr(),newReservationState);

        return reservation;
    }

}
