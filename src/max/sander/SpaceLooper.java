package max.sander;

public class SpaceLooper {
    int smallX;
    int bigX;
    int smallY;
    int bigY;
    int smallZ;
    int bigZ;
    public SpaceLooper(BlockPos start, BlockPos end) {
        if (start.getX() > end.getX()) {
            bigX = (int) Math.floor(start.getX());
            smallX = (int) Math.floor(end.getX());
        } else {
            bigX = (int) Math.floor(end.getX());
            smallX = (int) Math.floor(start.getX());
        }
        if (start.getY() > end.getY()) {
            bigY = (int) Math.floor(start.getY());
            smallY = (int) Math.floor(end.getY());
        } else {
            bigY = (int) Math.floor(end.getY());
            smallY = (int) Math.floor(start.getY());
        }
        if (start.getZ() > end.getZ()) {
            bigZ = (int) Math.floor(start.getZ());
            smallZ = (int) Math.floor(end.getZ());
        } else {
            bigZ = (int) Math.floor(end.getZ());
            smallZ = (int) Math.floor(start.getZ());
        }
        
    }
    public Boolean contains(BlockPos block) {
        return (block.getX() >= this.smallX && block.getX() <= this.bigX && block.getY() >= this.smallY && block.getY() <= this.bigY && block.getZ() >= this.smallZ && block.getZ() <= this.bigZ);
    }
}
