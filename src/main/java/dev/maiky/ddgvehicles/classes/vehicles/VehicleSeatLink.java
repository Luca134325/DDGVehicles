package dev.maiky.ddgvehicles.classes.vehicles;

import lombok.Getter;

public enum VehicleSeatLink {

    canary(new Seat(0.25,-1.1,0), new Seat(-0.75,-1.1,0)), // DONE !
    colos(new Seat(0, -.675d, -0.1)), // DONE!
    vortex(new Seat(-0.15,-1.25,0)), // DONE !
    hercal(new Seat(-0.3, -1, 0.0)), // TODO: Moet even gepland worden hoe die Helicopters gedaan worden, misschien in de zomer
    bollar(new Seat(0, -.8, 0), new Seat(-1, -.8, 0)), // DONE!
    range(new Seat(-.15d, -0.75, .15d), new Seat(-.15d, -0.75, -.7),
            new Seat(-1.15, -0.75, .15d), new Seat(-1.15, -0.75, -.7)), // DONE!
    fiets(new Seat(-0.75, -0.55, 0.025)),
    master(new Seat(0,-1.2,0)), // DONE !
    ufo(new Seat(0,-.9,0)); // DONE!

    @Getter
    private final Seat[] seats;

    VehicleSeatLink(Seat... seats) {
        this.seats = seats;
    }
}
