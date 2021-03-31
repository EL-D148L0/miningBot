package max.sander;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class ViewCalculations {
    static BlockPos obstructedTargetPoint(BlockPos target, double[] eyePos, ArrayList<BlockPos> possibleObstructions) {
        //todo maybe think of a better name for this function
        //will return null if looking at target is impossible/not easily calculatable
        // otherwise it returns a point that you have to look at in order to see the target.

        //search pattern: centers -> middles of edges -> corners -> grid
        BlockPos eyePosBP = new BlockPos(eyePos);
        BlockPos out = null;
        ArrayList<Integer> sides = getVisibleSides(eyePos,target);
        Queue<BlockPos> queue = new LinkedList<>();// queue of points on the surface of the target that will be checked for visibility
        BlockPos targetCenter = target.addX(0.5).addY(0.5).addZ(0.5);
        queue.add(targetCenter);// adding blocks center
        for (int i : sides) {// centers
            queue.add(targetCenter.move(i, 0.5));
        }
        out = testQueue(queue, target, eyePosBP, possibleObstructions);
        if (out != null) return out;

        for (int i : sides) {// middles of edges
            int[] dirs = getPerpendicularDirections(i);
            for (int j : dirs) {
                queue.add(targetCenter.move(i, 0.5).move(j, 0.4375));
            }
        }
        out = testQueue(queue, target, eyePosBP, possibleObstructions);
        if (out != null) return out;
        for (int i : sides) {// corners
            int[] dirs = getPositivePerpendicularDirections(i);
            queue.add(targetCenter.move(i, 0.5).move(dirs[0], 0.4375).move(dirs[1], 0.4375));
            queue.add(targetCenter.move(i, 0.5).move(dirs[0], 0.4375).move(dirs[1], 0.-4375));
            queue.add(targetCenter.move(i, 0.5).move(dirs[0], -0.4375).move(dirs[1], 0.4375));
            queue.add(targetCenter.move(i, 0.5).move(dirs[0], -0.4375).move(dirs[1], 0.-4375));
        }
        out = testQueue(queue, target, eyePosBP, possibleObstructions);
        if (out != null) return out;
        for (int i : sides) {// grid
            int[] dirs = getPositivePerpendicularDirections(i);
            for (double j = -0.4375; j <= 0.4375; j += 0.0625) {
                for (double k = -0.4375; k <= 0.4375; k += 0.0625) {
                    queue.add(targetCenter.move(i, 0.5).move(dirs[0], j).move(dirs[1], k));
                }
            }
        }
        out = testQueue(queue, target, eyePosBP, possibleObstructions);
        if (out != null) return out;


        return null;
    }
    static int[] getPerpendicularDirections(int in) {
        if (in == Directions.POSITIVE_X || in == Directions.NEGATIVE_X) {
            return new int[] {Directions.POSITIVE_Y, Directions.NEGATIVE_Y, Directions.POSITIVE_Z, Directions.NEGATIVE_Z};
        }
        if (in == Directions.POSITIVE_Y || in == Directions.NEGATIVE_Y) {
            return new int[] {Directions.POSITIVE_X, Directions.NEGATIVE_X, Directions.POSITIVE_Z, Directions.NEGATIVE_Z};
        }
        if (in == Directions.POSITIVE_Z || in == Directions.NEGATIVE_Z) {
            return new int[] {Directions.POSITIVE_Y, Directions.NEGATIVE_Y, Directions.POSITIVE_X, Directions.NEGATIVE_X};
        }
        return null;
    }
    static int[] getPositivePerpendicularDirections(int in) {
        if (in == Directions.POSITIVE_X || in == Directions.NEGATIVE_X) {
            return new int[] {Directions.POSITIVE_Y, Directions.POSITIVE_Z};
        }
        if (in == Directions.POSITIVE_Y || in == Directions.NEGATIVE_Y) {
            return new int[] {Directions.POSITIVE_X, Directions.POSITIVE_Z};
        }
        if (in == Directions.POSITIVE_Z || in == Directions.NEGATIVE_Z) {
            return new int[] {Directions.POSITIVE_Y, Directions.POSITIVE_X};
        }
        return null;
    }
    static private BlockPos testQueue(Queue<BlockPos> queue, BlockPos target, BlockPos eyePos, ArrayList<BlockPos> possibleObstructions) {
        
        while (queue.size() > 0) {
            BlockPos currentTestTarget = queue.remove();
            Ray3D testRay = new Ray3D(eyePos, currentTestTarget);
            if (getsThroughToTarget(testRay, target, possibleObstructions)) return currentTestTarget;
        }
        return null;
    }
    static private boolean getsThroughToTarget(Ray3D ray, BlockPos target, ArrayList<BlockPos> possibleObstructions) {
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
            Plane3D plane3D;
            BlockPos blockCenter = block.addX(0.5).addY(0.5).addZ(0.5);
            plane3D = new Plane3D(new Vector3D(blockCenter.move(i, 0.5).toDoubleArray()), new Vector3D(new BlockPos(0, 0, 0).move(i, 1).toDoubleArray()));
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
