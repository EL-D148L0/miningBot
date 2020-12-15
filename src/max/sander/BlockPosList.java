package max.sander;

import java.util.ArrayList;
import java.util.Arrays;

public class BlockPosList {
    ArrayList<double[]> blocks = new ArrayList<double[]>();
    public BlockPosList() {
    }
    public void addBlockPos(double[] blockPos) {
        blocks.add(blockPos);
    }
    public void addBlockPos(BlockPosWithDirection blockPos) {
        blocks.add(blockPos.toDoubleArray());
    }
    public void removeBlockPos(BlockPosWithDirection blockPos) {
        this.removeBlockPos(blockPos.toDoubleArray());
    }
    public void removeBlockPos(double[] blockPos) {
        blocks.removeIf(block -> Arrays.equals(block, blockPos));
    }
    public boolean contains(double[] blockPos) {
        boolean isThere = false;
        for (double[] block: blocks) {
            if (Arrays.equals(block, blockPos)) {
                isThere = true;
                break;
            }
        }
        return isThere;
    }

    public boolean contains(BlockPosWithDirection blockPos) {
        return this.contains(blockPos.toDoubleArray());
    }

}
