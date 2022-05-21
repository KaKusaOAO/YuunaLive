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

public class YuunaLivePlayerBreakBlockGoal extends MoveToBlockGoal {
    private final YuunaLivePlayerEntity entity;
    private final Predicate<BlockState> predicate;
    protected int timer;

    public YuunaLivePlayerBreakBlockGoal(YuunaLivePlayerEntity entity, double speed, int range, int maxYDifference, Predicate<BlockState> predicate) {
        super(entity, speed, range, maxYDifference);
        this.entity = entity;
        this.predicate = predicate;
    }

    @Override
    public boolean canUse() {
        if(!(!entity.isSleeping() && super.canUse())) return false;
        ItemStack item = entity.getMainHandItem();
        if(item.getItem() instanceof BlockItem bl) {
            return !predicate.test(bl.getBlock().defaultBlockState());
        }
        return true;
    }

    @Override
    public double acceptedDistance() {
        return 2.0;
    }

    @Override
    public boolean shouldRecalculatePath() {
        return this.tryTicks % 10 == 0;
    }

    @Override
    protected boolean isValidTarget(LevelReader world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        return predicate.test(blockState);
    }

    private BlockState getTargetBlockState() {
        return entity.level.getBlockState(this.blockPos);
    }

    @Override
    public void tick() {
        BlockState blockState = getTargetBlockState();
        if(!predicate.test(blockState)) {
            stop();
            return;
        }

        int total = Math.round(blockState.getBlock().defaultDestroyTime() * 20);
        if (this.isReachedTarget()) {
            entity.swinging = true;
            if (this.timer >= total) {
                this.breakBlock();
            } else {
                ++this.timer;
                if(this.timer % 5 == 0) {
                    entity.playSound(blockState.getSoundType().getBreakSound(), 1.0f, 1.0f);
                }
            }
        }
        super.tick();
    }

    protected void breakBlock() {
        if (!entity.level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)) {
            return;
        }
        BlockState state = entity.level.getBlockState(this.blockPos);
        entity.level.destroyBlock(this.blockPos, true, entity);
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
