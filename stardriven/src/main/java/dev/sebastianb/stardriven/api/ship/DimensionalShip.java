package dev.sebastianb.stardriven.api.ship;

import dev.sebastianb.stardriven.api.team.Team;
import dev.sebastianb.stardriven.client.render.dimension.star.DimensionalStarPosition;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.terradevelopment.terrautil.api.file.NbtFileIO;

import java.util.UUID;

public interface DimensionalShip extends Comparable<DimensionalShip> {

    void setLogicalWorld(ServerWorld world);

    void setDimensionShipPosition(DimensionalStarPosition dimensionalStarPosition);

    DimensionalStarPosition getDimensionShipPosition();

    void setDimensionShipName(String name);

    String getDimensionShipName();

    void setTeam(Team team);

    Team getTeam();

    boolean containsPlayer(UUID uuid);

    UUID getShipUUID();

    NbtFileIO attachedNBT();

}
