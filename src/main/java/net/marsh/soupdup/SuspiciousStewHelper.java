package net.marsh.soupdup;

import net.marsh.soupdup.component.SoupdUpComponentTypes;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.SuspiciousStewEffectsComponent;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.*;

public class SuspiciousStewHelper {
    public static List<SuspiciousStewEffectsComponent.StewEffect> getSusEffects(ItemStack stack) {
        return Objects.requireNonNull(stack.getComponents().get(DataComponentTypes.SUSPICIOUS_STEW_EFFECTS)).effects();
    }
    public static RegistryEntry<StatusEffect> getSusEffect(ItemStack stack, int index) {
        return Objects.requireNonNull(stack.getComponents().get(DataComponentTypes.SUSPICIOUS_STEW_EFFECTS)).effects().get(index).effect();
    }
    public static int getSusEffectDuration(ItemStack stack, int index) {
        return Objects.requireNonNull(stack.getComponents().get(DataComponentTypes.SUSPICIOUS_STEW_EFFECTS)).effects().get(index).duration();
    }
    public static void addSusEffect(ItemStack stack, RegistryEntry<StatusEffect> statusEffect, int duration) {
        SuspiciousStewEffectsComponent effectsComponent = stack.getComponents()
                .getOrDefault(
                        DataComponentTypes.SUSPICIOUS_STEW_EFFECTS,
                        new SuspiciousStewEffectsComponent(List.of())
                );

        List<SuspiciousStewEffectsComponent.StewEffect> newEffects = new ArrayList<>(effectsComponent.effects());
        newEffects.removeIf(e -> e.effect() == statusEffect);
        newEffects.add(new SuspiciousStewEffectsComponent.StewEffect(statusEffect, duration));
        stack.set(DataComponentTypes.SUSPICIOUS_STEW_EFFECTS, new SuspiciousStewEffectsComponent(newEffects));
    }
    public static void increaseSusEffectsTime(ItemStack stack, float multiplier) {
        SuspiciousStewEffectsComponent effectsComponent = stack.getComponents()
                .getOrDefault(
                        DataComponentTypes.SUSPICIOUS_STEW_EFFECTS,
                        new SuspiciousStewEffectsComponent(List.of())
                );
        List<SuspiciousStewEffectsComponent.StewEffect> multipliedEffects = effectsComponent.effects().stream()
                .map(effect -> new SuspiciousStewEffectsComponent.StewEffect(
                        effect.effect(),
                        (int) (effect.duration() * multiplier)
                ))
                .toList();
        stack.set(DataComponentTypes.SUSPICIOUS_STEW_EFFECTS, new SuspiciousStewEffectsComponent(multipliedEffects));
        stack.set(SoupdUpComponentTypes.TIME_UPGRADED, true);
    }
    public static boolean compareEffectsExact(ItemStack stack1, ItemStack stack2) {
        List<SuspiciousStewEffectsComponent.StewEffect> effects1 = getSusEffects(stack1);
        List<SuspiciousStewEffectsComponent.StewEffect> effects2 = getSusEffects(stack2);

        return effects1.size() == effects2.size() && new HashSet<>(effects1).containsAll(effects2);
    }
}
