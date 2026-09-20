package com.github.ysbbbbbb.kaleidoscopecookery.block.crop;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BonemealSource;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

/**
 * 骨粉右键催熟的接管逻辑。
 * <p>
 * 26.1 的 {@code BoneMealItem.useOn} 在客户端生长成功后返回 {@code PASS}（26.2 才改为客户端也返回 SUCCESS），
 * 客户端 {@code Minecraft.startUseItem} 的手部循环因此认为本次交互未被消费，会继续用副手再发一次方块交互包；
 * 那个包到达服务端时作物已被主手包催熟，于是立刻命中"成熟收获"分支，表现为骨粉"催熟即收获"。
 * 所以骨粉催熟未成熟作物时改由方块自己接管，双端消费交互，阻断副手空手包的触发链。
 */
public final class BonemealInteraction {
    private BonemealInteraction() {
    }

    /**
     * 若手上是骨粉且目标还能被催熟，则完成催熟并返回 true，调用方应立刻返回成功结果。
     * 生长逻辑与粒子、音效、振动均与 {@code BoneMealItem.growCrop} 保持一致。
     */
    public static boolean growCrop(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                   Player player, BonemealableBlock block) {
        if (!stack.is(Items.BONE_MEAL) || !block.isValidBonemealTarget(level, pos, state, BonemealSource.MOB)) {
            return false;
        }
        if (level instanceof ServerLevel serverLevel) {
            if (block.isBonemealSuccess(level, level.getRandom(), pos, state, BonemealSource.MOB)) {
                block.performBonemeal(serverLevel, level.getRandom(), pos, state, BonemealSource.MOB);
            }
            level.levelEvent(LevelEvent.PARTICLES_AND_SOUND_PLANT_GROWTH, pos, 15);
            stack.causeUseVibration(player, GameEvent.ITEM_INTERACT_FINISH);
            stack.consume(1, player);
        }
        return true;
    }
}
