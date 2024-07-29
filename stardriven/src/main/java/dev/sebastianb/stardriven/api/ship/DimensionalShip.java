package dev.sebastianb.stardriven.api.ship;

import dev.sebastianb.stardriven.api.team.Team;
import dev.sebastianb.stardriven.client.render.dimension.star.DimensionalStarPosition;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;

public interface DimensionalShip {

    void setDimensionShipPosition(DimensionalStarPosition dimensionalStarPosition);

    DimensionalStarPosition getDimensionShipPosition();

    void setDimensionShipName(String name);

    String getDimensionShipName();

    void setTeam(Team team);

    Team getTeam();

    boolean containsPlayer(UUID uuid);

}
