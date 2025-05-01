package net.marsh.soupdup.item;

import net.marsh.soupdup.SoupdUp;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public final class ModTags {
    public static class Items {
        public static final TagKey<Item> SOUPS = createTag("soup");
        private static TagKey<Item> createTag(String name) {
            return TagKey.of(RegistryKeys.ITEM, Identifier.of(SoupdUp.MOD_ID, name));
        }
    }
}
