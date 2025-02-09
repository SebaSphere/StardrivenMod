package dev.sebastianb.stardriven.api_impl.ship;

import dev.sebastianb.stardriven.api.ship.DimensionalShip;
import dev.sebastianb.stardriven.api.ship.DimensionalShipManager;
import dev.sebastianb.stardriven.api.team.Team;
import dev.sebastianb.stardriven.client.render.dimension.star.DimensionalStarPosition;
import net.minecraft.nbt.NbtCompound;
import net.terradevelopment.terrautil.api.file.NbtFileIO;
import net.terradevelopment.terrautil.api_imp.file.NbtFileIOImpl;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public enum DimensionalShipManagerImpl implements DimensionalShipManager {

    INSTANCE;


    private final List<DimensionalShip> dimensionalShips = new ArrayList<>();


    private NbtFileIO nbt;

    @Override
    public void init(Path path) {
        nbt = new NbtFileIOImpl();
        nbt.setHeaderPath(path);
    }

    @Override
    public DimensionalShip createDimensionalShip(String shipName, UUID shipUUID, UUID teamUUID, DimensionalStarPosition dimensionalStarPosition) {


        DimensionalShip dimensionalShip = new DimensionalShipImpl(dimensionalStarPosition);
        dimensionalShip.setDimensionShipName(shipName);

        // get Team from UUID
        // dimensionalShip.setTeam(team);

        nbt.setFileIdentifier(shipUUID.toString());

        NbtCompound nbtCompound = new NbtCompound();
        nbtCompound.putUuid("shipId", shipUUID);
        // nbtCompound.putUuid("teamId", team.getUUID()); // FIXME: add team UUID once implemented
        nbtCompound.putString("shipName", shipName);

        NbtCompound shipPosition = new NbtCompound();
        shipPosition.putDouble("x", dimensionalStarPosition.getX());
        shipPosition.putDouble("y", dimensionalStarPosition.getY());
        shipPosition.putDouble("z", dimensionalStarPosition.getZ());
        shipPosition.putDouble("pitch", dimensionalStarPosition.getPitch());
        shipPosition.putDouble("yaw", dimensionalStarPosition.getYaw());
        shipPosition.putDouble("roll", dimensionalStarPosition.getRoll());

        nbtCompound.put("shipPosition", shipPosition);
        System.out.println("work" + nbt.getWorkingPath());

        nbt.setWorkingPath("ship/");
        nbt.setFileTag(nbtCompound);
        nbt.writeNbtToFile(nbtCompound);

        System.out.println("head" + nbt.getHeaderPath());

        NbtFileIO.trackFile(nbt);

        return dimensionalShip;
    }

    @Override
    public void deleteDimensionalShip(UUID shipId) {


    }

    @Override
    public DimensionalShip getDimensionalShip(UUID shipId) {

        return null;
    }
}
