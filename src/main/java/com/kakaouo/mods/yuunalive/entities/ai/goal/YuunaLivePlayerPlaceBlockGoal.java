package com.kakaouo.mods.yuunalive.entities.ai.goal;

import com.kakaouo.mods.yuunalive.entities.YuunaLivePlayerEntity;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ai.goal.MoveToTargetPosGoal;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.WorldView;

import java.util.function.Predicate;

public class YuunaLivePlayerPlaceBlockGoal extends MoveToTargetPosGoal {
    private YuunaLivePlayerEntity entity;
    private BlockState targetState;
    private Predicate<BlockPos> placePredicate;
    protected int timer;

    public YuunaLivePlayerPlaceBlockGoal(YuunaLivePlayerEntity entity, double speed, int range, int maxYDifference, BlockState targetState, Predicate<BlockPos> placePredicate) {
        super(entity, speed, range, maxYDifference);
        this.entity = entity;
        this.targetState = targetState;
        this.placePredicate = placePredicate;
    }

    @Override
    public boolean canStart() {
        if(!(!entity.isSleeping() && super.canStart())) return false;
        ItemStack item = entity.getMainHandStack();
        if(item.getItem() instanceof BlockItem bl) {
            return targetState.equals(bl.getBlock().getDefaultState());
        }
        return false;
    }

    @Override
    public double getDesiredSquaredDistanceToTarget() {
        return 2;
    }

    @Override
    public boolean shouldResetPath() {
        return this.tryingTime % 10 == 0;
    }

    @Override
    protected boolean isTargetPos(WorldView world, BlockPos pos) {
        return placePredicate.test(pos);
    }

    @Override
    public void tick() {
        if(!placePredicate.test(this.targetPos)) {
            stop();
            return;
        }

        if (this.hasReached()) {
            entity.handSwinging = true;
            this.placeBlock();
        }
        super.tick();
    }

    protected void placeBlock() {
        if (!entity.world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
            return;
        }
        entity.world.breakBlock(this.targetPos, true, entity);
        entity.world.setBlockState(this.targetPos, targetState);
        entity.playSound(targetState.getSoundGroup().getPlaceSound(), 1, 1);
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
