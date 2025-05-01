package net.marsh.soupdup.block.entity;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityType;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.marsh.soupdup.SoupdUp;
import net.marsh.soupdup.block.ModBlocks;
import net.marsh.soupdup.block.entity.custom.SoupBarrelBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    public static final BlockEntityType<SoupBarrelBlockEntity> SOUP_BARREL_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(SoupdUp.MOD_ID, "soup_barrel_be"),
                    FabricBlockEntityTypeBuilder.create(SoupBarrelBlockEntity::new, ModBlocks.SOUP_BARREL).build());
    public static void registerBlockEntities() {
        SoupdUp.LOGGER.info("Registering Block Entities for " + SoupdUp.MOD_ID);
    }
}
