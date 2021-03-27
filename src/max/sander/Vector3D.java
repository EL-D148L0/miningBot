package max.sander;

public class Vector3D {
    private double x, y, z;

    Vector3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    Vector3D(double[] in) {
        this.x = in[0];
        this.y = in[1];
        this.z = in[2];
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

    double[] toDoubleArray() {
        return new double[] {this.x, this.y, this.z};
    }

    Vector3D plus(Vector3D v) {
        return new Vector3D(x + v.x, y + v.y, z + v.z);
    }

    Vector3D minus(Vector3D v) {
        return new Vector3D(x - v.x, y - v.y, z - v.z);
    }

    Vector3D times(double s) {
        return new Vector3D(s * x, s * y, s * z);
    }

    double dot(Vector3D v) {
        return x * v.x + y * v.y + z * v.z;
    }

    double length() {
        return Math.sqrt(x*x + y*y + z*z);
    }

    Vector3D unitVector() {
        double length = this.length();
        return new Vector3D(x/length, y/length, z/length);
    }
    
    static Vector3D intersectPointRay(Vector3D rayVector, Vector3D rayPoint, Vector3D planeNormal, Vector3D planePoint) {
        Vector3D diff = rayPoint.minus(planePoint);
        double prod1 = diff.dot(planeNormal);
        double prod2 = rayVector.dot(planeNormal);
        double prod3 = prod1 / prod2;
        //System.out.println(prod3);
        if (prod3 > 0) return null;
        if (!Double.isFinite(prod3)) return  null;
        return rayPoint.minus(rayVector.times(prod3));
    }
    static  Vector3D intersectPointRay(Ray3D ray, Plane3D plane) {
        return intersectPointRay(ray.getDirectionVector(), ray.getStartPointVector(), plane.getNormalVector(), plane.getStartPointVector());
    }

    @Override
    public String toString() {
        return String.format("(%f, %f, %f)", x, y, z);
    }
}

