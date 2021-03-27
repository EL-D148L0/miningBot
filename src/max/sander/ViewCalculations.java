package max.sander;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class ViewCalculations {
    static BlockPos obstructedTargetPoint(BlockPos target, double[] eyePos, ArrayList<BlockPos> possibleObstructions) {
        //will return null if looking at target is impossible/not easily calculatable
        // otherwise it returns a point that you have to look at in order to see the target.

        //suchmuster: mitten -> mitten der kanten -> ecken -> raster
        ArrayList<Integer> sides = getVisibleSides(eyePos,target);
        Queue<BlockPos> queue = new LinkedList<>();
        BlockPos targetCenter = target.addX(0.5).addY(0.5).addZ(0.5);
        for (int i : sides) {
            queue.add(target.move(i, 0.5));
        }



        return null;
    }
    static boolean getsThroughToTarget(Ray3D ray, BlockPos target, ArrayList<BlockPos> possibleObstructions) {
        possibleObstructions.remove(target);// just to make sure no whackiness happens
        if (!cutsBlock(ray, target)) return false;
        for (BlockPos block : possibleObstructions) {
            if (cutsBlock(ray, block)) return false;
        }
        return true;
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
            Plane3D plane3D = null;
            BlockPos blockCenter = block.addX(0.5).addY(0.5).addZ(0.5);
            if (i == Directions.POSITIVE_X || i == Directions.NEGATIVE_X) {
                plane3D = new Plane3D(new Vector3D(blockCenter.move(i, 0.5).toDoubleArray()), new Vector3D(1, 0, 0));
            } else if (i == Directions.POSITIVE_Y || i == Directions.NEGATIVE_Y){
                plane3D = new Plane3D(new Vector3D(blockCenter.move(i, 0.5).toDoubleArray()), new Vector3D(0, 1, 0));
            } else if (i == Directions.POSITIVE_Z || i == Directions.NEGATIVE_Z) {
                plane3D = new Plane3D(new Vector3D(blockCenter.move(i, 0.5).toDoubleArray()), new Vector3D(0, 0, 1));
            }
            Vector3D intersectPoint = Vector3D.intersectPointRay(ray, plane3D);

            if (intersectPoint != null) {
                if (isPointWithinBlock(intersectPoint, block)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean isPointWithinBlock(Vector3D point, BlockPos block) {
        return point.getX() >= block.getX() && point.getX() <= block.getX() + 1 && point.getY() >= block.getY() && point.getY() <= block.getY() + 1 && point.getZ() >= block.getZ() && point.getZ() <= block.getZ() + 1;
    }

}
