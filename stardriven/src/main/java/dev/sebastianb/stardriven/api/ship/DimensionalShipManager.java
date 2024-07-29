package dev.sebastianb.stardriven.api.ship;


import dev.sebastianb.stardriven.api.team.Team;
import dev.sebastianb.stardriven.client.render.dimension.star.DimensionalStarPosition;
import net.minecraft.world.World;

import java.nio.file.Path;
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

    /**
     * Initializes the DimensionalShipManager. This method is typically called once upon creation of the manager.
     */
    void init(Path path);

    /**
     * Creates a new dimensional ship with the given name and team.
     *
     * @param shipName the name to be assigned to the new dimensional ship
     * @param team     the team to be assigned to the new dimensional ship
     * @return the newly created dimensional ship
     */
    DimensionalShip createDimensionalShip(String shipName, Team team, DimensionalStarPosition dimensionalStarPosition);

    /**
     * Deletes the dimensional ship associated with the provided UUID.
     *
     * @param shipId the UUID of the dimensional ship to be deleted
     */
    void deleteDimensionalShip(UUID shipId);

    /**
     * Retrieves the dimensional ship associated with the provided UUID.
     *
     * @param shipId the UUID of the dimensional ship to retrieve
     * @return the retrieved dimensional ship if found, null otherwise
     */
    DimensionalShip getDimensionalShip(UUID shipId);


}
