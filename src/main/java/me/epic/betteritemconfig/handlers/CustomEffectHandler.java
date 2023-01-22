package me.epic.betteritemconfig.handlers;

import me.epic.betteritemconfig.ItemBuilder;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class CustomEffectHandler implements ItemHandler {
    @Override
    public ItemStack process(ItemStack stack, ConfigurationSection section) {
        ItemBuilder builder = ItemBuilder.modifyItem(stack);
        if (section.isConfigurationSection("effects")) {
            List<PotionEffect> effectList = new ArrayList<>();
            ConfigurationSection effectSection = section.getConfigurationSection("effects");
            for (String key : effectSection.getKeys(false)) {
                ConfigurationSection effect = effectSection.getConfigurationSection(key);
                boolean ambient = effect.contains("ambient") ? effectSection.getBoolean("ambient") : true;
                boolean particles = effect.contains("particles") ? effectSection.getBoolean("particles") : true;
                boolean icon = effect.contains("icon") ? effectSection.getBoolean("icon") : true;
                int duration = effect.getInt("duration");
                int amplifier = effect.getInt("amplifier");
                PotionEffectType effectType = PotionEffectType.getByKey(NamespacedKey.minecraft(key));
                effectList.add(new PotionEffect(effectType, duration, amplifier, ambient, particles, icon));
            }
            builder.potionEffects(effectList);
        }
        return builder.build();
    }

    @Override
    public void write(ItemStack item, ConfigurationSection section) {
        ItemMeta meta = item.getItemMeta();
        if (meta instanceof PotionMeta potionMeta) {
            if (potionMeta.hasCustomEffects()) {
                ConfigurationSection potionSection =  section.createSection("effects");
                for (PotionEffect customEffect : potionMeta.getCustomEffects()) {
                    ConfigurationSection customEffectSection = potionSection.createSection(customEffect.getType().getKey().getKey());
                    if (!customEffect.isAmbient()) customEffectSection.set("ambient", false);
                    if (!customEffect.hasParticles()) customEffectSection.set("particles", false);
                    if (!customEffect.hasIcon()) customEffectSection.set("icon", false);
                    customEffectSection.set("amplifier", customEffect.getAmplifier());
                    customEffectSection.set("duration", customEffect.getDuration());
                }
            }
        }
    }
}
