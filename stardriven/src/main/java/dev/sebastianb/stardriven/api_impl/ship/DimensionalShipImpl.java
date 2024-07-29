package dev.sebastianb.stardriven.api_impl.ship;

import dev.sebastianb.stardriven.api.ship.DimensionalShip;
import dev.sebastianb.stardriven.api.team.Team;
import dev.sebastianb.stardriven.client.render.dimension.star.DimensionalStarPosition;
import net.minecraft.util.math.Vec3d;

import java.util.UUID;

public class DimensionalShipImpl implements DimensionalShip {

    DimensionalStarPosition dimensionalStarPosition;


    public DimensionalShipImpl(DimensionalStarPosition dimensionalStarPosition) {
        this.dimensionalStarPosition = dimensionalStarPosition;
    }

    @Override
    public void setDimensionShipPosition(DimensionalStarPosition dimensionalStarPosition) {
        this.dimensionalStarPosition = dimensionalStarPosition;
    }

    @Override
    public DimensionalStarPosition getDimensionShipPosition() {
        return dimensionalStarPosition;
    }

    @Override
    public void setDimensionShipName(String name) {

    }

    @Override
    public String getDimensionShipName() {
        return "";
    }

    @Override
    public void setTeam(Team team) {

    }

    @Override
    public Team getTeam() {
        return null;
    }

    @Override
    public boolean containsPlayer(UUID uuid) {
        return false;
    }
}
