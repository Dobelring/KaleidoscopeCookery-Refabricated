package com.github.ysbbbbbb.kaleidoscopecookery.compat.everycomp;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.compat.everycomp.module.KaleidoscopeCookeryModule;
import net.mehvahdjukaar.every_compat.api.EveryCompatAPI;

public class EveryCompatCompat {
    public static void init() {
        EveryCompatAPI.registerOptionalModule(
                KaleidoscopeCookery.MOD_ID,
                () -> KaleidoscopeCookeryModule.class
        );
    }
}
