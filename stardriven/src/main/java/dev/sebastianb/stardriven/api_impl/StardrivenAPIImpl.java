package dev.sebastianb.stardriven.api_impl;

import dev.sebastianb.stardriven.api.StardrivenAPI;
import dev.sebastianb.stardriven.api.ship.DimensionalShipManager;
import dev.sebastianb.stardriven.api_impl.ship.DimensionalShipManagerImpl;

public enum StardrivenAPIImpl implements StardrivenAPI.API {
    INSTANCE;

    @Override
    public DimensionalShipManager getDimensionalShipManager() {
        return DimensionalShipManagerImpl.INSTANCE;
    }

}
