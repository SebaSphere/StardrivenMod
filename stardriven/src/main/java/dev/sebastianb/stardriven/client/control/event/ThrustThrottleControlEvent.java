package dev.sebastianb.stardriven.client.control.event;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

public class ThrustThrottleControlEvent implements ClientTickEvents.EndTick {
    @Override
    public void onEndTick(MinecraftClient client) {

    }
}
