package max.sander;

import static max.sander.Main.distance3D;

public class Direction {
    private double yaw;
    private double pitch;

    public Direction(double yaw, double pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
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
