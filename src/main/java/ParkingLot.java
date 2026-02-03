/*
UML Diagram for parking Lot

ParkingLot controls the system, Floors manage spots, Spots hold vehicles


Parking Lot
    |
    V
Parking Floor
    |
    V
Parking Spot
    |
    V
Vehicle

*/

/*
-------------------------------------------------------------------
Parking Lot
    - floors : List<Parking floors>
    - activeTickets : Map<TicketId, Ticket>
    ----------------------
    + parkVehicle(vehicle)
    + unParkVehicle(ticketId)

        |
        V

Parking Floors
    - floorId
    - spots : List<ParkingSpot>
    -------------------------
    +findAvailableSpot(vehicleType)

        |
        V

Parking Spot
    - spotId
    - spot Type
    - vehicle
  ------------------------------
    + park(vehicle)
    + removeVehicle()

        |
        V

Vehicle
    - vehicleNumber
    - vehicleType
-------------------------------------------------------


---------------------
ParkingTicket
- ticketId
- vehicle
- spot
---------------------


 */


import java.util.HashMap;
import java.util.List;
import java.util.Map;

enum Type {
    CAR, BIKE, TRUCK
}

enum UserType {
    STANDARD,
    PREMIUM
}

class Vehicle {
    String vehicleNumber;
    Type vehicleType;
    UserType userType;

    public Vehicle(String vehicleNumber, Type vehicleType, UserType userType) {
        this.vehicleNumber = vehicleNumber;
        this.vehicleType = vehicleType;
        this.userType = userType;
    }

}

class ParkingTicket{
    String ticketId;
    Vehicle vehicle;
    ParkingSpot spot;

    ParkingTicket(String ticketId, Vehicle vehicle, ParkingSpot spot) {
        this.ticketId = ticketId;
        this.vehicle = vehicle;
        this.spot = spot;
    }

}

class ParkingSpot {
    String spotId;
    Type spotType;
    Vehicle vehicle;

    ParkingSpot(String spotId, Type spotType) {
        this.spotId = spotId;
        this.spotType = spotType;
        this.vehicle = null;
    }

    public boolean canFit(Type vehicleType) {
        return this.spotType == vehicleType;
    }

    public boolean isFree() {
        return this.vehicle == null;
    }

    public boolean parkVehicle(Vehicle v) {
        if( ! isFree()) {
            return false;
        }
        if( ! canFit(v.vehicleType)) {
            return false;
        }
        this.vehicle = v;
        return true;
    }

    public void unParkVehicle() {
        this.vehicle = null;
    }
}

class ParkingFloor {
    int floorId;
    List<ParkingSpot> spots;

    ParkingFloor(int floorId, List<ParkingSpot> spots) {
        this.floorId = floorId;
        this.spots = spots;
    }
}

interface ParkingStrategy {
    ParkingSpot findSpot(List<ParkingFloor> floors, Vehicle v);
}

class StandardUserStrategy implements ParkingStrategy {

    @Override
    public ParkingSpot findSpot(List<ParkingFloor> floors, Vehicle v) {
        for(ParkingFloor floor: floors) {
            for(ParkingSpot spot: floor.spots) {
                if(spot.isFree() && spot.canFit(v.vehicleType)) {
                    return spot;
                }
            }
        }
        return null;
    }

}

class PremiumUserStrategy implements ParkingStrategy {

    @Override
    public ParkingSpot findSpot(List<ParkingFloor> floors, Vehicle v) {
        for(ParkingFloor floor: floors) {
            // Try ground floor first
            if(floor.floorId == 0) {
                for(ParkingSpot spot: floor.spots) {
                    if(spot.isFree() && spot.canFit(v.vehicleType)) {
                        return spot;
                    }
                }
            }

        }
        for(ParkingFloor floor: floors) {
            // Try remaining floors
            if(floor.floorId != 0) {
                for(ParkingSpot spot: floor.spots) {
                    if(spot.isFree() && spot.canFit(v.vehicleType)) {
                        return spot;
                    }
                }
            }

        }
        return null;
    }
}

class ParkingStrategyFactory {
    public static ParkingStrategy createStrategy(UserType userType) {
    if(userType == UserType.PREMIUM) {
        return new PremiumUserStrategy();
    }
    return new StandardUserStrategy();
    }
}

public class ParkingLot {
    List<ParkingFloor> floors;
    Map<String, ParkingTicket> activeTickets = new HashMap<>();

    ParkingLot(List<ParkingFloor> floors){
        this.floors = floors;
    }

    ParkingTicket parkVehicle(Vehicle v) {
        ParkingStrategy strategy = ParkingStrategyFactory.createStrategy(v.userType);
        ParkingSpot spot = strategy.findSpot(floors, v);
        if(spot == null) {
            return null;
        }

        boolean parked = spot.parkVehicle(v);
        if (!parked) {
            System.out.println("Failed to park vehicle: " + v.vehicleNumber);
            return null;
        }

        String ticketId = "T-" + System.nanoTime();
        ParkingTicket ticket = new ParkingTicket(ticketId, v, spot);
        activeTickets.put(ticketId, ticket);

        return ticket;
    }


    public void unParkVehicle(String ticketId){
        ParkingTicket ticket = activeTickets.get(ticketId);

        if(ticket ==  null ) return ;

        ticket.spot.unParkVehicle();
        activeTickets.remove(ticketId);
    }
}

// Driver code
class Main {
    public static void main(String []args) {

        ParkingLot parkingLot = creatParkingLot();
        // create vehicles
        Vehicle premiumCar = new Vehicle("TS-09-WA-9999", Type.CAR, UserType.PREMIUM);
        Vehicle standardBike = new Vehicle("TS-03-SS-1111", Type.BIKE, UserType.STANDARD);


        // park vehicle
        ParkingTicket ticket1 = parkingLot.parkVehicle(premiumCar);
        ParkingTicket ticket2 =parkingLot.parkVehicle(standardBike);

        parkingLot.unParkVehicle(ticket1.ticketId);
        parkingLot.unParkVehicle(ticket2.ticketId);


    }
    private static ParkingLot creatParkingLot() {
        // create spots
        ParkingSpot g1 = new ParkingSpot("G-A", Type.BIKE);
        ParkingSpot g2 = new ParkingSpot("G-B", Type.CAR);
        ParkingSpot g3 = new ParkingSpot("G-C", Type.TRUCK);

        ParkingSpot f1_A = new ParkingSpot("F1-A", Type.BIKE);
        ParkingSpot f1_B = new ParkingSpot("F1-B", Type.CAR);
        ParkingSpot f1_C = new ParkingSpot("F1-C", Type.TRUCK);

        // Create floors
        ParkingFloor groundFloor = new ParkingFloor(0, List.of(g1, g2, g3));
        ParkingFloor firstFloor = new ParkingFloor(1, List.of(f1_A, f1_B, f1_C));

        // create parking lot
        return new ParkingLot(List.of(groundFloor, firstFloor));

    }


}


