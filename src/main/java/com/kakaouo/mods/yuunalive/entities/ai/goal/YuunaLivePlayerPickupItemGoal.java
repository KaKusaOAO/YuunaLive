package com.kakaouo.mods.yuunalive.entities.ai.goal;

import com.kakaouo.mods.yuunalive.entities.YuunaLivePlayerEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.ItemStack;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

public class YuunaLivePlayerPickupItemGoal extends Goal {
    private YuunaLivePlayerEntity entity;
    final Predicate<ItemEntity> PICKABLE_DROP_FILTER = (item) -> {
        return !item.cannotPickup() && item.isAlive() && entity.isItemBetterThanEquipped(item.getStack());
    };

    public YuunaLivePlayerPickupItemGoal(YuunaLivePlayerEntity entity) {
        this.entity = entity;
        this.setControls(EnumSet.of(Control.MOVE));
    }

    public boolean canStart() {
        if (entity.getEquippedStack(EquipmentSlot.MAINHAND).isFood()) {
            return false;
        } else if (entity.getTarget() == null && entity.getAttacker() == null) {
            if (entity.getRandom().nextInt(10) != 0) {
                return false;
            } else {
                List<ItemEntity> list = entity.world.getEntitiesByClass(ItemEntity.class, entity.getBoundingBox().expand(8.0D, 8.0D, 8.0D), PICKABLE_DROP_FILTER)
                        .stream().filter(i -> entity.isItemBetterThanEquipped(i.getStack())).sorted(Comparator.comparingInt(a -> entity.getSwordLevel(a.getStack()))).toList();
                return !list.isEmpty() && !entity.getEquippedStack(EquipmentSlot.MAINHAND).isFood();
            }
        } else {
            return false;
        }
    }

    public void tick() {
        List<ItemEntity> list = entity.world.getEntitiesByClass(ItemEntity.class, entity.getBoundingBox().expand(8.0D, 8.0D, 8.0D), PICKABLE_DROP_FILTER)
                .stream().filter(i -> entity.isItemBetterThanEquipped(i.getStack())).sorted(Comparator.comparingInt(a -> entity.getSwordLevel(a.getStack()))).toList();
        ItemStack itemStack = entity.getEquippedStack(EquipmentSlot.MAINHAND);
        if (itemStack.isEmpty() && !list.isEmpty()) {
            entity.getNavigation().startMovingTo(list.get(0), 1.2000000476837158D);
        }
    }

    public void start() {
        List<ItemEntity> list = entity.world.getEntitiesByClass(ItemEntity.class, entity.getBoundingBox().expand(8.0D, 8.0D, 8.0D), PICKABLE_DROP_FILTER)
                .stream().filter(i -> entity.isItemBetterThanEquipped(i.getStack())).sorted(Comparator.comparingInt(a -> entity.getSwordLevel(a.getStack()))).toList();
        if (!list.isEmpty()) {
            entity.getNavigation().startMovingTo(list.get(0), 1.2000000476837158D);
        }
    }
}
