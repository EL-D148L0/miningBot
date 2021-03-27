package max.sander;

import java.util.ArrayList;

public class ViewCalculations {
    static BlockPos obstructedTargetPoint(BlockPos target, double[] eyePos, ArrayList<BlockPos> possibleObstructions) {
        //will return null if looking at target is impossible/not easily calculatable
        // otherwise it returns a point that you have to look at in order to see the target.

        return null;
    }
    static ArrayList<Integer> getVisibleSides(double[] eyePos, BlockPos block) {
        //returns list of directions in which you have to move block by 0.5 to get to a visible side
        ArrayList<Integer> out = new ArrayList<>();
        if (eyePos[0] < block.getX()) {
            out.add(Directions.NEGATIVE_X);
        }
        if (eyePos[0] > block.getX() + 1) {
            out.add(Directions.POSITIVE_X);
        }
        if (eyePos[1] < block.getY()) {
            out.add(Directions.NEGATIVE_Y);
        }
        if (eyePos[1] > block.getY() + 1) {
            out.add(Directions.POSITIVE_Y);
        }
        if (eyePos[2] < block.getZ()) {
            out.add(Directions.NEGATIVE_Z);
        }
        if (eyePos[2] > block.getZ() + 1) {
            out.add(Directions.POSITIVE_Z);
        }
        if (out.size() == 0) {
            out.add(Directions.NEGATIVE_X);
            out.add(Directions.POSITIVE_X);
            out.add(Directions.NEGATIVE_Y);
            out.add(Directions.POSITIVE_Y);
            out.add(Directions.NEGATIVE_Z);
            out.add(Directions.POSITIVE_Z);
        }
        return out;
    }
    static boolean cutsBlock(Ray3D ray, BlockPos block) {
        ArrayList<Integer> sides = getVisibleSides(ray.getStartPointVector().toDoubleArray(), block);
        for (int i : sides) {
            int plane;
            Vector3D planeNormal;
            if (i == Directions.POSITIVE_X || i == Directions.NEGATIVE_X) {
                plane = 0;
                planeNormal = new Vector3D(1, 0, 0);
            } else if (i == Directions.POSITIVE_Y || i == Directions.NEGATIVE_Y){
                plane = 1;
                planeNormal = new Vector3D(0, 1, 0);
            } else if (i == Directions.POSITIVE_Z || i == Directions.NEGATIVE_Z) {
                plane = 2;
                planeNormal = new Vector3D(0, 0, 1);
            }

        }
        return false;
    }

}
