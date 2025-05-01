package net.marsh.soupdup.block;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.marsh.soupdup.SoupdUp;
import net.marsh.soupdup.block.custom.SoupBarrelBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

public class ModBlocks {

    public static final Block SOUP_BARREL = registerBlock("soup_barrel",
            new SoupBarrelBlock(AbstractBlock.Settings.create()
                    .mapColor(MapColor.OAK_TAN)
                    .instrument(NoteBlockInstrument.BASS)
                    .strength(2.5f)
                    .requiresTool()
                    .sounds(BlockSoundGroup.WOOD)
                    .registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(SoupdUp.MOD_ID, "soup_barrel")))
            ), RenderLayer.getCutout());

    private static Block registerBlock(String name, Block block, RenderLayer renderLayer) {
        registerBlockItem(name, block);
        BlockRenderLayerMap.INSTANCE.putBlock(block, renderLayer);
        return Registry.register(Registries.BLOCK, Identifier.of(SoupdUp.MOD_ID, name), block);
    }

    private static void registerBlockItem(String name, Block block) {
        Registry.register(Registries.ITEM, Identifier.of(SoupdUp.MOD_ID, name),
                new BlockItem(block, new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(SoupdUp.MOD_ID, name)))));
    }

    public static void registerModBlocks() {
        SoupdUp.LOGGER.info("Registering Mod Blocks for " + SoupdUp.MOD_ID);
    }
}
