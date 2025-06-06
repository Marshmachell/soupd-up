package net.marsh.soupdup.item;

import net.minecraft.component.type.FoodComponent;

public class SoupdUpFoodComponents {
    public static final FoodComponent SALMON_SOUP = new FoodComponent.Builder().nutrition(7).saturationModifier(0.65F).build();
    public static final FoodComponent HEARTY_SALMON_SOUP = new FoodComponent.Builder().nutrition(8).saturationModifier(0.95F).build();
    public static final FoodComponent COD_SOUP = new FoodComponent.Builder().nutrition(7).saturationModifier(0.45F).build();
    public static final FoodComponent HEARTY_COD_SOUP = new FoodComponent.Builder().nutrition(8).saturationModifier(0.75F).build();
    public static final FoodComponent SEA_SOUP = new FoodComponent.Builder().nutrition(3).saturationModifier(0.45F).build();
    public static final FoodComponent CHORUS_SOUP = new FoodComponent.Builder().nutrition(5).saturationModifier(0.55F).alwaysEdible().build();
}
