package dev.sebastianb.stardriven.networking;

import dev.sebastianb.stardriven.util.MCWrapperUtils;
import lol.bai.badpackets.api.play.PlayPackets;
import net.minecraft.util.Identifier;

public abstract class StardrivenNetworking {

    public static final Identifier INTERSTELLAR_SKYBOX_INITIALIZER = MCWrapperUtils.id("interstellar_skybox_initializer");

    public static final Identifier INTERSTELLAR_SKYBOX_POSITION_UPDATE = MCWrapperUtils.id("interstellar_skybox_update");

    static {
        PlayPackets.registerClientChannel(INTERSTELLAR_SKYBOX_INITIALIZER);
        PlayPackets.registerClientChannel(INTERSTELLAR_SKYBOX_POSITION_UPDATE);
    }

}
