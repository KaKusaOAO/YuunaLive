package com.kakaouo.mods.yuunalive;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;

import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

/**
 * {@link Platform} 是需要在 Forge/Fabric 等版本的模組上實作的介面，有某些環境特定的功能需要透過這個介面來做到！
 */
public interface Platform {
    /**
     * 取得目前運行環境的名稱，可能是 Forge 或 Fabric 之類的。
     * @return 目前運行環境的名稱
     */
    String getPlatformName();

    /**
     * 目前的運行環境是否是遊戲端?
     * @return 如果是遊戲端，則回傳 {@code true}，否則 (伺服器端) 會回傳 {@code false}。
     */
    boolean isClient();

    /**
     * 為新的實體登錄自然重生的機制。
     * @param predicate 依照目前的 {@link Biome} 來判斷是否要自然重生該實體
     * @param type 該實體對應到的 {@link EntityType} 類型
     * @param category 自訂該實體對應到的生物類別，會影響到其他生物的生成！
     * @param data 這個實體重生的細節設定 (權重、最小數量、最大數量?)
     * @param <T> 該實體的 class
     */
    <T extends Entity> void registerSpawn(Predicate<Biome> predicate, EntityType<T> type, MobCategory category, MobSpawnSettings.SpawnerData data);

    /**
     * 為新的生物登錄預設的屬性 (attribute)。
     * @param type 該實體對應到的 {@link EntityType} 類型
     * @param builder 要為實體指定的屬性，以 {@link AttributeSupplier.Builder} 形式傳入
     * @param <T> 該實體的 class
     */
    <T extends LivingEntity> void registerDefaultAttribute(EntityType<T> type, AttributeSupplier.Builder builder);

    /**
     * 為新的實體登錄對應的 renderer。
     * @param type 該實體對應到的 {@link EntityType} 類型
     * @param rendererProvider 要登錄的 renderer，以 {@link EntityRendererProvider} 的形式提供
     * @param <T> 該實體的 class
     */
    <T extends Entity> void registerEntityRenderer(EntityType<T> type, EntityRendererProvider<T> rendererProvider);


    <T extends Entity> CompletableFuture<EntityType<T>> registerEntityTypeAsync(ResourceLocation id, EntityType.Builder<T> builder);

    /**
     * 登錄新的物品。
     * @param id 該物品對應到的 ID
     * @param item 用來登錄的新物品。
     * @param <T> 該物品的 class，需要繼承 {@link Item}
     * @return 一個 {@link CompletableFuture} 物件，之後可以從它取得登錄好的物品 {@link T}
     */
    <T extends Item> CompletableFuture<T> registerItemAsync(ResourceLocation id, T item);
}
