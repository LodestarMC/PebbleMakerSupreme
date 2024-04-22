package com.sammy.block_rummage.data_types;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class ToolActionData extends HeldData {

    public static final HashMap<ToolAction, Ingredient> ITEMS_WITH_ACTIONS = new HashMap<>();

    public final ToolAction toolAction;
    public final float chance;
    public final int durabilityCost;

    public ToolActionData(ToolAction toolAction, float chance, int durabilityCost) {
        this.toolAction = toolAction;
        this.chance = chance;
        this.durabilityCost = durabilityCost;
    }

    @Override
    public boolean matches(Player player, InteractionHand hand, ItemStack heldItem) {
        boolean roll = chance == 1 || player.getRandom().nextFloat() < chance;
        boolean test = heldItem.canPerformAction(toolAction);
        if (roll && test && durabilityCost != 0) {
            heldItem.hurtAndBreak(durabilityCost, player, (e) -> e.broadcastBreakEvent(hand));
        }
        return test;
    }

    @Override
    public @NotNull Ingredient getIngredient() {
        return ITEMS_WITH_ACTIONS.computeIfAbsent(toolAction, (a)->Ingredient.of(ForgeRegistries.ITEMS.getValues().stream().map(Item::getDefaultInstance).filter(i -> i.canPerformAction(a))));
    }
}