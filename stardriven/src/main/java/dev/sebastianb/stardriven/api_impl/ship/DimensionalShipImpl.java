package dev.sebastianb.stardriven.api_impl.ship;

import dev.sebastianb.stardriven.api.ship.DimensionalShip;
import dev.sebastianb.stardriven.api.team.Team;
import dev.sebastianb.stardriven.client.render.dimension.star.DimensionalStarPosition;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class DimensionalShipImpl implements DimensionalShip, Comparable<DimensionalShip> {

    private DimensionalStarPosition dimensionalStarPosition;
    private final UUID shipID;
    private String name;

    public DimensionalShipImpl(DimensionalStarPosition dimensionalStarPosition, UUID shipID, String name) {
        this.dimensionalStarPosition = dimensionalStarPosition;
        this.shipID = shipID;
        this.name = name;
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
        this.name = name;

    }

    @Override
    public String getDimensionShipName() {
        return name;
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

    @Override
    public UUID getShipUUID() {
        return shipID;
    }

    @Override
    public int compareTo(@NotNull DimensionalShip otherShip) {
        // This is copied and pasted from the UUID class
        // The ordering is intentionally set up so that the UUIDs
        // can simply be numerically compared as two numbers
        int mostSigBits = Long.compare(this.getShipUUID().getMostSignificantBits(), otherShip.getShipUUID().getMostSignificantBits());
        return mostSigBits != 0 ? mostSigBits : Long.compare(this.getShipUUID().getLeastSignificantBits(), otherShip.getShipUUID().getLeastSignificantBits());
    }
}
