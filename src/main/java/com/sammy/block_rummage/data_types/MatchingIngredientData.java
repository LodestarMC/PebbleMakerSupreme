package com.sammy.block_rummage.data_types;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MatchingIngredientData extends HeldData {
    public final Ingredient ingredient;
    public final float chance;
    public final int durabilityCost;
    public final boolean consumeItemInstead;

    public MatchingIngredientData(Ingredient ingredient, float chance, int durabilityCost, boolean consumeItemInstead) {
        this.ingredient = ingredient;
        this.chance = chance;
        this.durabilityCost = durabilityCost;
        this.consumeItemInstead = consumeItemInstead;
    }

    @Override
    public boolean matches(Player player, InteractionHand hand, ItemStack heldItem) {
        boolean roll = chance == 1 || player.getRandom().nextFloat() < chance;
        boolean test = consumeItemInstead ? (heldItem.getCount() >= durabilityCost && ingredient.test(heldItem)) : ingredient.test(heldItem);
        if (test && durabilityCost != 0) {
            if (consumeItemInstead) {
                if (!player.isCreative() && roll) {
                    heldItem.shrink(durabilityCost);
                }
            }
            else {
                if (roll) {
                    heldItem.hurtAndBreak(durabilityCost, player, (e) -> e.broadcastBreakEvent(hand));
                }
            }
        }
        return test;
    }

    @Override
    public @NotNull Ingredient getIngredient() {
        return ingredient;
    }

    @Override
    public ItemStack getModifiedItemStack(ItemStack stack) {
        if (consumeItemInstead) {
            ItemStack copy = stack.copy();
            copy.setCount(durabilityCost);
            return copy;
        }
        return stack;
    }

    @Override
    public void addTooltipInfoToIngredient(List<Component> tooltip) {
        tooltip.add(1, Component.translatable("block_rummage.jei.consume")
                .withStyle(ChatFormatting.GOLD));
    }
}