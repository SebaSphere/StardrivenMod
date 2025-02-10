package dev.sebastianb.stardriven.api_impl.ship;

import dev.sebastianb.stardriven.Stardriven;
import dev.sebastianb.stardriven.api.ship.DimensionalShip;
import dev.sebastianb.stardriven.api.ship.DimensionalShipManager;
import dev.sebastianb.stardriven.api.team.Team;
import dev.sebastianb.stardriven.client.render.dimension.star.DimensionalStarPosition;
import dev.sebastianb.stardriven.networking.StardrivenNetworking;
import io.netty.buffer.Unpooled;
import lol.bai.badpackets.api.PacketSender;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.terradevelopment.terrautil.api.file.NbtFileIO;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class DimensionalShipImpl implements DimensionalShip, Comparable<DimensionalShip> {

    private DimensionalStarPosition dimensionalStarPosition;
    private final UUID shipID;
    private String name;

    private NbtFileIO nbtFileIO;
    private ServerWorld world;

    public DimensionalShipImpl(NbtFileIO nbtFileIO, DimensionalStarPosition dimensionalStarPosition, UUID shipID, String name) {
        this.nbtFileIO = nbtFileIO;
        this.dimensionalStarPosition = dimensionalStarPosition;
        this.shipID = shipID;
        this.name = name;
    }

    public DimensionalShipImpl(UUID shipID) {
        this.shipID = shipID;
    }

    public static DimensionalShip getUUID(UUID shipID) {
        return new DimensionalShipImpl(shipID);
    }

    @Override
    public void setLogicalWorld(ServerWorld world) {
        this.world = world;
    }

    @Override
    public void setDimensionShipPosition(DimensionalStarPosition dimensionalStarPosition) {
        this.dimensionalStarPosition = dimensionalStarPosition;
        // TODO: only send packets to those in space
        world.getPlayers().forEach(player -> {
            PacketByteBuf packetByteBuf = new PacketByteBuf(Unpooled.buffer());
            packetByteBuf.writeUuid(getShipUUID());
            packetByteBuf.writeDouble(dimensionalStarPosition.getX());
            packetByteBuf.writeDouble(dimensionalStarPosition.getY());
            packetByteBuf.writeDouble(dimensionalStarPosition.getZ());

            PacketSender.s2c(player).send(
                    StardrivenNetworking.INTERSTELLAR_SKYBOX_POSITION_UPDATE,
                    packetByteBuf);
        });

        try {
            reloadNBT();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public DimensionalStarPosition getDimensionShipPosition() {
        return dimensionalStarPosition;
    }

    @Override
    public void setDimensionShipName(String name) {
        this.name = name;
        try {
            reloadNBT();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void reloadNBT() {
        DimensionalShipManager dimensionalShipManager = Stardriven.API.getDimensionalShipManager();


        System.out.println("AASdd2d");
        System.out.println(getShipUUID());
        System.out.println(attachedNBT());


        NbtFileIO.untrackFile(attachedNBT());
        this.nbtFileIO = dimensionalShipManager.loadShipNBT(this.name, this.getShipUUID(), this.getDimensionShipPosition());
        NbtFileIO.trackFile(attachedNBT());
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
    public NbtFileIO attachedNBT() {
        return nbtFileIO;
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
