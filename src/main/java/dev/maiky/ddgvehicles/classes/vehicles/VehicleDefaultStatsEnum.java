package dev.maiky.ddgvehicles.classes.vehicles;

public enum VehicleDefaultStatsEnum {

    canary(0.009, 0.65, 0.005, 4, 0.03, 0.35),
    colos(0.015, 0.8, 0.005, 4, 0.03, 0.35),
    vortex(0.012, 0.67, 0.005, 4, 0.03, 0.35),
    hercal(0.01, 0.7, 0.02, 6, 0.03, 0.35),
    bollar(0.009, 0.65, 0.005, 4, 0.03, 0.35),
    range(0.012, 0.8, 0.005, 4, 0.03, 0.35),
    fiets(0.006, 0.45, 0.005, 4, 0.03, 0.0),
    master(0.021, 0.92, 0.005, 4, 0.03, 0.35),
    ufo(0.012, 0.67, 0.005, 4, 0.03, 0.35);

    public final double acceleration, maxSpeed, fuel, deceleration, rotateSpeed,
            brakingSpeed, maxBackSpeed;

    VehicleDefaultStatsEnum(double acceleration, double maxSpeed, double deceleration,
                            double rotateSpeed, double brakingSpeed, double maxBackSpeed) {
        this.acceleration = bToKm(acceleration * 3);
        this.maxSpeed = bToKm(maxSpeed);
        this.fuel = 0.0025;
        this.deceleration = deceleration;
        this.rotateSpeed = rotateSpeed;
        this.brakingSpeed = brakingSpeed;
        this.maxBackSpeed = maxBackSpeed;
    }

    public static float kmToB(double km) {
        return (float) ((km / 3.6d) / 8d);
    }

    public static double bToKm(double blocks) {
        return (float) ((blocks * 8d) * 3.6d);
    }

}
