package net.marsh.soupdup;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public final class SoupdUpTags {
    public static class Items {
        public static final TagKey<Item> SOUPS = createTag("soup");
        private static TagKey<Item> createTag(String name) {
            return TagKey.of(RegistryKeys.ITEM, Identifier.of(SoupdUp.MOD_ID, name));
        }
    }
    public static class Blocks {
        public static final TagKey<Block> SOUP_BARRELS = createTag("soup_barrels");
        private static TagKey<Block> createTag(String name) {
            return TagKey.of(RegistryKeys.BLOCK, Identifier.of(SoupdUp.MOD_ID, name));
        }
    }
}
