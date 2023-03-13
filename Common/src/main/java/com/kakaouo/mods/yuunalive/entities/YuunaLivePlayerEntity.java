package com.kakaouo.mods.yuunalive.entities;

import com.kakaouo.mods.yuunalive.YuunaLive;
import com.kakaouo.mods.yuunalive.annotations.PlayerCape;
import com.kakaouo.mods.yuunalive.annotations.PlayerName;
import com.kakaouo.mods.yuunalive.annotations.PlayerNickname;
import com.kakaouo.mods.yuunalive.annotations.PlayerSkin;
import com.kakaouo.mods.yuunalive.entities.ai.goal.*;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.*;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Ghast;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.monster.ZombifiedPiglin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.*;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.phys.Vec3;

import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

public abstract class YuunaLivePlayerEntity extends PathfinderMob implements RangedAttackMob {
    private static final EntityDataAccessor<Optional<UUID>> OWNER_UUID = SynchedEntityData.defineId(YuunaLivePlayerEntity.class, EntityDataSerializers.OPTIONAL_UUID);

    private final YuunaLivePlayerBowAttackGoal bowAttackGoal = new YuunaLivePlayerBowAttackGoal(this, 1.0D, 20, 15.0F);
    private final MeleeAttackGoal meleeAttackGoal = new YuunaLivePlayerMeleeAttackGoal(this, 1.0D, false);
    private YuunaEntity owner;

    protected YuunaLivePlayerEntity(EntityType<? extends YuunaLivePlayerEntity> entityType, Level world) {
        super(entityType, world);
        this.setPersistenceRequired();
        this.updateAttackType();
    }

    protected static <T extends YuunaLivePlayerEntity> EntityType.Builder<T> createBuilder(EntityType.EntityFactory<T> builder) {
        return EntityType.Builder.of(builder, MobCategory.CREATURE)
                .canSpawnFarFromPlayer()
                .sized(0.6f, 1.95f);
    }

    public static AttributeSupplier.Builder createPlayerAttributes() {
        return Player.createAttributes()
                .add(Attributes.FOLLOW_RANGE, 36.0)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.ATTACK_KNOCKBACK);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0f));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.initCustomGoals();
    }

    protected void initCustomGoals() {
        this.goalSelector.addGoal(1, new FloatGoal(this));

        if(isAttractedByYuuna()) {
            this.goalSelector.addGoal(1, new YuunaLivePlayerFindOwnerGoal(this));
        }
        if(doesAttackYuuna()) {
            this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, YuunaEntity.class, 0,
                    false, false, this::canAttack
            ));
        }

        LootTableProvider

        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this, this.getClass()).setAlertOthers(ZombifiedPiglin.class));
        this.targetSelector.addGoal(1, new YuunaLivePlayerPickupItemGoal(this));
        this.targetSelector.addGoal(1, new YuunaLivePlayerCancelAttackGoal(this));
        this.targetSelector.addGoal(3, new YuunaLivePlayerTrackOwnerAttackerGoal(this));
        this.targetSelector.addGoal(4, new YuunaLivePlayerAttackWithOwnerGoal(this));
        this.goalSelector.addGoal(6, new YuunaLivePlayerFollowOwnerGoal(this, 1, 10, 2, false, false));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(OWNER_UUID, Optional.empty());
    }

    public boolean canAttack(LivingEntity entity) {
        if (!entity.canBeSeenAsEnemy()) return false;
        if (entity.isSpectator()) return false;
        if (!entity.canBeSeenByAnyone()) return false;

        return getAttackScore(entity) >= 0;
    }

    public int getAttackScore(LivingEntity entity) {
        YuunaEntity owner = getOwner();
        if(owner != null) {
            if(owner.getLastHurtByMob() == entity) return 1;
            if(owner.getLastHurtMob() == entity) return 1;
        }

        if(this instanceof YuunaEntity) {
            return 1;
        }

        if(entity instanceof YuunaLivePlayerEntity ylp) {
            if(owner != null) {
                YuunaEntity otherOwner = ylp.getOwner();
                if(owner.equals(otherOwner)) {
                    return -1;
                }
            }
        }

        return 0;
    }

    public float getCalculatedDamageAmount() {
        float a = (float) getAttributeValue(Attributes.ATTACK_DAMAGE);
        float b = EnchantmentHelper.getDamageBonus(getMainHandItem(), getMobType());
        return (a + b) * (canUseCriticalHit() ? 2 : 1);
    }

    public boolean canUseCriticalHit() {
        return hasOwner() && getTarget() == getOwner().getTarget();
    }

    public boolean doesChinFacing() {
        return false;
    }

    public float getOwnerFindRange() {
        return 48.0f;
    }

    public ResourceLocation getTexture() {
        return getTexture(this.getClass());
    }

    public boolean isSlim() {
        return isSlim(this.getClass());
    }

    public String getPlayerName() {
        return getPlayerName(this.getClass());
    }

    public String getNickName() {
        return getNickName(this.getClass());
    }

    public ResourceLocation getIdentifier() {
        return getIdentifier(this.getClass());
    }

    public static <T extends YuunaLivePlayerEntity> ResourceLocation getTexture(Class<T> clz) {
        PlayerSkin name = clz.getAnnotation(PlayerSkin.class);
        if(name != null) return YuunaLive.id(name.value());
        return null;
    }

    public static <T extends YuunaLivePlayerEntity> boolean isSlim(Class<T> clz) {
        PlayerSkin name = clz.getAnnotation(PlayerSkin.class);
        if(name != null) return name.slim();
        return false;
    }

    public static <T extends YuunaLivePlayerEntity> String getPlayerName(Class<T> clz) {
        PlayerName name = clz.getAnnotation(PlayerName.class);
        if(name != null) return name.value();
        return "?";
    }

    public static <T extends YuunaLivePlayerEntity> String getNickName(Class<T> clz) {
        PlayerNickname name = clz.getAnnotation(PlayerNickname.class);
        if(name != null) return name.value();
        return null;
    }

    public static <T extends YuunaLivePlayerEntity> ResourceLocation getIdentifier(Class<T> clz) {
        return YuunaLive.id(getPlayerName(clz).toLowerCase(Locale.ROOT));
    }

    public boolean isAttractedByYuuna() {
        return true;
    }

    public boolean doesAttackYuuna() {
        return false;
    }

    @Override
    public Component getName() {
        MutableComponent playerName = new TextComponent(getPlayerName());
        String nickname = getNickName();
        MutableComponent nickTag = new TextComponent("");

        if(nickname != null) {
            MutableComponent nickName = new TextComponent(getNickName());
            nickName.setStyle(nickName.getStyle().withColor(getNickNameColor()));

            if(this instanceof YuunaEntity) {
                playerName.withStyle(ChatFormatting.LIGHT_PURPLE);
                nickName.withStyle(ChatFormatting.BOLD);
            }

            nickTag = nickTag.append(new TranslatableComponent("[%s] ", nickName).withStyle(ChatFormatting.GREEN));
        }

        return nickTag.append(playerName);
    }

    public TextColor getNickNameColor() {
        return TextColor.fromLegacyFormat(ChatFormatting.AQUA);
    }

    public boolean hasOwner() {
        return owner != null;
    }

    public YuunaEntity getOwner() {
        return owner;
    }

    public UUID getOwnerUuid() {
        return this.entityData.get(OWNER_UUID).orElse(null);
    }

    public void setOwner(YuunaEntity owner) {
        if(owner != null) {
            this.setOwnerById(owner.getUUID());
        } else {
            this.setOwnerById(null);
        }
    }

    public void setOwnerById(UUID uuid) {
        this.entityData.set(OWNER_UUID, Optional.ofNullable(uuid));

        if(!(this.level instanceof ServerLevel sw)) return;
        if(uuid == null) {
            this.owner = null;
        } else if(sw.getEntity(uuid) instanceof YuunaEntity yuuna) {
            this.owner = yuuna;
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putBoolean("Panic", isPanicking());
        if(owner != null) {
            nbt.putUUID("Owner", owner.uuid);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        if(nbt.contains("Panic", Tag.TAG_BYTE)) {
            setPanicking(nbt.getBoolean("Panic"));
        }

        if(nbt.hasUUID("Owner")) {
            setOwnerById(nbt.getUUID("Owner"));
        }
    }

    @Override
    public void setItemSlot(EquipmentSlot slot, ItemStack stack) {
        super.setItemSlot(slot, stack);

        if (!this.level.isClientSide) {
            this.updateAttackType();
        }
    }

    private boolean panicking = false;

    public void setPanicking(boolean flag) {
        panicking = flag;
    }

    public boolean isPanicking() {
        return panicking;
    }

    @Override
    public boolean isEffectiveAi() {
        return super.isEffectiveAi() && !isPanicking();
    }

    @Override
    public boolean canBeControlledByRider() {
        return false;
    }

    public void updateAttackType() {
        if (this.level != null && !this.level.isClientSide) {
            this.goalSelector.removeGoal(this.meleeAttackGoal);
            this.goalSelector.removeGoal(this.bowAttackGoal);
            ItemStack itemStack = this.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this, Items.BOW));
            if (itemStack.is(Items.BOW)) {
                int i = 20;
                if (this.level.getDifficulty() != Difficulty.HARD) {
                    i = 40;
                }

                this.bowAttackGoal.setAttackInterval(i);
                this.goalSelector.addGoal(2, this.bowAttackGoal);
            } else {
                this.goalSelector.addGoal(2, this.meleeAttackGoal);
            }
        }
    }

    @Override
    public boolean isLeftHanded() {
        return false;
    }

    @Override
    public boolean isCustomNameVisible() {
        return true;
    }

    @Override
    public boolean canPickUpLoot() {
        return true;
    }

    // from fox
    private void dropItem(ItemStack stack) {
        ItemEntity itemEntity = new ItemEntity(this.level, this.getX(), this.getY(), this.getZ(), stack);
        this.level.addFreshEntity(itemEntity);
    }

    @Override
    public Pose getPose() {
        if(isShiftKeyDown()) return Pose.CROUCHING;
        if(isSleeping()) return Pose.SLEEPING;
        return super.getPose();
    }

    public SoundEvent getPanicSound() {
        return null;
    }

    @Override
    public void aiStep() {
        if (!this.level.isClientSide && this.isAlive() && this.isEffectiveAi()) {
            ++this.eatingTime;
            ItemStack itemStack = this.getItemBySlot(EquipmentSlot.MAINHAND);
            int startEatingTime = 60;
            if (itemStack.isEdible() || itemStack.is(Items.POTION)) {
                if (this.eatingTime > startEatingTime + 40) {
                    ItemStack itemStack2 = itemStack.finishUsingItem(this.level, this);
                    if(itemStack.is(Items.POTION)) {
                        this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.GLASS_BOTTLE));
                    } else {
                        if (!itemStack2.isEmpty()) {
                            this.setItemSlot(EquipmentSlot.MAINHAND, itemStack2);
                        }
                    }

                    this.eatingTime = 0;
                } else if (this.eatingTime > startEatingTime && this.random.nextFloat() < 0.1F) {
                    if(itemStack.is(Items.POTION)) {
                        this.playSound(SoundEvents.GENERIC_DRINK, 1.0F, 1.0F);
                    } else {
                        this.playSound(this.getEatingSound(itemStack), 1.0F, 1.0F);
                        this.level.broadcastEntityEvent(this, (byte)45);
                    }
                }
            } else if(itemStack.getItem() instanceof ArmorItem ait) {
                if(this.eatingTime > 20) {
                    ItemStack equipped = this.getItemBySlot(ait.getSlot());
                    if(isItemBetterThanEquipped(itemStack, ait.getSlot())) {
                        if(!equipped.isEmpty()) {
                            spit(equipped);
                        }
                        this.setItemSlot(ait.getSlot(), itemStack.split(1));
                        this.playSound(ait.getEquipSound(), 1, 1);
                    }
                }
            }
        }

        if (this.isSleeping() || this.isImmobile()) {
            this.jumping = false;
            this.xxa = 0.0F;
            this.zza = 0.0F;
        }

        if(this.isPanicking() && this.isAlive()) {
            float dx = (getRandom().nextFloat() - 0.5f);
            float dy = (getRandom().nextFloat() - 0.5f);
            float dz = (getRandom().nextFloat() - 0.5f);
            if(tickCount % 5 == 0) lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(getX() + dx, getEyeY() + dy, getZ() + dz));

            if(getRandom().nextFloat() < 0.25f) {
                SoundEvent sound = getPanicSound();
                if(sound != null) {
                    playSound(sound, 1, 1.25f + 0.25f * getRandom().nextFloat());
                }
            }

            if(swingTime == 0) {
                swinging = true;
                swingingArm = getRandom().nextFloat() > 0.5f ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND;
                if(level instanceof ServerLevel sw) {
                    sw.getChunkSource().broadcastAndSend(this, new ClientboundAnimatePacket(this, getRandom().nextFloat() > 0.5f ? 0 : 3));
                }
            }
        }
        super.aiStep();
    }

    private int eatingTime;

    @Override
    public boolean canHoldItem(ItemStack stack) {
        ItemStack itemStack = this.getItemBySlot(EquipmentSlot.MAINHAND);
        return itemStack.isEmpty() || this.eatingTime > 0 && isItemBetterThanEquipped(stack);
    }

    public int getSwordLevel(ItemStack stack) {
        if(stack.is(Items.NETHERITE_SWORD)) return 5;
        if(stack.is(Items.DIAMOND_SWORD)) return 4;
        if(stack.is(Items.IRON_SWORD)) return 3;
        if(stack.is(Items.GOLDEN_SWORD)) return 2;
        if(stack.is(Items.STONE_SWORD)) return 1;
        if(stack.is(Items.WOODEN_SWORD)) return 0;

        if(stack.is(Items.BOW)) return 3;
        return -1;
    }

    public boolean isItemBetterThanEquipped(ItemStack stack) {
        return isItemBetterThanEquipped(stack, EquipmentSlot.MAINHAND);
    }

    public boolean isItemBetterThanEquipped(ItemStack stack, EquipmentSlot slot) {
        if(stack.isEmpty()) return false;

        ItemStack equipped = getItemBySlot(slot);
        if(equipped.isEmpty()) return true;
        if(equipped.isEdible() || equipped.is(Items.POTION)) return false;
        if(stack.isEdible() || stack.is(Items.POTION)) return true;

        Item equippedItem = equipped.getItem();
        Item stackItem = stack.getItem();

        if(stackItem instanceof SwordItem s1) {
            if(equippedItem instanceof SwordItem s2) {
                return s1.getDamage() > s2.getDamage();
            } else {
                return true;
            }
        }

        if(stackItem instanceof ArmorItem ait1) {
            if(equippedItem instanceof ArmorItem ait2) {
                float a = ait1.getMaterial().getToughness();
                float b = ait2.getMaterial().getToughness();
                return a > b;
            }
            return isItemBetterThanEquipped(stack, ait1.getSlot());
        }

        return false;
    }

    private void spit(ItemStack stack) {
        if (!stack.isEmpty() && !this.level.isClientSide) {
            ItemEntity itemEntity = new ItemEntity(this.level, this.getX() + this.getLookAngle().x, this.getY() + 1.0D, this.getZ() + this.getLookAngle().z, stack);
            itemEntity.setPickUpDelay(40);
            itemEntity.setThrower(this.getUUID());
            this.playSound(SoundEvents.FOX_SPIT, 1.0F, 1.0F);
            this.level.addFreshEntity(itemEntity);
        }
    }

    @Override
    protected void pickUpItem(ItemEntity item) {
        ItemStack itemStack = item.getItem();
        if (this.canHoldItem(itemStack)) {
            int i = itemStack.getCount();
            if (i > 1) {
                this.dropItem(itemStack.split(i - 1));
            }

            this.spit(this.getItemBySlot(EquipmentSlot.MAINHAND));
            this.onItemPickup(item);
            this.setItemSlot(EquipmentSlot.MAINHAND, itemStack.split(1));
            this.handDropChances[EquipmentSlot.MAINHAND.getIndex()] = 2.0F;
            this.take(item, itemStack.getCount());
            item.discard();
            this.eatingTime = 0;
        }
    }

    // Arrow shooting
    protected AbstractArrow createArrowProjectile(ItemStack arrow, float damageModifier) {
        return ProjectileUtil.getMobArrow(this, arrow, damageModifier);
    }

    @Override
    public void performRangedAttack(LivingEntity target, float pullProgress) {
        ItemStack itemStack = this.getProjectile(this.getItemInHand(ProjectileUtil.getWeaponHoldingHand(this, Items.BOW)));
        AbstractArrow persistentProjectileEntity = this.createArrowProjectile(itemStack, pullProgress);
        double d = target.getX() - this.getX();
        double e = target.getY(0.3333333333333333D) - persistentProjectileEntity.getY();
        double f = target.getZ() - this.getZ();
        double g = Math.sqrt(d * d + f * f);
        persistentProjectileEntity.shoot(d, e + g * 0.20000000298023224D, f, 1.6F, (float)(14 - this.level.getDifficulty().getId() * 4));
        this.playSound(SoundEvents.SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.level.addFreshEntity(persistentProjectileEntity);
    }

    public boolean canAttackWithOwner(LivingEntity target, YuunaEntity owner) {
        if (!(target instanceof Creeper) && !(target instanceof Ghast)) {
            if (target instanceof Wolf wolfEntity) {
                return !wolfEntity.isTame() || wolfEntity.getOwner() != owner;
            } else if (target instanceof AbstractHorse && ((AbstractHorse)target).isTamed()) {
                return false;
            } else if(target instanceof YuunaLivePlayerEntity minion) {
                return minion.getOwner() != owner;
            } else {
                return !(target instanceof TamableAnimal) || !((TamableAnimal)target).isTame();
            }
        } else {
            return false;
        }
    }

    // Cape
    public double capeX;
    public double capeY;
    public double capeZ;

    public double prevCapeX;
    public double prevCapeY;
    public double prevCapeZ;

    public float strideDistance;
    public float prevStrideDistance;

    private void updateCapeAngles() {
        this.prevCapeX = this.capeX;
        this.prevCapeY = this.capeY;
        this.prevCapeZ = this.capeZ;
        double d = this.getX() - this.capeX;
        double e = this.getY() - this.capeY;
        double f = this.getZ() - this.capeZ;
        double g = 10.0;
        if (d > 10.0) {
            this.prevCapeX = this.capeX = this.getX();
        }
        if (f > 10.0) {
            this.prevCapeZ = this.capeZ = this.getZ();
        }
        if (e > 10.0) {
            this.prevCapeY = this.capeY = this.getY();
        }
        if (d < -10.0) {
            this.prevCapeX = this.capeX = this.getX();
        }
        if (f < -10.0) {
            this.prevCapeZ = this.capeZ = this.getZ();
        }
        if (e < -10.0) {
            this.prevCapeY = this.capeY = this.getY();
        }
        this.capeX += d * 0.25;
        this.capeZ += f * 0.25;
        this.capeY += e * 0.25;
    }

    public boolean canRenderCapeTexture() {
        return canRenderCapeTexture(this.getClass());
    }

    public static <T extends YuunaLivePlayerEntity> boolean canRenderCapeTexture(Class<T> clz) {
        PlayerCape cape = clz.getAnnotation(PlayerCape.class);
        return cape != null;
    }

    @Override
    public void removeVehicle() {
        super.removeVehicle();
        setPanicking(false);
    }

    @Override
    public void tick() {
        if(this.equals(getVehicle())) {
            this.removeVehicle();
        }
        super.tick();
        this.updateCapeAngles();
        this.updateSwingTime();

        if(owner != null && owner.isDeadOrDying()) {
            setOwner(null);
        }
    }

    @Override
    public void rideTick() {
        super.rideTick();
        this.prevStrideDistance = this.strideDistance;
        this.strideDistance = 0.0f;
    }

    public ResourceLocation getCapeTexture() {
        return getCapeTexture(this.getClass());
    }

    public static <T extends YuunaLivePlayerEntity> ResourceLocation getCapeTexture(Class<T> clz) {
        PlayerCape cape = clz.getAnnotation(PlayerCape.class);
        if(cape == null) return null;
        return YuunaLive.id(cape.value());
    }
}
