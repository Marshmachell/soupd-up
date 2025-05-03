package net.marsh.soupdup;

import net.fabricmc.api.ModInitializer;

import net.marsh.soupdup.block.SoupdUpBlocks;
import net.marsh.soupdup.block.entity.SoupdUpBlockEntities;
import net.marsh.soupdup.item.SoupdUpItemGroups;
import net.marsh.soupdup.item.SoupdUpItems;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SoupdUp implements ModInitializer {
	public static final String MOD_ID = "soupdup";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		SoupdUpItemGroups.registerItemGroups();
		SoupdUpItems.registerModItems();
		SoupdUpBlocks.registerModBlocks();
		SoupdUpBlockEntities.registerBlockEntities();
	}
}