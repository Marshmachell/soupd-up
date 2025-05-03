package net.marsh.soupdup.item;

import net.marsh.soupdup.SoupdUp;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

import static net.minecraft.item.Items.BOWL;

public class SoupdUpItems {
    public static final Item SALMON_SOUP = registerItem("salmon_soup", new Item(new Item.Settings().food(SoupdUpFoodComponents.SALMON_SOUP).maxCount(1).useRemainder(BOWL).registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(SoupdUp.MOD_ID,"salmon_soup")))));
    public static final Item HEARTY_SALMON_SOUP = registerItem("hearty_salmon_soup", new Item(new Item.Settings().food(SoupdUpFoodComponents.HEARTY_SALMON_SOUP).maxCount(1).useRemainder(BOWL).registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(SoupdUp.MOD_ID,"hearty_salmon_soup")))));
    public static final Item COD_SOUP = registerItem("cod_soup", new Item(new Item.Settings().food(SoupdUpFoodComponents.COD_SOUP).maxCount(1).useRemainder(BOWL).registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(SoupdUp.MOD_ID,"cod_soup")))));
    public static final Item HEARTY_COD_SOUP = registerItem("hearty_cod_soup", new Item(new Item.Settings().food(SoupdUpFoodComponents.HEARTY_COD_SOUP).useRemainder(BOWL).maxCount(1).registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(SoupdUp.MOD_ID,"hearty_cod_soup")))));
    public static final Item SEA_SOUP = registerItem("sea_soup", new Item(new Item.Settings().food(SoupdUpFoodComponents.SEA_SOUP, SoupdUpConsumableComponents.SEA_SOUP).useRemainder(BOWL).maxCount(1).registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(SoupdUp.MOD_ID,"sea_soup")))));
    public static final Item CHORUS_SOUP = registerItem("chorus_soup", new Item(new Item.Settings().food(SoupdUpFoodComponents.CHORUS_SOUP, SoupdUpConsumableComponents.CHORUS_SOUP).useRemainder(BOWL).maxCount(1).registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(SoupdUp.MOD_ID,"chorus_soup"))).useCooldown(1.0F)));


    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(SoupdUp.MOD_ID, name), item);
    }

    public static void registerModItems() {
        SoupdUp.LOGGER.info("Registering Mod Items for " + SoupdUp.MOD_ID);
    }
}
