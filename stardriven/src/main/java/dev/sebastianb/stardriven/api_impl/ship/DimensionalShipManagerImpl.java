package dev.sebastianb.stardriven.api_impl.ship;

import dev.sebastianb.stardriven.api.ship.DimensionalShip;
import dev.sebastianb.stardriven.api.ship.DimensionalShipManager;
import dev.sebastianb.stardriven.api.team.Team;
import dev.sebastianb.stardriven.client.render.dimension.star.DimensionalStarPosition;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.WorldSavePath;
import net.minecraft.world.World;
import net.terradevelopment.terrautil.api.file.NbtFileIO;
import net.terradevelopment.terrautil.api_imp.file.NbtFileIOImpl;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.*;

public enum DimensionalShipManagerImpl implements DimensionalShipManager {

    INSTANCE;



    private NbtFileIO nbt;

    // TODO: comparable by UUID (to make it fast to retrieve ships via binary search)
    TreeMap<UUID, DimensionalShip> dimensionalShips = new TreeMap<>();

    @Override
    public void init(World world, UUID shipUUID, boolean shouldLoadNBT) {
        Path path = world.getServer()
                .getSavePath(WorldSavePath.ROOT)
                .resolve("dimensions/stardriven/interstellar-ship_" + shipUUID + "/ship");

        nbt = new NbtFileIOImpl();
        nbt.setHeaderPath(path);

        nbt.setFileIdentifier(shipUUID.toString());

        nbt.readNbtFromFile();

        // create dimension ship object
        if (shouldLoadNBT) {
            NbtCompound nbtCompound = nbt.getFileTag();
            try {
                NbtCompound shipPosition = nbtCompound.getCompound("shipPosition");
                DimensionalStarPosition dimensionalStarPosition = new DimensionalStarPosition(
                        shipPosition.getDouble("x"), shipPosition.getDouble("y"), shipPosition.getDouble("z"),
                        shipPosition.getDouble("pitch"), shipPosition.getDouble("roll"), shipPosition.getDouble("yaw")
                );

                DimensionalShip dimensionalShip = createDimensionalShipObject(
                        nbtCompound.getString("shipName"),
                        nbtCompound.getUuid("shipId"),
                        dimensionalStarPosition

                );

                dimensionalShips.put(shipUUID, dimensionalShip);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    @Override
    public DimensionalShip createDimensionalShip(String shipName, UUID shipUUID, UUID teamUUID, DimensionalStarPosition dimensionalStarPosition) {


        DimensionalShip dimensionalShip =
                createDimensionalShipObject(shipName, shipUUID, dimensionalStarPosition);

        dimensionalShips.put(shipUUID, dimensionalShip);

        return dimensionalShip;
    }

    private @NotNull DimensionalShip createDimensionalShipObject(String shipName, UUID shipUUID, DimensionalStarPosition dimensionalStarPosition) {
        DimensionalShip dimensionalShip = new DimensionalShipImpl(nbt, dimensionalStarPosition, shipUUID, shipName);
        dimensionalShip.setDimensionShipName(shipName);

        // get Team from UUID
        // dimensionalShip.setTeam(team);


        this.loadShipNBT(shipName, shipUUID, dimensionalStarPosition);

        // tracks the nbt changes
        NbtFileIO.trackFile(nbt);
        return dimensionalShip;
    }

    @Override
    public NbtFileIO loadShipNBT(String shipName, UUID shipUUID, DimensionalStarPosition dimensionalStarPosition) {

        DimensionalShip dimensionalShip = dimensionalShips.get(shipUUID);

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

        nbt.setWorkingPath("./");
        nbt.setHeaderPath(nbt.getHeaderPath().getParent().getParent()
                .resolve("interstellar-ship_" + shipUUID + "/ship/"));

        nbt.setFileIdentifier(String.valueOf(shipUUID));
        System.out.println("ASASASE");
        System.out.println(nbt.getFileIdentifier());
        nbt.setFileTag(nbtCompound);
        nbt.writeNbtToFile(nbtCompound);

        return nbt;
    }

    @Override
    public void deleteDimensionalShip(UUID shipId) {
        DimensionalShip searchShip = DimensionalShipImpl.getUUID(shipId);
        // TODO: delete logical instance of ship + nbt data + move actual loaded world to a backup
    }

    @Override
    public DimensionalShip getDimensionalShip(UUID shipId) {
        return dimensionalShips.getOrDefault(shipId, null);
    }

    @Override
    public TreeMap<UUID, DimensionalShip> getAllDimensionalShips() {
        return dimensionalShips;
    }
}
