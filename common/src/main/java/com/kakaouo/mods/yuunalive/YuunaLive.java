package com.kakaouo.mods.yuunalive;

import com.kakaouo.mods.yuunalive.annotations.SpawnEggColor;
import com.kakaouo.mods.yuunalive.entities.ModEntityType;
import com.kakaouo.mods.yuunalive.util.KakaUtils;
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

public final class YuunaLive {
    private YuunaLive() {}

    /**
     * YuunaLive 模組預設使用的命名空間。
     */
    public static final String NAMESPACE = "yuunalive";

    /**
     * YuunaLive 模組預設使用的 {@link Logger}，各平台實作可以自訂一份新的
     * {@link Logger} 以和 Common 區分。
     */
    public static final Logger LOGGER = LogManager.getLogger("YuunaLive");

    public static ResourceLocation createId(String path) {
        return new ResourceLocation(NAMESPACE, path);
    }

    private static Platform hostPlatform;

    public static Platform getPlatform() {
        return hostPlatform;
    }

    // Called by platform
    public static void init() {
        Platform platform = PlatformManager.getPlatform();
        if (platform == null) {
            throw new UnsupportedOperationException("Platform is not initialized!");
        }

        YuunaLive.hostPlatform = platform;
        LOGGER.info("YuunaLive is running on {}.", platform.getPlatformName());

        ModEntityType.registerAllAsync().thenRun(() -> {
            LOGGER.info("Registering spawn eggs and biome modifications...");
            for(EntityType<? extends Mob> type : ModEntityType.getYuunaLivePlayerEntityTypes()) {
                ResourceLocation id = Registry.ENTITY_TYPE.getKey(type);
                ResourceLocation itemId = createId("spawn_egg_" + id.getPath());

                // Default colors for our entities
                int primary = 0xffffff;
                int secondary = 0xff88aa;

                SpawnEggColor color = ModEntityType.getClassByType(type).getDeclaredAnnotation(SpawnEggColor.class);
                if(color != null) {
                    primary = color.primary();
                    secondary = color.secondary();
                }

                SpawnEggItem item = new SpawnEggItem(type, primary, secondary, new Item.Properties().tab(CreativeModeTab.TAB_MISC));
                platform.registerItemAsync(itemId, item);

                // Natural spawning
                platform.registerSpawn(KakaUtils::isNotOceanBiome, type, type.getCategory(), new MobSpawnSettings.SpawnerData(type, 100, 1, 2));
            }
        });
    }
}
