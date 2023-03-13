package com.kakaouo.mods.yuunalive.forge;

import com.google.common.base.Preconditions;
import com.kakaouo.mods.yuunalive.forge.utils.MobSpawnEntry;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

// Weird Forge.
public class ForgeBiomeModifier implements BiomeModifier {
    public static final DeferredRegister<Codec<? extends BiomeModifier>> BIOME_MODIFIER_SERIALIZERS =
        DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, YuunaLiveForge.MOD_ID);

    public static final RegistryObject<Codec<ForgeBiomeModifier>> CODEC = BIOME_MODIFIER_SERIALIZERS.register("main", () ->
        RecordCodecBuilder.create(builder -> builder.point(new ForgeBiomeModifier())));

    private static List<MobSpawnEntry<?>> entries;

    @Override
    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase != Phase.ADD) return;

        for (MobSpawnEntry<?> entry : entries) {
            if (!entry.predicate().test(biome)) continue;
            builder.getMobSpawnSettings().addSpawn(entry.category(), entry.data());
        }
    }

    @Override
    public Codec<? extends BiomeModifier> codec() {
        return CODEC.get();
    }

    public static void load(List<MobSpawnEntry<?>> entries) {
        Preconditions.checkNotNull(entries, "entries cannot be null");
        ForgeBiomeModifier.entries = entries;
    }
}
