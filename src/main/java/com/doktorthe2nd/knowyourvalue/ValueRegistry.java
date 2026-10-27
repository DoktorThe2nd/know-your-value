package com.doktorthe2nd.knowyourvalue;

import com.doktorthe2nd.knowyourvalue.adapters.Adapter;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValueRegistry {
    public static ValueRegistry INSTANCE = new ValueRegistry();

    private final Map<Item, Long> map = new HashMap<>();
    private final List<Adapter> adapters = new ArrayList<>();

    public void addAdapter(Adapter adapter) {
        adapters.add(adapter);
    }
    public void clearAdapters() {
        adapters.clear();
    }

    public void invalidateCache() {
        map.clear();
    }

    private Long askConfig(Item item) {
        ResourceLocation loc = BuiltInRegistries.ITEM.getKey(item);
        if (loc == null) return null;
        String res_s = loc.getNamespace() + ":" + loc.getPath();
        for (String s : Config.ITEMS_VALUES.get()) {
            if (res_s.equals(s.substring(0, s.indexOf("=")))) {
                return Long.parseLong(s.substring(s.indexOf("=")+1));
            }
        }
        return null;
    }

    private Long evaluate(Item item) {
        Long value = askConfig(item);
        if (value != null) return value;
        for (Adapter adapter : adapters) {
            Long new_value = adapter.getValue(item);
            if (new_value != null && (value == null || new_value < value))
                value = new_value;
        }
        return value;
    }

    public Long getValue(Item item) {
        Long value = map.get(item);
        if (value == null) {
            value = evaluate(item);
            if (value != null) map.put(item, value);
        }
        return value;
    }
}
