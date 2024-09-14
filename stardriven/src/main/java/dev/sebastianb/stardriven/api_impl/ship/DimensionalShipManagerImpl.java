package dev.sebastianb.stardriven.api_impl.ship;

import dev.sebastianb.stardriven.api.ship.DimensionalShip;
import dev.sebastianb.stardriven.api.ship.DimensionalShipManager;
import dev.sebastianb.stardriven.api.team.Team;
import dev.sebastianb.stardriven.client.render.dimension.star.DimensionalStarPosition;
import net.minecraft.nbt.NbtCompound;
import net.terradevelopment.terrautil.api.file.NbtFileIO;
import net.terradevelopment.terrautil.api_imp.file.NbtFileIOImpl;

import java.nio.file.Path;
import java.util.UUID;

public enum DimensionalShipManagerImpl implements DimensionalShipManager {

    INSTANCE;

    private NbtFileIO nbt;

    @Override
    public void init(Path path) {
        nbt = NbtFileIOImpl.INSTANCE;
        nbt.setHeaderPath(path);
    }

    @Override
    public DimensionalShip createDimensionalShip(String shipName, Team team, DimensionalStarPosition dimensionalStarPosition) {

        DimensionalShip dimensionalShip = new DimensionalShipImpl(dimensionalStarPosition);
        dimensionalShip.setDimensionShipName(shipName);
        dimensionalShip.setTeam(team);

        UUID uuid = UUID.randomUUID();

        nbt.setWorkingPath("ship/");
        nbt.setFileIdentifier(uuid.toString());

        NbtCompound nbtCompound = new NbtCompound();
        nbtCompound.putUuid("shipId", uuid);
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


        nbt.writeNbtToFile(nbtCompound);

        return dimensionalShip;
    }

    @Override
    public void deleteDimensionalShip(UUID shipId) {



    }

    @Override
    public DimensionalShip getDimensionalShip(UUID shipId) {
        // DimensionalShip dimensionalShip = new DimensionalShipImpl();

        nbt.readNbtFromFile();
        nbt.getFileTag();

        // dimensionalShip.setDimensionShipPosition(new DimensionalStarPosition(0,0,0));

        return null;
    }
}
