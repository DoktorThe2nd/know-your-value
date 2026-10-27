package com.doktorthe2nd.knowyourvalue;

import java.util.ArrayList;
import java.util.List;

import net.neoforged.neoforge.common.ModConfigSpec;

// An example config class. This is not required, but it's a good idea to have one to keep your config organized.
// Demonstrates how to use Neo's config APIs
public class Config {
    public static final List<String> BUILTIN = new ArrayList<>(){{
        add("minecraft:cobblestone=1");
    }};

    private static final ModConfigSpec.Builder COMMON_BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.ConfigValue<List<? extends String>> ITEMS_VALUES = COMMON_BUILDER
            .comment("Predefined item values. Cannot be overridden by calculation. Write in format 'modid:item=value', value >= 0.")
            .defineListAllowEmpty("items_values", BUILTIN, () -> "", Config::validateItemValue);

    static final ModConfigSpec COMMON_SPEC = COMMON_BUILDER.build();

    private static boolean validateItemValue(final Object obj) {
        if (obj instanceof String s) {
            if (!s.contains("=")) return false;
            try {
                return Long.parseLong(s.substring(s.indexOf("=")+1)) >= 0;
            } catch (NumberFormatException | IndexOutOfBoundsException e) {
                return false;
            }
        }
        return false;
    }
}
