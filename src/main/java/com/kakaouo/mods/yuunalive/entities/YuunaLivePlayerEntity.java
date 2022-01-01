package com.kakaouo.mods.yuunalive.entities;

import com.kakaouo.mods.yuunalive.YuunaLive;
import com.kakaouo.mods.yuunalive.annotations.PlayerCape;
import com.kakaouo.mods.yuunalive.annotations.PlayerName;
import com.kakaouo.mods.yuunalive.annotations.PlayerNickname;
import com.kakaouo.mods.yuunalive.annotations.PlayerSkin;
import com.kakaouo.mods.yuunalive.entities.ai.goal.*;
import com.kakaouo.mods.yuunalive.util.KakaUtils;
import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.command.argument.EntityAnchorArgumentType;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.RangedAttackMob;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.*;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.packet.s2c.play.EntityAnimationS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.LocateCommand;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;

import java.net.ServerSocket;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

public abstract class YuunaLivePlayerEntity extends PathAwareEntity implements RangedAttackMob {
    private static final TrackedData<Optional<UUID>> OWNER_UUID = DataTracker.registerData(YuunaLivePlayerEntity.class, TrackedDataHandlerRegistry.OPTIONAL_UUID);

    private final YuunaLivePlayerBowAttackGoal bowAttackGoal = new YuunaLivePlayerBowAttackGoal(this, 1.0D, 20, 15.0F);
    private final MeleeAttackGoal meleeAttackGoal = new YuunaLivePlayerMeleeAttackGoal(this, 1.0D, false);
    private YuunaEntity owner;

    protected YuunaLivePlayerEntity(EntityType<? extends YuunaLivePlayerEntity> entityType, World world) {
        super(entityType, world);
        this.setPersistent();
        this.updateAttackType();
    }

    protected static <T extends YuunaLivePlayerEntity> EntityType<T> getType(EntityType.EntityFactory<T> builder) {
        return FabricEntityTypeBuilder.create(SpawnGroup.CREATURE, builder)
                .spawnableFarFromPlayer()
                .dimensions(EntityDimensions.fixed(0.6f, 1.95f))
                .build();
    }

    public static DefaultAttributeContainer.Builder createPlayerAttributes() {
        return PlayerEntity.createPlayerAttributes()
                .add(EntityAttributes.GENERIC_FOLLOW_RANGE, 36.0)
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3)
                .add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK);
    }

    @Override
    protected void initGoals() {
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0f));
        this.goalSelector.add(8, new LookAroundGoal(this));
        this.initCustomGoals();
    }

    protected void initCustomGoals() {
        this.goalSelector.add(1, new SwimGoal(this));

        if(isAttractedByYuuna()) {
            this.goalSelector.add(1, new YuunaLivePlayerFindOwnerGoal(this));
        }
        if(doesAttackYuuna()) {
            this.targetSelector.add(2, new ActiveTargetGoal<>(this, YuunaEntity.class, 0,
                    false, false, this::canAttack
            ));
        }

        this.goalSelector.add(7, new WanderAroundFarGoal(this, 1.0));
        this.targetSelector.add(2, new RevengeGoal(this, this.getClass()).setGroupRevenge(ZombifiedPiglinEntity.class));
        this.targetSelector.add(1, new YuunaLivePlayerPickupItemGoal(this));
        this.targetSelector.add(1, new YuunaLivePlayerCancelAttackGoal(this));
        this.targetSelector.add(3, new YuunaLivePlayerTrackOwnerAttackerGoal(this));
        this.targetSelector.add(4, new YuunaLivePlayerAttackWithOwnerGoal(this));
        this.goalSelector.add(6, new YuunaLivePlayerFollowOwnerGoal(this, 1, 10, 2, false, false));
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(OWNER_UUID, Optional.empty());
    }

    public boolean canAttack(LivingEntity entity) {
        return getAttackScore(entity) >= 0;
    }

    public int getAttackScore(LivingEntity entity) {
        YuunaEntity owner = getOwner();
        if(owner != null) {
            if(owner.getAttacker() == entity) return 1;
            if(owner.getAttacking() == entity) return 1;
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
        float a = (float) getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        float b = EnchantmentHelper.getAttackDamage(getMainHandStack(), getGroup());
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

    public Identifier getTexture() {
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

    public Identifier getIdentifier() {
        return getIdentifier(this.getClass());
    }

    public static <T extends YuunaLivePlayerEntity> Identifier getTexture(Class<T> clz) {
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

    public static <T extends YuunaLivePlayerEntity> Identifier getIdentifier(Class<T> clz) {
        return YuunaLive.id(getPlayerName(clz).toLowerCase(Locale.ROOT));
    }

    public boolean isAttractedByYuuna() {
        return true;
    }

    public boolean doesAttackYuuna() {
        return false;
    }

    @Override
    public Text getName() {
        MutableText playerName = new LiteralText(getPlayerName());
        String nickname = getNickName();
        MutableText nickTag = new LiteralText("");

        if(nickname != null) {
            MutableText nickName = new LiteralText(getNickName());
            nickName.setStyle(nickName.getStyle().withColor(getNickNameColor()));

            if(this instanceof YuunaEntity) {
                playerName.formatted(Formatting.LIGHT_PURPLE);
                nickName.formatted(Formatting.BOLD);
            }

            nickTag = nickTag.append(new TranslatableText("[%s] ", nickName).formatted(Formatting.GREEN));
        }

        return nickTag.append(playerName);
    }

    public TextColor getNickNameColor() {
        return TextColor.fromFormatting(Formatting.AQUA);
    }

    public boolean hasOwner() {
        return owner != null;
    }

    public YuunaEntity getOwner() {
        return owner;
    }

    public UUID getOwnerUuid() {
        return this.dataTracker.get(OWNER_UUID).orElse(null);
    }

    public void setOwner(YuunaEntity owner) {
        if(owner != null) {
            this.setOwnerById(owner.getUuid());
        } else {
            this.setOwnerById(null);
        }
    }

    public void setOwnerById(UUID uuid) {
        this.dataTracker.set(OWNER_UUID, Optional.ofNullable(uuid));

        if(!(this.world instanceof ServerWorld sw)) return;
        if(uuid == null) {
            this.owner = null;
        } else if(sw.getEntity(uuid) instanceof YuunaEntity yuuna) {
            this.owner = yuuna;
        }
    }

    @Override
    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("Panic", isPanicking());
        if(owner != null) {
            nbt.putUuid("Owner", owner.uuid);
        }
    }

    @Override
    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        if(nbt.contains("Panic", NbtElement.BYTE_TYPE)) {
            setPanicking(nbt.getBoolean("Panic"));
        }

        if(nbt.containsUuid("Owner")) {
            setOwnerById(nbt.getUuid("Owner"));
        }
    }

    @Override
    public void equipStack(EquipmentSlot slot, ItemStack stack) {
        super.equipStack(slot, stack);

        if (!this.world.isClient) {
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
    public boolean canMoveVoluntarily() {
        return super.canMoveVoluntarily() && !isPanicking();
    }

    @Override
    public boolean canBeControlledByRider() {
        return false;
    }

    public void updateAttackType() {
        if (this.world != null && !this.world.isClient) {
            this.goalSelector.remove(this.meleeAttackGoal);
            this.goalSelector.remove(this.bowAttackGoal);
            ItemStack itemStack = this.getStackInHand(ProjectileUtil.getHandPossiblyHolding(this, Items.BOW));
            if (itemStack.isOf(Items.BOW)) {
                int i = 20;
                if (this.world.getDifficulty() != Difficulty.HARD) {
                    i = 40;
                }

                this.bowAttackGoal.setAttackInterval(i);
                this.goalSelector.add(2, this.bowAttackGoal);
            } else {
                this.goalSelector.add(2, this.meleeAttackGoal);
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
        ItemEntity itemEntity = new ItemEntity(this.world, this.getX(), this.getY(), this.getZ(), stack);
        this.world.spawnEntity(itemEntity);
    }

    @Override
    public EntityPose getPose() {
        if(isSneaking()) return EntityPose.CROUCHING;
        if(isSleeping()) return EntityPose.SLEEPING;
        return super.getPose();
    }

    public SoundEvent getPanicSound() {
        return null;
    }

    @Override
    public void tickMovement() {
        if (!this.world.isClient && this.isAlive() && this.canMoveVoluntarily()) {
            ++this.eatingTime;
            ItemStack itemStack = this.getEquippedStack(EquipmentSlot.MAINHAND);
            int startEatingTime = 60;
            if (itemStack.isFood() || itemStack.isOf(Items.POTION)) {
                if (this.eatingTime > startEatingTime + 40) {
                    ItemStack itemStack2 = itemStack.finishUsing(this.world, this);
                    if(itemStack.isOf(Items.POTION)) {
                        this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.GLASS_BOTTLE));
                    } else {
                        if (!itemStack2.isEmpty()) {
                            this.equipStack(EquipmentSlot.MAINHAND, itemStack2);
                        }
                    }

                    this.eatingTime = 0;
                } else if (this.eatingTime > startEatingTime && this.random.nextFloat() < 0.1F) {
                    if(itemStack.isOf(Items.POTION)) {
                        this.playSound(SoundEvents.ENTITY_GENERIC_DRINK, 1.0F, 1.0F);
                    } else {
                        this.playSound(this.getEatSound(itemStack), 1.0F, 1.0F);
                        this.world.sendEntityStatus(this, (byte)45);
                    }
                }
            } else if(itemStack.getItem() instanceof ArmorItem ait) {
                if(this.eatingTime > 20) {
                    ItemStack equipped = this.getEquippedStack(ait.getSlotType());
                    if(isItemBetterThanEquipped(itemStack, ait.getSlotType())) {
                        if(!equipped.isEmpty()) {
                            spit(equipped);
                        }
                        this.equipStack(ait.getSlotType(), itemStack.split(1));
                        this.playSound(ait.getEquipSound(), 1, 1);
                    }
                }
            }
        }

        if (this.isSleeping() || this.isImmobile()) {
            this.jumping = false;
            this.sidewaysSpeed = 0.0F;
            this.forwardSpeed = 0.0F;
        }

        if(this.isPanicking() && this.isAlive()) {
            float dx = (getRandom().nextFloat() - 0.5f);
            float dy = (getRandom().nextFloat() - 0.5f);
            float dz = (getRandom().nextFloat() - 0.5f);
            if(age % 5 == 0) lookAt(EntityAnchorArgumentType.EntityAnchor.EYES, new Vec3d(getX() + dx, getEyeY() + dy, getZ() + dz));

            if(getRandom().nextFloat() < 0.25f) {
                SoundEvent sound = getPanicSound();
                if(sound != null) {
                    playSound(sound, 1, 1.25f + 0.25f * getRandom().nextFloat());
                }
            }

            if(handSwingTicks == 0) {
                handSwinging = true;
                preferredHand = getRandom().nextFloat() > 0.5f ? Hand.MAIN_HAND : Hand.OFF_HAND;
                if(world instanceof ServerWorld sw) {
                    sw.getChunkManager().sendToNearbyPlayers(this, new EntityAnimationS2CPacket(this, getRandom().nextFloat() > 0.5f ? 0 : 3));
                }
            }
        }
        super.tickMovement();
    }

    private int eatingTime;

    @Override
    public boolean canPickupItem(ItemStack stack) {
        ItemStack itemStack = this.getEquippedStack(EquipmentSlot.MAINHAND);
        return itemStack.isEmpty() || this.eatingTime > 0 && isItemBetterThanEquipped(stack);
    }

    public int getSwordLevel(ItemStack stack) {
        if(stack.isOf(Items.NETHERITE_SWORD)) return 5;
        if(stack.isOf(Items.DIAMOND_SWORD)) return 4;
        if(stack.isOf(Items.IRON_SWORD)) return 3;
        if(stack.isOf(Items.GOLDEN_SWORD)) return 2;
        if(stack.isOf(Items.STONE_SWORD)) return 1;
        if(stack.isOf(Items.WOODEN_SWORD)) return 0;

        if(stack.isOf(Items.BOW)) return 3;
        return -1;
    }

    public boolean isItemBetterThanEquipped(ItemStack stack) {
        return isItemBetterThanEquipped(stack, EquipmentSlot.MAINHAND);
    }

    public boolean isItemBetterThanEquipped(ItemStack stack, EquipmentSlot slot) {
        if(stack.isEmpty()) return false;

        ItemStack equipped = getEquippedStack(slot);
        if(equipped.isEmpty()) return true;
        if(equipped.isFood() || equipped.isOf(Items.POTION)) return false;
        if(stack.isFood() || stack.isOf(Items.POTION)) return true;

        Item equippedItem = equipped.getItem();
        Item stackItem = stack.getItem();

        if(stackItem instanceof SwordItem s1) {
            if(equippedItem instanceof SwordItem s2) {
                return s1.getAttackDamage() > s2.getAttackDamage();
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
            return isItemBetterThanEquipped(stack, ait1.getSlotType());
        }

        return false;
    }

    private void spit(ItemStack stack) {
        if (!stack.isEmpty() && !this.world.isClient) {
            ItemEntity itemEntity = new ItemEntity(this.world, this.getX() + this.getRotationVector().x, this.getY() + 1.0D, this.getZ() + this.getRotationVector().z, stack);
            itemEntity.setPickupDelay(40);
            itemEntity.setThrower(this.getUuid());
            this.playSound(SoundEvents.ENTITY_FOX_SPIT, 1.0F, 1.0F);
            this.world.spawnEntity(itemEntity);
        }
    }

    @Override
    protected void loot(ItemEntity item) {
        ItemStack itemStack = item.getStack();
        if (this.canPickupItem(itemStack)) {
            int i = itemStack.getCount();
            if (i > 1) {
                this.dropItem(itemStack.split(i - 1));
            }

            this.spit(this.getEquippedStack(EquipmentSlot.MAINHAND));
            this.triggerItemPickedUpByEntityCriteria(item);
            this.equipStack(EquipmentSlot.MAINHAND, itemStack.split(1));
            this.handDropChances[EquipmentSlot.MAINHAND.getEntitySlotId()] = 2.0F;
            this.sendPickup(item, itemStack.getCount());
            item.discard();
            this.eatingTime = 0;
        }
    }

    // Arrow shooting
    protected PersistentProjectileEntity createArrowProjectile(ItemStack arrow, float damageModifier) {
        return ProjectileUtil.createArrowProjectile(this, arrow, damageModifier);
    }

    @Override
    public void attack(LivingEntity target, float pullProgress) {
        ItemStack itemStack = this.getArrowType(this.getStackInHand(ProjectileUtil.getHandPossiblyHolding(this, Items.BOW)));
        PersistentProjectileEntity persistentProjectileEntity = this.createArrowProjectile(itemStack, pullProgress);
        double d = target.getX() - this.getX();
        double e = target.getBodyY(0.3333333333333333D) - persistentProjectileEntity.getY();
        double f = target.getZ() - this.getZ();
        double g = Math.sqrt(d * d + f * f);
        persistentProjectileEntity.setVelocity(d, e + g * 0.20000000298023224D, f, 1.6F, (float)(14 - this.world.getDifficulty().getId() * 4));
        this.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
        this.world.spawnEntity(persistentProjectileEntity);
    }

    public boolean canAttackWithOwner(LivingEntity target, YuunaEntity owner) {
        if (!(target instanceof CreeperEntity) && !(target instanceof GhastEntity)) {
            if (target instanceof WolfEntity wolfEntity) {
                return !wolfEntity.isTamed() || wolfEntity.getOwner() != owner;
            } else if (target instanceof HorseBaseEntity && ((HorseBaseEntity)target).isTame()) {
                return false;
            } else if(target instanceof YuunaLivePlayerEntity minion) {
                return minion.getOwner() != owner;
            } else {
                return !(target instanceof TameableEntity) || !((TameableEntity)target).isTamed();
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
    public void dismountVehicle() {
        super.dismountVehicle();
        setPanicking(false);
    }

    @Override
    public void tick() {
        if(this.equals(getVehicle())) {
            this.dismountVehicle();
        }
        super.tick();
        this.updateCapeAngles();
        this.tickHandSwing();

        if(owner != null && owner.isDead()) {
            setOwner(null);
        }
    }

    @Override
    public void tickRiding() {
        super.tickRiding();
        this.prevStrideDistance = this.strideDistance;
        this.strideDistance = 0.0f;
    }

    public Identifier getCapeTexture() {
        return getCapeTexture(this.getClass());
    }

    public static <T extends YuunaLivePlayerEntity> Identifier getCapeTexture(Class<T> clz) {
        PlayerCape cape = clz.getAnnotation(PlayerCape.class);
        if(cape == null) return null;
        return YuunaLive.id(cape.value());
    }
}
