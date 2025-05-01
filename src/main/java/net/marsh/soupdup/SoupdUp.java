package net.marsh.soupdup;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.marsh.soupdup.block.ModBlocks;
import net.marsh.soupdup.block.entity.ModBlockEntities;
import net.marsh.soupdup.item.ModItemGroups;
import net.marsh.soupdup.item.ModTags;
import net.marsh.soupdup.item.ModItems;
import net.minecraft.client.render.RenderLayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SoupdUp implements ModInitializer {
	public static final String MOD_ID = "soupdup";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		ModItemGroups.registerItemGroups();
		ModItems.registerModItems();
		ModBlocks.registerModBlocks();
		ModBlockEntities.registerBlockEntities();
	}
}