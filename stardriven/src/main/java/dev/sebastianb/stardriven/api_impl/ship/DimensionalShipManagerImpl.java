package dev.sebastianb.stardriven.api_impl.ship;

import dev.sebastianb.stardriven.api.ship.DimensionalShip;
import dev.sebastianb.stardriven.api.ship.DimensionalShipManager;
import dev.sebastianb.stardriven.api.team.Team;

import java.util.UUID;

public enum DimensionalShipManagerImpl implements DimensionalShipManager {

    INSTANCE;

    @Override
    public DimensionalShip createDimensionalShip(String shipName, Team team) {
        return null;
    }

    @Override
    public void deleteDimensionalShip(UUID shipId) {

        System.out.println("deleted ship with id: " + shipId + " from database");

    }

    @Override
    public DimensionalShip getDimensionalShip(UUID shipId) {

        return null;
    }
}
