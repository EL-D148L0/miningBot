package max.sander;

import java.util.Objects;

public class BlockPosWithHeight {

    double x;
    double y;
    double z;
    int height;
    public BlockPosWithHeight(double x, double y, double z, int height) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.height = height;
    }
    public BlockPosWithHeight(double[] pos, int height) {
        this.x = pos[0];
        this.y = pos[1];
        this.z = pos[2];
        this.height = height;
    }
    public BlockPosWithHeight(BlockPosWithDirection pos, int height) {
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
        this.height = height;
    }
    public BlockPosWithHeight(BlockPos pos, int height) {
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
        this.height = height;
    }
    public BlockPosWithHeight up(double n) {
        return new BlockPosWithHeight(x, y + n, z, height);
    }
    public BlockPosWithHeight down(double n) {
        return new BlockPosWithHeight(x, y - n, z, height);
    }

    public BlockPosWithHeight up() {
        return up(1);
    }
    public BlockPosWithHeight down() {
        return down(1);
    }
    public BlockPosWithHeight upHeight() {
        return new BlockPosWithHeight(x, y + 1, z, height - 1);
    }
    public BlockPosWithHeight downHeight() {
        return new BlockPosWithHeight(x, y - 1, z, height + 1);
    }

    public BlockPosWithHeight addX(double n) {
        return new BlockPosWithHeight(x + n, y, z, height);
    }
    public BlockPosWithHeight addY(double n) {
        return new BlockPosWithHeight(x, y + n, z, height);
    }
    public BlockPosWithHeight addZ(double n) {
        return new BlockPosWithHeight(x, y, z + n, height);
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

    public int getHeight() {
        return height;
    }

    public boolean equalsDoubleArray(double[] in) {
        if (in == null) return false;
        return in[0] == x && in[1] == y && in[2] == z;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BlockPosWithHeight that = (BlockPosWithHeight) o;
        return Double.compare(that.getX(), getX()) == 0 && Double.compare(that.getY(), getY()) == 0 && Double.compare(that.getZ(), getZ()) == 0 && getHeight() == that.getHeight();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getX(), getY(), getZ(), getHeight());
    }

    @Override
    public String toString() {
        return "BlockPosWithHeight{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", height=" + height +
                '}';
    }
}
