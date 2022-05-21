package com.kakaouo.mods.yuunalive.forge;

import com.kakaouo.mods.yuunalive.Platform;
import com.kakaouo.mods.yuunalive.PlatformManager;
import com.kakaouo.mods.yuunalive.YuunaLive;
import com.kakaouo.mods.yuunalive.forge.registries.ModRegistries;
import com.kakaouo.mods.yuunalive.forge.utils.ForgeHelper;
import com.kakaouo.mods.yuunalive.forge.utils.MobAttributesEntry;
import com.kakaouo.mods.yuunalive.forge.utils.MobSpawnEntry;
import com.kakaouo.mods.yuunalive.util.EntityRegisterEntry;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

@Mod(YuunaLiveForge.MOD_ID)
@Mod.EventBusSubscriber(modid = YuunaLiveForge.MOD_ID)
public class YuunaLiveForge implements Platform {
    public static final String MOD_ID = YuunaLive.NAMESPACE;
    private final List<MobSpawnEntry<?>> mobSpawnEntries = new ArrayList<>();
    private final List<MobAttributesEntry<?>> mobAttributesEntries = new ArrayList<>();

    private boolean seenEntityTypeRegisterEvent = false;
    private boolean seenItemRegisterEvent = false;

    public YuunaLiveForge() {
        PlatformManager.setPlatform(this);
        YuunaLive.init();

        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addGenericListener(EntityType.class, this::onRegisterEntityTypes);
        modEventBus.addGenericListener(Item.class, this::onRegisterItems);
        modEventBus.addListener(this::onEntityAttributeCreation);

        IEventBus mainEventBus = MinecraftForge.EVENT_BUS;
        mainEventBus.addListener(this::onBiomeLoad);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void onRegisterEntityTypes(RegistryEvent.Register<EntityType<?>> event) {
        seenEntityTypeRegisterEvent = true;

        YuunaLive.logger.info("Registering entity types on Forge ...");
        IForgeRegistry<EntityType<?>> registry = event.getRegistry();
        for(EntityRegisterEntry<?> entry : ModRegistries.ENTITIES) {
            ResourceLocation location = entry.location();
            EntityType<?> type = entry.builder().build(location.getPath());
            type.setRegistryName(location);
            registry.register(type);
            ((CompletableFuture) entry.callback()).complete(type);
        }
    }

    public void onRegisterItems(RegistryEvent.Register<Item> event) {
        seenItemRegisterEvent = true;

        YuunaLive.logger.info("Registering items on Forge...");
        IForgeRegistry<Item> registry = event.getRegistry();
        for(Item entry : ModRegistries.ITEMS) {
            registry.register(entry);
        }
    }

    public void onBiomeLoad(BiomeLoadingEvent event) {
        ResourceLocation name = event.getName();
        if (name == null) return;

        var biome = ForgeRegistries.BIOMES.getValue(name);
        for (MobSpawnEntry<?> entry : mobSpawnEntries) {
            if (entry.predicate().test(biome)) {
                event.getSpawns().addSpawn(entry.category(), entry.data());
            }
        }
    }

    public void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        YuunaLive.logger.info("Setting entity default attributes on Forge...");
        for (MobAttributesEntry<?> entry : mobAttributesEntries) {
            event.put(entry.type(), entry.attributeSupplier());
        }
    }

    @Override
    public String getPlatformName() {
        return "Forge";
    }

    private static Boolean alwaysTrue() {
        return Boolean.TRUE;
    }

    @Override
    public boolean isClient() {
        return FMLLoader.getDist().isClient();
    }

    @Override
    public <T extends Entity> void registerSpawn(Predicate<Biome> predicate, EntityType<T> type, MobCategory category, MobSpawnSettings.SpawnerData data) {
        mobSpawnEntries.add(new MobSpawnEntry<>(predicate, type, category, data));
    }

    @Override
    public <T extends LivingEntity> void registerDefaultAttribute(EntityType<T> type, AttributeSupplier.Builder builder) {
        mobAttributesEntries.add(new MobAttributesEntry<>(type, builder.build()));
    }

    @Override
    public <T extends Entity> void registerEntityRenderer(EntityType<T> type, EntityRendererProvider<T> rendererProvider) {
        EntityRenderers.register(type, rendererProvider);
    }

    @Override
    public <T extends Entity> CompletableFuture<EntityType<T>> registerEntityTypeAsync(ResourceLocation id, EntityType.Builder<T> builder) {
        if (!seenEntityTypeRegisterEvent) {
            CompletableFuture<EntityType<T>> future = new CompletableFuture<>();
            ModRegistries.ENTITIES.add(new EntityRegisterEntry<>(builder, id, future));
            return future;
        } else {
            return ForgeHelper.registerAfterInitAsync(ForgeRegistries.ENTITIES, () -> builder.build(id.toString()));
        }
    }

    @Override
    public <T extends Item> CompletableFuture<T> registerItemAsync(ResourceLocation id, T item) {
        item.setRegistryName(id);

        if (!seenItemRegisterEvent) {
            CompletableFuture<T> future = new CompletableFuture<>();
            ModRegistries.ITEMS.add(item);
            return future;
        } else {
            try {
                return ForgeHelper.registerAfterInitAsync(ForgeRegistries.ITEMS, () -> item);
            } catch (Exception ex) {
                YuunaLive.logger.error("Failed to register item: " + id);
                YuunaLive.logger.error(ex);
                return CompletableFuture.failedFuture(ex);
            }
        }
    }
}
