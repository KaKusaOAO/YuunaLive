package com.kakaouo.mods.yuunalive.entities;

import com.kakaouo.mods.yuunalive.YuunaLive;
import com.kakaouo.mods.yuunalive.entities.ai.goal.YuunaLivePlayerTravelGoal;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.command.LocateCommand;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.StructureFeature;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class YuunaEntity extends YuunaLivePlayerEntity implements Travellable {
    public static final String NAME = "SachiYuuna";
    public static final String NICKNAME = "優奈";

    public static final Identifier ID = YuunaLive.id(NAME.toLowerCase(Locale.ROOT));
    public static final EntityType<YuunaEntity> TYPE = getType(ID, YuunaEntity::new);

    private boolean wantsToAdventure = false;
    private BlockPos travelTarget = BlockPos.ORIGIN;

    protected YuunaEntity(EntityType<YuunaEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public Identifier getTexture() {
        return YuunaLive.id("textures/entities/yuuna/1.png");
    }

    @Override
    protected void initCustomGoals() {
        super.initCustomGoals();
        this.goalSelector.add(14, new YuunaLivePlayerTravelGoal(this));
        this.targetSelector.add(2, new ActiveTargetGoal<>(this, HostileEntity.class, 0,
                false, false, this::canAttack
        ));
    }

    @Override
    public String getPlayerName() {
        return NAME;
    }

    @Override
    public String getNickName() {
        return NICKNAME;
    }

    @Override
    public TextColor getNickNameColor() {
        return TextColor.fromFormatting(Formatting.LIGHT_PURPLE);
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
    public void onKilledOther(ServerWorld world, LivingEntity other) {
        super.onKilledOther(world, other);
        var effect = new StatusEffectInstance(StatusEffects.REGENERATION, 200, 1, false, true);
        addStatusEffect(effect);
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("WantsToAdventure", wantsToAdventure);
        if(travelTarget != null) {
            nbt.put("TravelTarget", NbtHelper.fromBlockPos(travelTarget));
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if(nbt.contains("WantsToAdventure", NbtElement.BYTE_TYPE)) {
            setWantsToAdventure(nbt.getBoolean("WantsToAdventure"));
        }

        if(nbt.contains("TravelTarget")) {
            setTravelTarget(NbtHelper.toBlockPos(nbt.getCompound("TravelTarget")));
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

    public List<StructureFeature<?>> getAvailableStructureTypes() {
        var result = new ArrayList<StructureFeature<?>>();
        result.add(StructureFeature.VILLAGE);
        result.add(StructureFeature.MANSION);
        result.add(StructureFeature.SWAMP_HUT);
        result.add(StructureFeature.PILLAGER_OUTPOST);
        result.add(StructureFeature.RUINED_PORTAL);
        return result;
    }

    public StructureFeature<?> getRandomStructureType() {
        var list = getAvailableStructureTypes();
        return list.get(getRandom().nextInt(list.size()));
    }

    @Override
    public void tick() {
        super.tick();
        if(!(world instanceof ServerWorld sw)) return;

        if(isAlive()) {
            LivingEntity target = getTarget();
            if(target != null && target.isDead()) {
                setTarget(null);
                var effect = new StatusEffectInstance(StatusEffects.REGENERATION, 200, 1, false, true);
                addStatusEffect(effect);
            }

            if(!wantsToAdventure) {
                if (getRandom().nextInt(100) == 0) {
                    BlockPos found = sw.locateStructure(getRandomStructureType(), getBlockPos(), 12, false);
                    if(travelTarget != null && !travelTarget.equals(found)) {
                        setTravelTarget(found);
                        wantsToAdventure = true;
                    }
                }
            }
        }
    }
}
