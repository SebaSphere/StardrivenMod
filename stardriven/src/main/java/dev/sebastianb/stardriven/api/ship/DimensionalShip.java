package dev.sebastianb.stardriven.api.ship;

import dev.sebastianb.stardriven.api.team.Team;
import net.minecraft.util.math.Vec3d;

public interface DimensionalShip {

    void setDimensionShipPosition(Vec3d position);

    Vec3d getDimensionShipPosition();

    // Set the name of the dimensional ship
    void setDimensionShipName(String name);

    // Get the name of the dimensional ship
    String getDimensionShipName();

    // Set the team of the dimensional ship
    void setTeam(Team team);

    // Get the team of the dimensional ship
    Team getTeam();

}
