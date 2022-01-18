package com.kakaouo.mods.yuunalive;

import com.kakaouo.mods.yuunalive.annotations.SpawnEggColor;
import com.kakaouo.mods.yuunalive.entities.ModEntityType;
import com.kakaouo.mods.yuunalive.util.KakaUtils;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.SpawnSettings;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.stream.Collectors;

public class YuunaLive implements ModInitializer {
    public static final String NAMESPACE = "yuunalive";
    public static final Logger logger = LogManager.getLogger("YuunaLive");
    public static Identifier id(String path) {
        return new Identifier(NAMESPACE, path);
    }

    @Override
    public void onInitialize() {
        for(EntityType<? extends MobEntity> type : ModEntityType.getYuunaLivePlayerEntityTypes()) {
            Identifier id = Registry.ENTITY_TYPE.getId(type);
            Identifier itemId = id("spawn_egg_" + id.getPath());

            // Default colors for our entities
            int primary = 0xffffff;
            int secondary = 0xff88aa;

            SpawnEggColor color = ModEntityType.getClassByType(type).getDeclaredAnnotation(SpawnEggColor.class);
            if(color != null) {
                primary = color.primary();
                secondary = color.secondary();
            }
            Registry.register(Registry.ITEM, itemId, new SpawnEggItem(type, primary, secondary, new Item.Settings().group(ItemGroup.MISC)));

            // Natural spawning
            BiomeModifications.create(id).add(ModificationPhase.ADDITIONS, KakaUtils::isNotOceanBiome, ctx -> {
                ctx.getSpawnSettings().addSpawn(type.getSpawnGroup(), new SpawnSettings.SpawnEntry(type, 100, 1, 2));
            });
        }
    }
}
