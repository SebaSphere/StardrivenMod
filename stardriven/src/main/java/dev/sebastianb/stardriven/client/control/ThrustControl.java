package dev.sebastianb.stardriven.client.control;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public class ThrustControl {

    String keyTranslation;
    InputUtil.Type keyType;
    int keyboardConstant;

    public ThrustControl(String keyTranslation, InputUtil.Type keyType, int keyboardConstant) {
        this.keyTranslation = keyTranslation;
        this.keyType = keyType;
        this.keyboardConstant = keyboardConstant;
    }

    public KeyBinding getKeyBinding() {
        return KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key.stardriven.ship_controls." + keyTranslation,
                        keyType,
                        keyboardConstant,
                        "category.stardriven.ship_controls"
                )
        );
    }

    public static class ThrustTickEvent implements ClientTickEvents.EndTick {

        @Override
        public void onEndTick(MinecraftClient client) {

        }
    }

    public void tickEvent() {
        ClientTickEvents.END_CLIENT_TICK.register(new ThrustTickEvent());
    }


}
