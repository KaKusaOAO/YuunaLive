package com.kakaouo.mods.yuunalive.entities;

import com.kakaouo.mods.yuunalive.Platform;
import com.kakaouo.mods.yuunalive.PlatformManager;
import com.kakaouo.mods.yuunalive.YuunaLive;
import com.kakaouo.mods.yuunalive.entities.client.renderer.YuunaLivePlayerEntityRenderer;
import com.kakaouo.mods.yuunalive.util.KakaUtils;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public final class ModEntityType {
    private static final Map<Class<? extends Entity>, EntityType<? extends Entity>> clzTypeMap = new HashMap<>();
    private static final Map<EntityType<? extends Entity>, Class<? extends Entity>> typeClzMap = new HashMap<>();
    private static CompletableFuture<Void> untilAllRegistered;

    static {
        untilAllRegistered = registerAllByReflectionAsync();
    }

    /**
     * 等待所有的 YuunaLive 生物登錄完畢。
     * @return 一個登錄完畢後會完成的 {@link CompletableFuture} 物件。
     */
    public static CompletableFuture<Void> registerAllAsync() {
        return untilAllRegistered;
    }

    public static EntityType<? extends Entity> getTypeByClass(Class<?> clz) {
        return clzTypeMap.get(clz);
    }

    public static Class<? extends Entity> getClassByType(EntityType<?> type) {
        return typeClzMap.get(type);
    }

    /**
     * 取得所有已經登錄好的 {@link YuunaLivePlayerEntity} 生物類型。
     * @return 所有已經登錄好的 {@link YuunaLivePlayerEntity} 生物類型
     */
    @SuppressWarnings("unchecked")
    public static Set<EntityType<? extends YuunaLivePlayerEntity>> getYuunaLivePlayerEntityTypes() {
        return clzTypeMap.keySet().stream()
                .filter(YuunaLivePlayerEntity.class::isAssignableFrom)
                .map(clz -> (EntityType<? extends YuunaLivePlayerEntity>) clzTypeMap.get(clz))
                .collect(Collectors.toSet());
    }

    /**
     * 自動搜尋所有繼承 {@link YuunaLivePlayerEntity} 的類別並分別登錄為新的生物。
     * 若該類別有定義 {@code static boolean shouldBeExcluded()} 並回傳
     * {@code false}，則該類別不會被登錄為新的生物。
     * @return 一個登錄完畢後會完成的 {@link CompletableFuture} 物件。
     */
    @SuppressWarnings("unchecked")
    private static CompletableFuture<Void> registerAllByReflectionAsync() {
        YuunaLive.LOGGER.info("Looking for entity classes...");
        List<CompletableFuture<?>> futures = new ArrayList<>();
        for(Class<?> clz : KakaUtils.getClassesOfPackage(ModEntityType.class.getPackage())) {
            if(YuunaLivePlayerEntity.class.isAssignableFrom(clz) && !clz.equals(YuunaLivePlayerEntity.class)) {
                Class<? extends YuunaLivePlayerEntity> c = (Class<? extends YuunaLivePlayerEntity>) clz;
                boolean exclude = false;
                try {
                    if ((boolean) c.getDeclaredMethod("shouldBeExcluded").invoke(null)) {
                        exclude = true;
                    }
                } catch(NoSuchMethodException | InvocationTargetException | IllegalAccessException ex) {
                    //
                }
                if (!exclude) {
                    futures.add(createYuunaPlayerBuilderAsync(c));
                }
            }
        }
        YuunaLive.LOGGER.info("Scheduled " +  futures.size() + " entity registrations.");
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture<?>[0]));
    }

    /**
     * 登錄一個繼承 {@link YuunaLivePlayerEntity} 的類別為新的生物。
     * @param clz 該繼承 {@link YuunaLivePlayerEntity} 的類別
     * @param <T> 該繼承 {@link YuunaLivePlayerEntity} 的類別
     * @return 一個 {@link CompletableFuture} 物件，之後可以從它取得登錄好的實體類型 {@link EntityType}
     */
    @SuppressWarnings("unchecked")
    private static <T extends YuunaLivePlayerEntity> CompletableFuture<EntityType<T>> createYuunaPlayerBuilderAsync(Class<T> clz) {
        ResourceLocation id = YuunaLivePlayerEntity.getIdentifier(clz);
        EntityType.EntityFactory<T> factory = (type, world) -> {
            try {
                Constructor<?> ctor = clz.getDeclaredConstructor(EntityType.class, Level.class);
                ctor.setAccessible(true);
                return (T)ctor.newInstance(type, world);
            } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException ex) {
                YuunaLive.LOGGER.error(ex);
                return null;
            }
        };

        YuunaLive.LOGGER.info("Scheduled for registering entity: " + clz.getName());
        EntityType.Builder<T> builder = YuunaLivePlayerEntity.createBuilder(factory);
        return registerByBuilderAsync(clz, id, builder).thenApply(type -> {
            YuunaLive.LOGGER.info("Entity class " + clz.getName() + " has been registered!");

            Platform platform = PlatformManager.getPlatform();
            platform.registerDefaultAttribute(type, YuunaLivePlayerEntity.createPlayerAttributes());

            if (platform.isClient()) {
                registerClientRenderer(clz, type);
            }
            return type;
        });
    }

    /**
     * 為新的 {@link YuunaLivePlayerEntity} 實體登錄對應的 renderer。
     * @param clz 該繼承 {@link YuunaLivePlayerEntity} 的類別
     * @param type 該實體對應到的 {@link EntityType} 類型
     * @param <T> 該繼承 {@link YuunaLivePlayerEntity} 的類別
     */
    @SuppressWarnings("unchecked")
    private static <T extends YuunaLivePlayerEntity> void registerClientRenderer(Class<T> clz, EntityType<T> type) {
        EntityRendererProvider<T> normal = ctx -> (EntityRenderer<T>) new YuunaLivePlayerEntityRenderer(ctx, false);
        EntityRendererProvider<T> slim = ctx -> (EntityRenderer<T>) new YuunaLivePlayerEntityRenderer(ctx, true);

        Platform platform = YuunaLive.getPlatform();
        platform.registerEntityRenderer(type, YuunaLivePlayerEntity.isSlim(clz) ? slim : normal);
    }

    /**
     * 透過一個已經設定好的 {@link EntityType.Builder} 登錄一個繼承 {@link YuunaLivePlayerEntity} 的類別為新的生物。
     * @param clz 該繼承 {@link YuunaLivePlayerEntity} 的類別
     * @param id 該實體類型對應到的 ID
     * @param builder 用來建立該實體類型的 {@link EntityType.Builder}
     * @param <T> 該繼承 {@link YuunaLivePlayerEntity} 的類別
     * @return
     */
    private static <T extends YuunaLivePlayerEntity> CompletableFuture<EntityType<T>> registerByBuilderAsync(Class<T> clz, ResourceLocation id, EntityType.Builder<T> builder) {
        Platform platform = YuunaLive.getPlatform();
        return platform.registerEntityTypeAsync(id, builder).thenApply(type -> {
            clzTypeMap.put(clz, type);
            typeClzMap.put(type, clz);
            return type;
        });
    }

    /**
     * 開始載入並登錄 YuunaLive 的實體。
     */
    public static void load() {}
}
