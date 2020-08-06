package com.ashindigo.musicexpansion.recipe;

import com.ashindigo.musicexpansion.MusicExpansion;
import com.ashindigo.musicexpansion.accessor.MusicDiscItemAccessor;
import com.ashindigo.musicexpansion.item.ItemCustomRecord;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

@SuppressWarnings("deprecation")
// Used for updating old records to the new format
public class UpdateRecordRecipe extends SpecialCraftingRecipe {

    public UpdateRecordRecipe(Identifier id) {
        super(id);
    }

    @Override
    public boolean matches(CraftingInventory inv, World world) {
        ItemStack itemStack = ItemStack.EMPTY;

        for (int i = 0; i < inv.size(); ++i) {
            ItemStack itemStack2 = inv.getStack(i);
            if (!itemStack2.isEmpty()) {
                if (itemStack2.getItem() instanceof ItemCustomRecord) {
                    if (!itemStack.isEmpty()) {
                        return false;
                    }

                    itemStack = itemStack2;
                }
            }
        }

        return !itemStack.isEmpty();
    }

    @Override
    public ItemStack craft(CraftingInventory inv) {
        ItemStack newRecord = new ItemStack(MusicExpansion.customDisc);
        for (int i = 0; i < inv.size(); i++) {
            if (inv.getStack(i).getItem() instanceof ItemCustomRecord) {
                ItemCustomRecord record = (ItemCustomRecord) inv.getStack(i).getItem();
                CompoundTag tag = newRecord.getOrCreateTag();
                tag.putString("track", ((MusicDiscItemAccessor)record).musicexpansion_getSound().getId().toString());
                newRecord.setTag(tag);
            }
        }
        return newRecord;
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return MusicExpansion.UPDATE_DISC;
    }
}
