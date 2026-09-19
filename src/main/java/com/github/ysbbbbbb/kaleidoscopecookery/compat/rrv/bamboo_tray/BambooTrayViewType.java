package com.github.ysbbbbbb.kaleidoscopecookery.compat.rrv.bamboo_tray;

import cc.cassian.rrv.api.recipe.ReliableClientRecipeType;
import cc.cassian.rrv.common.recipe.inventory.RecipeViewMenu;
import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class BambooTrayViewType implements ReliableClientRecipeType {
    public static final BambooTrayViewType INSTANCE = new BambooTrayViewType();

    private BambooTrayViewType() {
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("emi.category.kaleidoscope_cookery.bamboo_tray");
    }

    @Override
    public int getDisplayWidth() {
        return 118;
    }

    @Override
    public int getDisplayHeight() {
        return 58;
    }

    @Override
    public @Nullable Identifier getGuiTexture() {
        return Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "textures/gui/eiv/bamboo_tray.png");
    }

    @Override
    public int getSlotCount() {
        return 2;
    }

    @Override
    public void placeSlots(RecipeViewMenu.SlotDefinition slotDefinition) {
        slotDefinition.addItemSlot(0, 31, 20);
        slotDefinition.addItemSlot(1, 88, 21);
    }

    @Override
    public Identifier getId() {
        return Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "bamboo_tray");
    }

    @Override
    public ItemStack getIcon() {
        return ModItems.BAMBOO_TRAY.getDefaultInstance();
    }

    @Override
    public List<ItemStack> getCraftReferences() {
        return List.of(this.getIcon());
    }
}
