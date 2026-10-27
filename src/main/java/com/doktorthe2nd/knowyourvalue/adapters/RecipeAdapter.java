package com.doktorthe2nd.knowyourvalue.adapters;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;

import java.util.*;

public class RecipeAdapter extends Adapter {
    private final Map<Item, List<RecipeHolder<?>>> index;
    private final HolderLookup.Provider registries;
    private final Set<Item> visiting = new HashSet<>();

    public RecipeAdapter(RecipeManager recipeManager, HolderLookup.Provider registries) {
        this.registries = registries;
        this.index = new HashMap<>();
        for (RecipeHolder<?> recipe : recipeManager.getRecipes()) {
            Item item = recipe.value().getResultItem(registries).getItem();
            List<RecipeHolder<?>> list = index.computeIfAbsent(item, k -> new ArrayList<>());
            list.add(recipe);
        }
    }

    @Override
    public Long getValue(Item item) {
        Long value = Long.MAX_VALUE;
        if (!visiting.add(item)) return null;

        try {
            List<RecipeHolder<?>> recipes = index.get(item);
            if (recipes == null || recipes.isEmpty()) return null;

            long best = Long.MAX_VALUE;

            for (RecipeHolder<?> holder : recipes) {
                Recipe<?> recipe = holder.value();

                long cost = 0;
                boolean ok = true;

                for (Ingredient ingredient : recipe.getIngredients()) {
                    // Пустые слоты (Ingredient.EMPTY) не учитываем
                    ItemStack[] stacks = ingredient.getItems();
                    if (stacks.length == 0) continue;

                    Long min = minIngredientValue(stacks);
                    if (min == null) { ok = false; break; }
                    cost += min;
                }

                if (!ok) continue;

                int count = recipe.getResultItem(registries).getCount();
                if (count <= 0) continue;

                long perItem = cost / count;
                if (perItem > 0 && perItem < best) best = perItem;
            }
            if (best == Long.MAX_VALUE) return null;
            return best;
        } finally {
            visiting.remove(item);
        }
    }

    private Long minIngredientValue(ItemStack[] stacks) {
        Long best = null;
        for (ItemStack stack : stacks) {
            Long v = getValue(stack.getItem());
            if (v != null && (best == null || v < best)) best = v;
        }
        return best;
    }
}
