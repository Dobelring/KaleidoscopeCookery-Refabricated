package com.github.ysbbbbbb.kaleidoscopecookery.item;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.UseOnContext;
import org.jetbrains.annotations.NotNull;

public class EmptyCupItem extends BlockItem {
    public EmptyCupItem() {
        super(ModBlocks.EMPTY_CUP, new Properties().stacksTo(16));
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        return super.useOn(context);
    }
}
