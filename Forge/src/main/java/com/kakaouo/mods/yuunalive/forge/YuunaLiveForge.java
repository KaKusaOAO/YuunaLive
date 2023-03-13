package com.kakaouo.mods.yuunalive.forge;

import com.kakaouo.mods.yuunalive.Platform;
import com.kakaouo.mods.yuunalive.PlatformManager;
import com.kakaouo.mods.yuunalive.YuunaLive;
import com.kakaouo.mods.yuunalive.forge.registries.ModRegistries;
import com.kakaouo.mods.yuunalive.forge.utils.ForgeHelper;
import com.kakaouo.mods.yuunalive.forge.utils.MobAttributesEntry;
import com.kakaouo.mods.yuunalive.forge.utils.MobSpawnEntry;
import com.kakaouo.mods.yuunalive.util.EntityRegisterEntry;
import com.kakaouo.mods.yuunalive.util.RegisterEntry;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegisterEvent;

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
        modEventBus.addListener(this::onRegister);
        modEventBus.addListener(this::onEntityAttributeCreation);

        ForgeBiomeModifier.load(mobSpawnEntries);
        ForgeBiomeModifier.BIOME_MODIFIER_SERIALIZERS.register(modEventBus);
    }

    public void onRegister(RegisterEvent event) {
        if (event.getRegistryKey().equals(Registry.ENTITY_TYPE_REGISTRY)) {
            this.registerEntityTypes(event);
        }

        if (event.getRegistryKey().equals(Registry.ITEM_REGISTRY)) {
            this.registerItems(event);
        }
    }

    private void registerEntityTypes(RegisterEvent event) {
        seenEntityTypeRegisterEvent = true;
        YuunaLive.LOGGER.info("Registering entity types on Forge ...");

        for (EntityRegisterEntry<?> entry : ModRegistries.ENTITIES) {
            this.registerEntityType(event, entry);
        }
    }

    private <T extends Entity> void registerEntityType(RegisterEvent event, EntityRegisterEntry<T> entry) {
        ResourceLocation location = entry.location();
        EntityType<T> type = entry.builder().build(location.getPath());
        event.register(Registry.ENTITY_TYPE_REGISTRY, location, () -> type);
        entry.callback().complete(type);
    }

    private void registerItems(RegisterEvent event) {
        seenItemRegisterEvent = true;
        YuunaLive.LOGGER.info("Registering items on Forge...");

        for (RegisterEntry<Item> entry : ModRegistries.ITEMS) {
            event.register(Registry.ITEM_REGISTRY, entry.location(), entry::object);
        }
    }

    public void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        YuunaLive.LOGGER.info("Setting entity default attributes on Forge...");
        for (MobAttributesEntry<?> entry : mobAttributesEntries) {
            event.put(entry.type(), entry.attributeSupplier());
        }
    }

    @Override
    public String getPlatformName() {
        return "Forge";
    }

    @Override
    public boolean isClient() {
        return FMLLoader.getDist().isClient();
    }

    @Override
    public <T extends Entity> void registerSpawn(Predicate<Holder<Biome>> predicate, EntityType<T> type, MobCategory category, MobSpawnSettings.SpawnerData data) {
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
            return ForgeHelper.registerAfterInitAsync(ForgeRegistries.ENTITY_TYPES, id, () -> builder.build(id.toString()));
        }
    }

    @Override
    public <T extends Item> CompletableFuture<T> registerItemAsync(ResourceLocation id, T item) {
        if (!seenItemRegisterEvent) {
            CompletableFuture<T> future = new CompletableFuture<>();
            ModRegistries.ITEMS.add(new RegisterEntry<>(id, item));
            return future;
        } else {
            try {
                return ForgeHelper.registerAfterInitAsync(ForgeRegistries.ITEMS, id, () -> item);
            } catch (Exception ex) {
                YuunaLive.LOGGER.error("Failed to register item: " + id);
                YuunaLive.LOGGER.error(ex);
                return CompletableFuture.failedFuture(ex);
            }
        }
    }
}
