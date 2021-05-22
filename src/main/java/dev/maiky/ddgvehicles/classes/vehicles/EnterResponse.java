package dev.maiky.ddgvehicles.classes.vehicles;

import lombok.Getter;

public class EnterResponse {

    @Getter
    private boolean state;
    @Getter
    private int seatNumber;

    public EnterResponse(boolean state, int seatNumber) {
        this.state = state;
        this.seatNumber = seatNumber;
    }
}
