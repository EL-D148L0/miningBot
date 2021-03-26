package max.sander;

import java.util.ArrayList;

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
        return true;
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
