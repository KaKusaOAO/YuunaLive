package com.kakaouo.mods.yuunalive;

import com.kakaouo.mods.yuunalive.entities.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.fabricmc.fabric.api.biome.v1.OverworldBiomes;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.MinecraftClientGame;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.debug.DebugRenderer;
import net.minecraft.client.render.debug.PathfindingDebugRenderer;
import net.minecraft.datafixer.fix.BiomesFix;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNodeNavigator;
import net.minecraft.entity.attribute.DefaultAttributeRegistry;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BuiltinBiomes;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.biome.source.BiomeSource;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class YuunaLive implements ModInitializer {
    public static final String NAMESPACE = "yuunalive";
    public static final Logger logger = LogManager.getLogger("YuunaLive");
    public static Identifier id(String path) {
        return new Identifier(NAMESPACE, path);
    }

    @Override
    public void onInitialize() {
        for(EntityType<? extends MobEntity> type : new EntityType[] {
                GinaChenEntity.TYPE,
                KiuryilEntity.TYPE,
                YunariEntity.TYPE,
                KakaEntity.TYPE,
                YCTainEntity.TYPE,
                YuruEntity.TYPE,
                YuunaEntity.TYPE,
                Support1NoEntity.TYPE
        }) {
            Identifier id = Registry.ENTITY_TYPE.getId(type);
            Identifier itemId = id("spawn_egg_" + id.getPath());
            Registry.register(Registry.ITEM, itemId, new SpawnEggItem(type, 0xffffff, 0xff88aa, new Item.Settings().group(ItemGroup.MISC)));

            // Default attributes
            FabricDefaultAttributeRegistry.register(type, YuunaLivePlayerEntity.createPlayerAttributes());

            // Natural spawning
            BiomeModifications.create(id).add(ModificationPhase.ADDITIONS, ctx -> true, ctx -> {
                ctx.getSpawnSettings().addSpawn(type.getSpawnGroup(), new SpawnSettings.SpawnEntry(type, 100, 1, 2));
            });
        }
    }
}
