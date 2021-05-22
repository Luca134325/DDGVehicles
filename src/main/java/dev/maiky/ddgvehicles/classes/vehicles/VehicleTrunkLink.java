package dev.maiky.ddgvehicles.classes.vehicles;

/**
 * Door: Maiky
 * Info: DDGVehicles - 04 Apr 2021
 * Package: dev.maiky.ddgvehicles.classes.vehicles
 */

public enum VehicleTrunkLink {

	canary(27),
	colos(9),
	vortex(9),
	hercal(9),
	bollar(27),
	range(90),
	fiets(9),
	master(9),
	ufo(9);

	private final int rows;
	VehicleTrunkLink(int rows) {
		this.rows = rows;
	}

	public int getRows() {
		return rows;
	}
}
