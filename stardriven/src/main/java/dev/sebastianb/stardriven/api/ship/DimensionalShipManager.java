package dev.sebastianb.stardriven.api.ship;


import dev.sebastianb.stardriven.api.team.Team;
import net.minecraft.world.World;

import java.util.UUID;

/**
 * The DimensionalShipManager interface represents a manager for ships, which are represented as dimensions
 * It provides methods to create, retrieve, update, and delete dimensional ships,
 * as well as perform various operations related to dimensional ships.
 *
 * This interface does not define the implementation details for managing the dimensional ships,
 * but rather acts as a contract that any implementation of DimensionalShipManager must adhere to.
 *
 * Wherever a dimensional ship ID is used, it refers to a unique identifier that identifies a specific dimensional ship.
 * Similarly, wherever a dimension ID is used, it refers to a unique identifier that identifies a specific dimension.
 *
 */
public interface DimensionalShipManager {

    DimensionalShip createDimensionalShip(String shipName, Team team);
    void deleteDimensionalShip(UUID shipId);
    DimensionalShip getDimensionalShip(UUID shipId);


}
