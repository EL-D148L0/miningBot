package max.sander;

import java.util.Objects;

public class BlockPos {
    double x;
    double y;
    double z;
    public BlockPos(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    public BlockPos(double[] pos) {
        this.x = pos[0];
        this.y = pos[1];
        this.z = pos[2];
    }
    public BlockPos(BlockPosWithDirection pos) {
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
    }
    public BlockPos(BlockPosWithHeight pos) {
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
    }
    public BlockPos up(double n) {
        return new BlockPos(x, y + n, z);
    }
    public BlockPos down(double n) {
        return new BlockPos(x, y - n, z);
    }

    public BlockPos up() {
        return up(1);
    }
    public BlockPos down() {
        return down(1);
    }
    public BlockPos move(int direction) {
        if (direction == Directions.POSITIVE_X) {
            return addX(1);
        } else if (direction == Directions.NEGATIVE_X) {
            return addX(-1);
        } else if (direction == Directions.POSITIVE_Z) {
            return addZ(1);
        } else if (direction == Directions.NEGATIVE_Z) {
            return addZ(-1);
        } else return this;
    }

    public BlockPos addX(double n) {
        return new BlockPos(x + n, y, z);
    }
    public BlockPos addY(double n) {
        return new BlockPos(x, y + n, z);
    }
    public BlockPos addZ(double n) {
        return new BlockPos(x, y, z + n);
    }

    public double[] toDoubleArray() {
        return new double[]{x, y, z};
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public  boolean equalsBPWH(BlockPosWithHeight in) {
        return this.equals(new BlockPos(in));
    }
    public  boolean equalsBPWD(BlockPosWithDirection in) {
        return this.equals(new BlockPos(in));
    }




    public boolean equalsDoubleArray(double[] in) {
        if (in == null) return false;
        return in[0] == x && in[1] == y && in[2] == z;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlockPos that = (BlockPos) o;
        return Double.compare(that.getX(), getX()) == 0 && Double.compare(that.getY(), getY()) == 0 && Double.compare(that.getZ(), getZ()) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getX(), getY(), getZ());
    }

    @Override
    public String toString() {
        return "BlockPos{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
