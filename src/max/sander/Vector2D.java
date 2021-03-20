package max.sander;

public class Vector2D {
    private double x, y;

    Vector2D(double x, double y) {
        this.x = x;
        this.y = y;
    }
    Vector2D(double[] a, double[] b) {
        this.x = b[0] - a[0];
        this.y = b[1] - a[1];
    }
    Vector2D(double[] in) {
        this.x = in[0];
        this.y = in[1];
    }
    double[] toDoubleArray() {
        return new double[] {this.x, this.y};
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    Vector2D plus(Vector2D v) {
        return new Vector2D(x + v.x, y + v.y);
    }

    Vector2D minus(Vector2D v) {
        return new Vector2D(x - v.x, y - v.y);
    }

    Vector2D times(double s) {
        return new Vector2D(s * x, s * y);
    }
    double length() {
        return Math.sqrt(x*x + y*y);
    }
    Vector2D unitVector() {
        double length = this.length();
        return new Vector2D(x/length, y/length);
    }

    double dot(Vector2D v) {
        return x * v.x + y * v.y;
    }


    @Override
    public String toString() {
        return String.format("(%f, %f)", x, y);
    }
}
