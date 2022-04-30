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
                               final int seats) {
        ChaincodeStub stub = ctx.getStub();

        String airline;
        //TODO - AIRLINE DISTINGUISHER
        //TODO - PREVENT FLIGHTNR DUPLICATES
        airline = "EC"; //alebo "BS"
        Random random = new Random();
        String flightNr = airline + String.format("%03d", random.nextInt(999)+1);

        Flight flight = new Flight(flightNr, flyFrom, flyTo, dateTime, seats);

        String flightState = genson.serialize(flight);
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

        //TODO
        Reservation reservation = new Reservation();

        return reservation;
    }

    @Transaction()
    public Reservation bookSeats(final Context ctx, final String reservationNr) {
        ChaincodeStub stub = ctx.getStub();

        //TODO
        Reservation reservation = new Reservation();

        return reservation;
    }

    @Transaction()
    public Reservation checkIn(final Context ctx, final String reservationNr, final String[] passportIDs){
        ChaincodeStub stub = ctx.getStub();

        //TODO
        Reservation reservation = new Reservation();

        return reservation;
    }

}
