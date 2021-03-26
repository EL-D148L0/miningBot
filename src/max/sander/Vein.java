package max.sander;

import java.util.ArrayList;

public class Vein {
    ArrayList<BlockPos> seenBlocks;// blocks that are mined get removed from this list
    ArrayList<BlockPos> oreBlocks;
    ArrayList<BlockPos> minedBlocks;
    ArrayList<BlockPos> hazardBlockingBlocks;
    ArrayList<BlockPosWithHeight> floorPlan;

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
            if (height > 0) {
                floorPlan.add(new BlockPosWithHeight(block, height));
            }
        }
        return floorPlan;
    }
}
