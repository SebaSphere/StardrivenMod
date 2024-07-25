package dev.sebastianb.stardriven.client.control;

import dev.sebastianb.stardriven.client.control.event.ThrustThrottleControlEvent;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class KeybindControl {


    public static KeyBinding thrustForward;

    public static void register() {

        ThrustControl thrustControl = new ThrustControl(
                "key.stardriven.thrust_forward",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_Z
        );

        thrustForward = thrustControl.getKeyBinding();
        thrustControl.registerControlEvent(new ThrustThrottleControlEvent(thrustForward));


    }

}
