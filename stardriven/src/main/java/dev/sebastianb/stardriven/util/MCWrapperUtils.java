package dev.sebastianb.stardriven.util;

import dev.sebastianb.stardriven.Stardriven;
import net.minecraft.util.Identifier;

public class MCWrapperUtils {

    public static Identifier id(String modId, String path) {
        return new Identifier(modId, path);
    }

    public static Identifier id(String path) {
        return id(Stardriven.MOD_ID, path);
    }

}
