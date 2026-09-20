package com.github.ysbbbbbb.kaleidoscopecookery.util.neo;

import com.github.ysbbbbbb.kaleidoscopecookery.api.event.CropGrowEvent;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public final class Hooks {
    public static boolean onCropsGrowPre(Level level, BlockPos pos, BlockState state, boolean def) {
        CropGrowEvent.Pre ev = new CropGrowEvent.Pre(level, pos, state);
        ModEvents.CROP_GROW_PRE.invoker().beforeGrow(ev);
        return ev.getResult() == CropGrowEvent.Pre.Result.GROW || ev.getResult() == CropGrowEvent.Pre.Result.DEFAULT && def;
    }

    public static void onCropsGrowPost(Level level, BlockPos pos, BlockState state) {
        var ev = new CropGrowEvent.Post(level, pos, state, level.getBlockState(pos));
        ModEvents.CROP_GROW_POST.invoker().afterGrow(ev);
    }
}