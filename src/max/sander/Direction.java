package max.sander;

import static max.sander.Main.distance3D;
import static max.sander.Util.round;

public class Direction {
    private final double yaw;
    private final double pitch;

    public Direction(double targetYaw, double targetPitch) {
        this.yaw = round(targetYaw, 1);
        this.pitch = round(targetPitch, 1);
    }
    public Direction(double[] eyePos, BlockPos target) {
        double xDiff = (eyePos[0] - (target.getX()+ 0.5));
        double yDiff = ((eyePos[1]) - (target.getY()+ 0.5));
        double zDiff = (eyePos[2] - (target.getZ()+ 0.5));
        double targetYaw = 0;
        double targetPitch = 0;
        if (xDiff == 0) {
            if (zDiff < 0) {
                targetYaw = 0;
            } else if (zDiff > 0) {
                targetYaw = 180;
            }
        } else if (xDiff > 0) {
            targetYaw = Math.toDegrees(Math.atan(zDiff / xDiff)) + 90;
        } else if (xDiff < 0) {
            targetYaw = Math.toDegrees(Math.atan(zDiff / xDiff)) - 90;
        }

        double distance = distance3D(eyePos, target.toDoubleArray());
        if (distance != 0) targetPitch = Math.toDegrees(Math.asin(yDiff/distance));
        targetYaw = round(targetYaw, 1);
        targetPitch = round(targetPitch, 1);
        this.yaw = targetYaw;
        this.pitch = targetPitch;
    }

    public double getYaw() {
        return yaw;
    }

    public double getPitch() {
        return pitch;
    }
}
