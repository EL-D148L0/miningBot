package max.sander;

import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static max.sander.Main.robot;

public class Vein {
    ArrayList<BlockPos> seenBlocks;// blocks that are mined get removed from this list
    ArrayList<BlockPos> oreBlocks;
    ArrayList<BlockPos> minedBlocks;
    ArrayList<BlockPos> hazardBlockingBlocks;
    ArrayList<BlockPosWithHeight> floorPlan;// does not include spaces with height 1

    public Vein(ArrayList<BlockPos> seenBlocks, ArrayList<BlockPos> oreBlocks, ArrayList<BlockPos> minedBlocks, ArrayList<BlockPos> hazardBlockingBlocks) {
        this.seenBlocks = seenBlocks;
        this.oreBlocks = oreBlocks;
        this.minedBlocks = minedBlocks;
        this.hazardBlockingBlocks = hazardBlockingBlocks;
        this.floorPlan = getFloorPlan();
        //System.out.println("aaaa" + getUnknownNeighbors(new BlockPos(45, 56, 86)));
    }

    public ArrayList<BlockPosWithHeight> getFloorPlan() {
        floorPlan = new ArrayList<>();
        for (BlockPos block : seenBlocks) {
            int height = 0;
            while (true) {
                if (minedBlocks.contains(block.up(height + 1))) {
                    height += 1;
                } else break;
            }
            if (height > 1) {
                floorPlan.add(new BlockPosWithHeight(block, height));
            }
        }
        return floorPlan;
    }
    public boolean mine() throws InterruptedException, HowDidThisHappenException {
        // if this is at a different time than right after discovering ore in the tunnel weird things might happen.

        BlockPosWithHeight startPoint = getStartPoint();
        moveTo(startPoint);
        int startDirection = getStartDirection(startPoint);
        if (!minedBlocks.contains(new BlockPos(startPoint.up(2).move(startDirection)))) {

        }
        return true;
    }

    private int scan(BlockPos minedBlock) {
        ArrayList<BlockPos> unknownNeighbors = getUnknownNeighbors(minedBlock);

        return ScanResults.OK;
    }
    private boolean pointAtBlock(BlockPos target) throws InterruptedException {
        //todo so far not tested
        String debug = Main.getDebug(Main.getGameScreen());

        double targetYaw;
        double targetPitch;
        double eyeHeight = 1.62;
        double[] eyePos = Main.getPlayerPos(debug);
        eyePos[1] += eyeHeight;
        ArrayList<BlockPos> possibleObstacles = getPossibleObstacles(target, eyePos);
        BlockPos pointingTarget = ViewCalculations.obstructedTargetPoint(target, eyePos, possibleObstacles);
        if (pointingTarget == null) return false;
        Direction dir = new Direction(eyePos, pointingTarget);
        targetYaw = dir.getYaw();
        targetPitch = dir.getPitch();


        Point mouseLocation = MouseInfo.getPointerInfo().getLocation();
        int mouseX = mouseLocation.x;
        int mouseY = mouseLocation.y;
        double[] facing;
        double yawDiff;
        double pitchDiff;
        while (true) {
            debug = Main.getDebug(Main.getGameScreen());
            facing = Main.getFacing(debug);
            yawDiff = targetYaw - facing[0];
            pitchDiff = targetPitch - facing[1];
            if (yawDiff < -180) {
                yawDiff +=360;
            }
            if (yawDiff > 180) {
                yawDiff -=360;
            }
            if (target == Main.getLookingAtBlockPos(debug)) break;
            if (yawDiff == 0 && pitchDiff == 0) break;
            robot.mouseMove((int) (mouseX + Math.round(yawDiff*10)), (int) (mouseY + Math.round(pitchDiff*10)));
            try {
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return true;
    }


    private ArrayList<BlockPos> getPossibleObstacles(BlockPos target, double[] eyePos) {
        ArrayList<BlockPos> possibleObstacles = new ArrayList<>();
        SpaceLooper spaceLooper = new SpaceLooper(new BlockPos(eyePos), target);
        for (BlockPos block :
                seenBlocks) {
            if (spaceLooper.contains(block)) {
                possibleObstacles.add(block);
            }
        }
        return possibleObstacles;
    }

    private ArrayList<BlockPos> getUnknownNeighbors(BlockPos pos) {
        ArrayList<BlockPos> out = new ArrayList<>();
        out.add(pos.up());
        out.add(pos.down());
        out.add(pos.addX(1));
        out.add(pos.addX(-1));
        out.add(pos.addZ(1));
        out.add(pos.addZ(-1));
        out.removeIf(block -> minedBlocks.contains(block) || seenBlocks.contains(block));
        return out;
    }

    private int getStartDirection(BlockPosWithHeight startPoint) throws HowDidThisHappenException {
        BlockPos testPos = new BlockPos(startPoint);
        testPos = testPos.up(2).addX(1);
        if (minedBlocks.contains(testPos) && !minedBlocks.contains(testPos.down())) return Directions.POSITIVE_X;
        testPos = testPos.addX(-2);
        if (minedBlocks.contains(testPos) && !minedBlocks.contains(testPos.down())) return Directions.NEGATIVE_X;
        testPos = testPos.addX(1).addZ(1);
        if (minedBlocks.contains(testPos) && !minedBlocks.contains(testPos.down())) return Directions.POSITIVE_Z;
        testPos = testPos.addZ(-2);
        if (minedBlocks.contains(testPos) && !minedBlocks.contains(testPos.down())) return Directions.NEGATIVE_Z;
        if (oreBlocks.get(0).getX() > startPoint.getX()) {
            return Directions.POSITIVE_X;
        } else if (oreBlocks.get(0).getX() < startPoint.getX()) {
            return Directions.NEGATIVE_X;
        } else if (oreBlocks.get(0).getZ() > startPoint.getZ()) {
            return Directions.POSITIVE_Z;
        } else if (oreBlocks.get(0).getZ() < startPoint.getZ()) {
            return Directions.NEGATIVE_Z;
        }//if no return at this point the ore should either be above or under the player. the direction that is following the tunnel will be returned by the following code
        testPos = new BlockPos(startPoint);
        testPos = testPos.up(2).addX(1);
        if (minedBlocks.contains(testPos)) return Directions.NEGATIVE_X;
        testPos = testPos.addX(-2);
        if (minedBlocks.contains(testPos)) return Directions.POSITIVE_X;
        testPos = testPos.addX(1).addZ(1);
        if (minedBlocks.contains(testPos)) return Directions.NEGATIVE_Z;
        testPos = testPos.addZ(-2);
        if (minedBlocks.contains(testPos)) return Directions.POSITIVE_Z;

        throw new HowDidThisHappenException("i really have no idea how we got here. something must be wrong with the values that were passed into Vein");
    }

    private boolean moveTo(BlockPosWithHeight target) throws InterruptedException, HowDidThisHappenException {
        ArrayList<BlockPosWithHeight> path = getPath(target);
        if (path == null) {
            return false;
        }
        return Main.follow3dPath(path);
    }

    private ArrayList<BlockPosWithHeight> getPath(BlockPosWithHeight target) throws HowDidThisHappenException, InterruptedException {
        ArrayList<BlockPosWithHeight> path = Main.get3dPath(this.floorPlan, Main.getCurrentBPWH(this.floorPlan, Main.getDebug(Main.getGameScreen())), target);
        return path;
    }

    private BlockPosWithHeight getStartPoint() {
        this.floorPlan = getFloorPlan();
        BlockPosWithHeight startPoint = floorPlan.get(0);
        double distance = 100000;
        for (BlockPosWithHeight block : floorPlan) {
            double distanceNew = Main.distance3D(oreBlocks.get(0).toDoubleArray(), block.toDoubleArray());
            if (distanceNew < distance) {
                distance = distanceNew;
                startPoint = block;
            }
        }
        return startPoint;
    }
}
