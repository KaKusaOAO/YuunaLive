package com.kakaouo.mods.yuunalive.entities.ai.goal;

import com.kakaouo.mods.yuunalive.entities.YuunaLivePlayerEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Predicate;

public class YuunaLivePlayerPlaceBlockGoal extends MoveToBlockGoal {
    private final YuunaLivePlayerEntity entity;
    private final BlockState targetState;
    private final Predicate<BlockPos> placePredicate;
    protected int timer;

    public YuunaLivePlayerPlaceBlockGoal(YuunaLivePlayerEntity entity, double speed, int range, int maxYDifference, BlockState targetState, Predicate<BlockPos> placePredicate) {
        super(entity, speed, range, maxYDifference);
        this.entity = entity;
        this.targetState = targetState;
        this.placePredicate = placePredicate;
    }

    @Override
    public boolean canUse() {
        if(!(!entity.isSleeping() && super.canUse())) return false;
        ItemStack item = entity.getMainHandItem();
        if(item.getItem() instanceof BlockItem bl) {
            return targetState.equals(bl.getBlock().defaultBlockState());
        }
        return false;
    }

    @Override
    public double acceptedDistance() {
        return 2;
    }

    @Override
    public boolean shouldRecalculatePath() {
        return this.tryTicks % 10 == 0;
    }

    @Override
    protected boolean isValidTarget(LevelReader world, BlockPos pos) {
        return placePredicate.test(pos);
    }

    @Override
    public void tick() {
        if(!placePredicate.test(this.blockPos)) {
            stop();
            return;
        }

        if (this.isReachedTarget()) {
            entity.swinging = true;
            this.placeBlock();
        }
        super.tick();
    }

    protected void placeBlock() {
        if (!entity.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
            return;
        }
        entity.level.destroyBlock(this.blockPos, true, entity);
        entity.level.setBlockAndUpdate(this.blockPos, targetState);
        entity.playSound(targetState.getSoundType().getPlaceSound(), 1, 1);
        stop();
    }

    @Override
    public void start() {
        this.timer = 0;
        super.start();
    }

    @Override
    public void stop() {
        this.timer = 0;
        super.stop();
    }
}
