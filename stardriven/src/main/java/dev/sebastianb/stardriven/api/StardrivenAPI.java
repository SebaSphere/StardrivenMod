package dev.sebastianb.stardriven.api;

public class StardrivenAPI {

    private static API instance;

    public static void _init(API instance) {
        if (StardrivenAPI.instance != null) {
            throw new IllegalStateException("can't init more than once!");
        }
        StardrivenAPI.instance = instance;
    }

    public interface API {

    }

}
