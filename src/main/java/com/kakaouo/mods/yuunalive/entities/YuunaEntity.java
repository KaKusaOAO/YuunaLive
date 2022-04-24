package com.kakaouo.mods.yuunalive.entities;

import com.kakaouo.mods.yuunalive.YuunaLive;
import com.kakaouo.mods.yuunalive.annotations.PlayerName;
import com.kakaouo.mods.yuunalive.annotations.PlayerNickname;
import com.kakaouo.mods.yuunalive.annotations.PlayerSkin;
import com.kakaouo.mods.yuunalive.annotations.SpawnEggColor;
import com.kakaouo.mods.yuunalive.entities.ai.goal.YuunaLivePlayerTravelGoal;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.commands.LocateCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;

@PlayerSkin(value = "textures/entities/yuuna/4.png", slim = true)
@PlayerName("SachiYuuna")
@PlayerNickname("優奈")
@SpawnEggColor(primary = 0xffffff, secondary = 0xff76a8)
public class YuunaEntity extends YuunaLivePlayerEntity implements Travellable {
    private boolean wantsToAdventure = false;
    private BlockPos travelTarget = BlockPos.ZERO;

    protected YuunaEntity(EntityType<YuunaEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    protected void initCustomGoals() {
        super.initCustomGoals();
        this.goalSelector.addGoal(14, new YuunaLivePlayerTravelGoal<>(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Monster.class, 0,
                false, false, this::canAttack
        ));
    }

    @Override
    public TextColor getNickNameColor() {
        return TextColor.fromLegacyFormat(ChatFormatting.LIGHT_PURPLE);
    }

    @Override
    public boolean doesAttackYuuna() {
        return true;
    }

    @Override
    public boolean canUseCriticalHit() {
        return true;
    }

    @Override
    public boolean isAttractedByYuuna() {
        return false;
    }

    @Override
    public void killed(ServerLevel world, LivingEntity other) {
        super.killed(world, other);
        var effect = new MobEffectInstance(MobEffects.REGENERATION, 200, 1, false, true);
        addEffect(effect);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putBoolean("WantsToAdventure", wantsToAdventure);

        BlockPos target = travelTarget;
        if(target != null) {
            nbt.put("TravelTarget", NbtUtils.writeBlockPos(target));
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        if(nbt.contains("WantsToAdventure", Tag.TAG_BYTE)) {
            setWantsToAdventure(nbt.getBoolean("WantsToAdventure"));
        }

        if(nbt.contains("TravelTarget")) {
            setTravelTarget(NbtUtils.readBlockPos(nbt.getCompound("TravelTarget")));
        }
    }

    @Override
    public boolean doesWantToAdventure() {
        return wantsToAdventure;
    }

    @Override
    public void setWantsToAdventure(boolean flag) {
        wantsToAdventure = flag;
    }

    @Override
    public BlockPos getTravelTarget() {
        return travelTarget;
    }

    public void setTravelTarget(BlockPos pos) {
        travelTarget = pos;
    }

    public List<ResourceLocation> getAvailableStructureTypes() {
        var result = new ArrayList<ResourceLocation>();
        result.add(new ResourceLocation("village"));
        result.add(new ResourceLocation("mansion"));
        result.add(new ResourceLocation("swamp_hut"));
        result.add(new ResourceLocation("pillager_outpost"));
        result.add(new ResourceLocation("ruined_portal"));
        return result;
    }

    public ResourceLocation getRandomStructureType() {
        var list = getAvailableStructureTypes();
        return list.get(getRandom().nextInt(list.size()));
    }

    @Override
    public void tick() {
        super.tick();
        if(!(level instanceof ServerLevel sw)) return;

        if(isAlive()) {
            LivingEntity target = getTarget();
            if(target != null && target.isDeadOrDying()) {
                setTarget(null);
                var effect = new MobEffectInstance(MobEffects.REGENERATION, 200, 1, false, true);
                addEffect(effect);
            }

            if(!wantsToAdventure) {
                if (getRandom().nextInt(100) == 0) {
                    // Minecraft has changed the API for locating structures since 1.18.2...
                    var id = getRandomStructureType();
                    var feature = sw.registryAccess().registryOrThrow(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY)
                            .get(id);
                    if (feature == null) {
                        YuunaLive.logger.warn("feature is null! Trying to find structure: " + id.toString());
                    } else {
                        var result = sw.getChunkSource().getGenerator()
                                .findNearestMapFeature(sw, HolderSet.direct(Holder.direct(feature)), blockPosition(), 12, false);

                        if (result != null) {
                            BlockPos found = result.getFirst();
                            if(travelTarget != null && !travelTarget.equals(found)) {
                                setTravelTarget(found);
                                wantsToAdventure = true;
                            }
                        }
                    }
                }
            }
        }
    }
}
