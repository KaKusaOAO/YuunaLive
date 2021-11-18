package com.kakaouo.mods.yuunalive;

import com.kakaouo.mods.yuunalive.entities.*;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
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
            FabricDefaultAttributeRegistry.register(type, YuunaLivePlayerEntity.createPlayerAttributes());
        }
    }
}
