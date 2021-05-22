package dev.maiky.ddgvehicles.events;

import lombok.Getter;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class VehiclePlaceEvent extends Event implements Cancellable {

    private boolean isCancelled = false;

    public static HandlerList handlerList = new HandlerList();

    @Getter
    private final String license;

    public VehiclePlaceEvent(String license) {
        this.license = license;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.isCancelled = cancel;
    }

}
