package dev.sebastianb.stardriven.client.control.event;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;

public class ThrustThrottleControlEvent implements ClientTickEvents.EndTick {

    KeyBinding keyBinding;

    public ThrustThrottleControlEvent(KeyBinding keyBinding) {
        this.keyBinding = keyBinding;
    }

    @Override
    public void onEndTick(MinecraftClient client) {
        if (keyBinding.isPressed()) {
            System.out.println("Is pressed");
        }
    }
}
