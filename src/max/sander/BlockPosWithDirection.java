package max.sander;

public class BlockPosWithDirection {
    double x;
    double y;
    double z;
    int direction; //  0: positive X 1: negative X 2: positive Z 3: negative Z
    public BlockPosWithDirection(String debug) {
        double[] playerPos = Main.getPlayerPosFloored(debug);
        this.x = playerPos[0];
        this.y = playerPos[1];
        this.z = playerPos[2];
        if (debug.contains("positive X")) {
            direction = 0; //todo konstanten einführen
        } else if (debug.contains("negative X")) {
            direction = 1;
        } else if (debug.contains("positive Z")) {
            direction = 2;
        } else if (debug.contains("negative Z")) {
            direction = 3;
        }
    }
    public BlockPosWithDirection(double x, double y, double z, int dir) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.direction = dir;
    }

    public BlockPosWithDirection(double[] pos, int dir) {
        this.x = pos[0];
        this.y = pos[1];
        this.z = pos[2];
        this.direction = dir;
    }
    public BlockPosWithDirection up() {
        return up(1);
    }
    public BlockPosWithDirection down() {
        return down(1);
    }
    public BlockPosWithDirection left() {
        return left(1);
    }
    public BlockPosWithDirection right() {
        return right(1);
    }
    public BlockPosWithDirection forward() {
        return forward(1);
    }
    public BlockPosWithDirection backward() {
        return backward(1);
    }
    public BlockPosWithDirection up(double n) {
        return new BlockPosWithDirection(x, y + n, z, direction);
    }
    public BlockPosWithDirection down(double n) {
        return new BlockPosWithDirection(x, y - n, z, direction);
    }
    public BlockPosWithDirection left(double n) {
        if (direction == 0) {
            return new BlockPosWithDirection(x, y, z - n, direction);
        } else if (direction == 1) {
            return new BlockPosWithDirection(x, y, z + n, direction);
        } else if (direction == 2) {
            return new BlockPosWithDirection(x + n, y, z, direction);
        } else if (direction == 3) {
            return new BlockPosWithDirection(x - n, y, z, direction);
        } else return this;
    }
    public BlockPosWithDirection right(double n) {
        if (direction == 0) {
            return new BlockPosWithDirection(x, y, z + n, direction);
        } else if (direction == 1) {
            return new BlockPosWithDirection(x, y, z - n, direction);
        } else if (direction == 2) {
            return new BlockPosWithDirection(x - n, y, z, direction);
        } else if (direction == 3) {
            return new BlockPosWithDirection(x + n, y, z, direction);
        } else return this;
    }
    public BlockPosWithDirection forward(double n) {
        if (direction == 0) {
            return new BlockPosWithDirection(x + n, y, z, direction);
        } else if (direction == 1) {
            return new BlockPosWithDirection(x - n, y, z, direction);
        } else if (direction == 2) {
            return new BlockPosWithDirection(x, y, z + n, direction);
        } else if (direction == 3) {
            return new BlockPosWithDirection(x, y, z - n, direction);
        } else return this;
    }
    public BlockPosWithDirection backward(double n) {
        if (direction == 0) {
            return new BlockPosWithDirection(x - n, y, z, direction);
        } else if (direction == 1) {
            return new BlockPosWithDirection(x + n, y, z, direction);
        } else if (direction == 2) {
            return new BlockPosWithDirection(x, y, z - n, direction);
        } else if (direction == 3) {
            return new BlockPosWithDirection(x, y, z + n, direction);
        } else return this;
    }

    public BlockPosWithDirection addX(double n) {
        return new BlockPosWithDirection(x + n, y, z, direction);
    }
    public BlockPosWithDirection addY(double n) {
        return new BlockPosWithDirection(x, y + n, z, direction);
    }
    public BlockPosWithDirection addZ(double n) {
        return new BlockPosWithDirection(x, y, z + n, direction);
    }


    @Override
    public String toString() {
        return "BlockPosWithDirection{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", direction=" + direction +
                '}';
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
    public double[] toDoubleArray() {
        return new double[]{x, y, z};
    }

    public int getDirection() {
        return direction;
    }

    public boolean equalsDoubleArray(double[] in) {
        if (in == null) return false;
        return in[0] == x && in[1] == y && in[2] == z;
    }



}
