package com.kakaouo.mods.yuunalive.entities.ai.goal;

import com.kakaouo.mods.yuunalive.entities.YuunaLivePlayerEntity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class YuunaLivePlayerPickupItemGoal extends Goal {
    private YuunaLivePlayerEntity entity;
    final Predicate<ItemEntity> PICKABLE_DROP_FILTER = (item) -> {
        return !item.hasPickUpDelay() && item.isAlive() && entity.isItemBetterThanEquipped(item.getItem());
    };

    public YuunaLivePlayerPickupItemGoal(YuunaLivePlayerEntity entity) {
        this.entity = entity;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    public boolean canUse() {
        if (entity.getItemBySlot(EquipmentSlot.MAINHAND).isEdible()) {
            return false;
        } else if (entity.getTarget() == null && entity.getLastHurtByMob() == null) {
            if (entity.getRandom().nextInt(10) != 0) {
                return false;
            } else {
                List<ItemEntity> list = entity.level.getEntitiesOfClass(ItemEntity.class, entity.getBoundingBox().inflate(8.0D, 8.0D, 8.0D), PICKABLE_DROP_FILTER)
                        .stream().filter(i -> entity.isItemBetterThanEquipped(i.getItem())).sorted(Comparator.comparingInt(a -> entity.getSwordLevel(a.getItem()))).toList();
                return !list.isEmpty() && !entity.getItemBySlot(EquipmentSlot.MAINHAND).isEdible();
            }
        } else {
            return false;
        }
    }

    public void tick() {
        List<ItemEntity> list = entity.level.getEntitiesOfClass(ItemEntity.class, entity.getBoundingBox().inflate(8.0D, 8.0D, 8.0D), PICKABLE_DROP_FILTER)
                .stream().filter(i -> entity.isItemBetterThanEquipped(i.getItem())).sorted(Comparator.comparingInt(a -> entity.getSwordLevel(a.getItem()))).toList();
        ItemStack itemStack = entity.getItemBySlot(EquipmentSlot.MAINHAND);
        if (itemStack.isEmpty() && !list.isEmpty()) {
            entity.getNavigation().moveTo(list.get(0), 1.2000000476837158D);
        }
    }

    public void start() {
        List<ItemEntity> list = entity.level.getEntitiesOfClass(ItemEntity.class, entity.getBoundingBox().inflate(8.0D, 8.0D, 8.0D), PICKABLE_DROP_FILTER)
                .stream().filter(i -> entity.isItemBetterThanEquipped(i.getItem())).sorted(Comparator.comparingInt(a -> entity.getSwordLevel(a.getItem()))).toList();
        if (!list.isEmpty()) {
            entity.getNavigation().moveTo(list.get(0), 1.2000000476837158D);
        }
    }
}
