package dev.sebastianb.stardriven.client.control;

import dev.sebastianb.stardriven.client.control.event.ThrustThrottleControlEvent;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

public class ThrustControl {

    String keyTranslation;
    InputUtil.Type keyType;
    int keyboardConstant;

    KeyBinding keyBinding;


    public ThrustControl(String keyTranslation, InputUtil.Type keyType, int keyboardConstant) {
        this.keyTranslation = keyTranslation;
        this.keyType = keyType;
        this.keyboardConstant = keyboardConstant;

        keyBinding = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key.stardriven.ship_controls." + keyTranslation,
                        keyType,
                        keyboardConstant,
                        "category.stardriven.ship_controls"
                )
        );

    }

    public KeyBinding getKeyBinding() {
        return keyBinding;
    }

    public void registerControlEvent(ClientTickEvents.EndTick controlEvent) {
        ClientTickEvents.END_CLIENT_TICK.register(controlEvent);
    }


}
