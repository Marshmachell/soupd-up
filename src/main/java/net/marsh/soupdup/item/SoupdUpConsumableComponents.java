package net.marsh.soupdup.item;

import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.consume.ApplyEffectsConsumeEffect;
import net.minecraft.item.consume.TeleportRandomlyConsumeEffect;

import static net.minecraft.component.type.ConsumableComponents.food;

public class SoupdUpConsumableComponents {
    public static final ConsumableComponent SEA_SOUP = food().consumeEffect(new ApplyEffectsConsumeEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 60, 0), 0.3F)).build();
    public static final ConsumableComponent CHORUS_SOUP = food().consumeEffect(new ApplyEffectsConsumeEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 120, 1), 0.8F)).consumeEffect(new TeleportRandomlyConsumeEffect()).build();
}
