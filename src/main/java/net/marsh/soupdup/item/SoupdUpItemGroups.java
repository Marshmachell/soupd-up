package net.marsh.soupdup.item;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.marsh.soupdup.SoupdUp;
import net.marsh.soupdup.block.SoupdUpBlocks;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class SoupdUpItemGroups {
    public static final ItemGroup SOUPD_UP_ITEMS_GROUP = Registry.register(Registries.ITEM_GROUP,
            Identifier.of(SoupdUp.MOD_ID, "soupd_up_items"),
            FabricItemGroup.builder().icon(() -> new ItemStack(SoupdUpItems.HEARTY_SALMON_SOUP))
                    .displayName(Text.translatable("itemgroup.soupdup.soupd_up_items"))
                    .entries((displayContext, entries) -> {
                        entries.add(SoupdUpBlocks.SOUP_BARREL);
                        entries.add(SoupdUpItems.SALMON_SOUP);
                        entries.add(SoupdUpItems.HEARTY_SALMON_SOUP);
                        entries.add(SoupdUpItems.COD_SOUP);
                        entries.add(SoupdUpItems.HEARTY_COD_SOUP);
                        entries.add(SoupdUpItems.SEA_SOUP);
                        entries.add(SoupdUpItems.CHORUS_SOUP);
                    })
                    .build());
    public static void registerItemGroups() {
        SoupdUp.LOGGER.info("Registering Item Groups for " + SoupdUp.MOD_ID);
    }
}
