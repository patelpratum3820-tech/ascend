package net.ascend.mod.entity.mob.ai;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;

public class KiteGoal extends Goal {
    private final MobEntity mob;
    private final double minDistance;
    private final double speed;

    public KiteGoal(MobEntity mob, double minDistance, double speed) {
        this.mob = mob;
        this.minDistance = minDistance;
        this.speed = speed;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    @Override
    public boolean canStart() {
        LivingEntity target = mob.getTarget();
        return target != null && target.isAlive() && mob.squaredDistanceTo(target) < (minDistance * minDistance);
    }

    @Override
    public void tick() {
        LivingEntity target = mob.getTarget();
        if (target == null) return;

        mob.getLookControl().lookAt(target, 30.0f, 30.0f);

        // Calculate vector away from target
        Vec3d away = mob.getPos().subtract(target.getPos()).normalize();
        Vec3d destination = mob.getPos().add(away.multiply(3.0));

        mob.getNavigation().startMovingTo(destination.x, destination.y, destination.z, speed);
    }
}

