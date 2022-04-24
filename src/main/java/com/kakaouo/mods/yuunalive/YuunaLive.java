package com.kakaouo.mods.yuunalive;

import com.kakaouo.mods.yuunalive.annotations.SpawnEggColor;
import com.kakaouo.mods.yuunalive.entities.ModEntityType;
import com.kakaouo.mods.yuunalive.util.KakaUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.level.biome.MobSpawnSettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.stream.Collectors;

public class YuunaLive implements ModInitializer {
    public static final String NAMESPACE = "yuunalive";
    public static final Logger logger = LogManager.getLogger("YuunaLive");
    public static ResourceLocation id(String path) {
        return new ResourceLocation(NAMESPACE, path);
    }

    @Override
    public void onInitialize() {
        for(EntityType<? extends Mob> type : ModEntityType.getYuunaLivePlayerEntityTypes()) {
            ResourceLocation id = Registry.ENTITY_TYPE.getKey(type);
            ResourceLocation itemId = id("spawn_egg_" + id.getPath());

            // Default colors for our entities
            int primary = 0xffffff;
            int secondary = 0xff88aa;

            SpawnEggColor color = ModEntityType.getClassByType(type).getDeclaredAnnotation(SpawnEggColor.class);
            if(color != null) {
                primary = color.primary();
                secondary = color.secondary();
            }
            Registry.register(Registry.ITEM, itemId, new SpawnEggItem(type, primary, secondary, new Item.Properties().tab(CreativeModeTab.TAB_MISC)));

            // Natural spawning
            BiomeModifications.create(id).add(ModificationPhase.ADDITIONS, KakaUtils::isNotOceanBiome, ctx -> {
                ctx.getSpawnSettings().addSpawn(type.getCategory(), new MobSpawnSettings.SpawnerData(type, 100, 1, 2));
            });
        }
    }
}
