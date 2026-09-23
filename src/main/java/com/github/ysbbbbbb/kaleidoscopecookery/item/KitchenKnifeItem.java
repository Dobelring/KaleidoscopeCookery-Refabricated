package com.github.ysbbbbbb.kaleidoscopecookery.item;

import com.github.ysbbbbbb.kaleidoscopecookery.item.template.WithTooltipsItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.NonNull;

public class KitchenKnifeItem extends WithTooltipsItem {

    public KitchenKnifeItem(Properties p, ToolMaterial material, float attackDamageBonus, float attackSpeedBonus) {
        super(p.stacksTo(1).sword(material, attackDamageBonus, attackSpeedBonus), "kitchen_knife");
    }



    @Override
    public boolean canDestroyBlock(@NonNull ItemStack itemStack, @NonNull BlockState blockState, @NonNull Level level, @NonNull BlockPos blockPos, @NonNull LivingEntity livingEntity) {
        return super.canDestroyBlock(itemStack, blockState, level, blockPos, livingEntity);
    }

    @Override
    public void hurtEnemy(@NonNull ItemStack stack, @NonNull LivingEntity target, @NonNull LivingEntity attacker) {


    }

    @Override
    public void postHurtEnemy(ItemStack stack, @NonNull LivingEntity target, @NonNull LivingEntity attacker) {
        stack.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND);
    }
}
