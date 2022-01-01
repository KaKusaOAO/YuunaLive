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

public class YuunaLivePlayerBreakBlockGoal extends MoveToTargetPosGoal {
    private final YuunaLivePlayerEntity entity;
    private final Predicate<BlockState> predicate;
    protected int timer;

    public YuunaLivePlayerBreakBlockGoal(YuunaLivePlayerEntity entity, double speed, int range, int maxYDifference, Predicate<BlockState> predicate) {
        super(entity, speed, range, maxYDifference);
        this.entity = entity;
        this.predicate = predicate;
    }

    @Override
    public boolean canStart() {
        if(!(!entity.isSleeping() && super.canStart())) return false;
        ItemStack item = entity.getMainHandStack();
        if(item.getItem() instanceof BlockItem bl) {
            return !predicate.test(bl.getBlock().getDefaultState());
        }
        return true;
    }

    @Override
    public double getDesiredDistanceToTarget() {
        return 2.0;
    }

    @Override
    public boolean shouldResetPath() {
        return this.tryingTime % 10 == 0;
    }

    @Override
    protected boolean isTargetPos(WorldView world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos);
        return predicate.test(blockState);
    }

    private BlockState getTargetBlockState() {
        return entity.world.getBlockState(this.targetPos);
    }

    @Override
    public void tick() {
        BlockState blockState = getTargetBlockState();
        if(!predicate.test(blockState)) {
            stop();
            return;
        }

        int total = Math.round(blockState.getBlock().getHardness() * 20);
        if (this.hasReached()) {
            entity.handSwinging = true;
            if (this.timer >= total) {
                this.breakBlock();
            } else {
                ++this.timer;
                if(this.timer % 5 == 0) {
                    entity.playSound(blockState.getSoundGroup().getBreakSound(), 1.0f, 1.0f);
                }
            }
        }
        super.tick();
    }

    protected void breakBlock() {
        if (!entity.world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) {
            return;
        }
        BlockState state = entity.world.getBlockState(this.targetPos);
        entity.world.breakBlock(this.targetPos, true, entity);
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
