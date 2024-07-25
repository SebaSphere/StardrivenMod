package dev.sebastianb.stardriven.api;

import dev.sebastianb.stardriven.api.ship.DimensionalShipManager;

public class StardrivenAPI {

    private static API instance;

    public static StardrivenAPI.API api() {
        return instance;
    }

    public static void _init(API instance) {
        if (StardrivenAPI.instance != null) {
            throw new IllegalStateException("can't init more than once!");
        }
        StardrivenAPI.instance = instance;
    }

    public interface API {

        DimensionalShipManager getDimensionalShipManager();

    }

}
