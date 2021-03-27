package max.sander;

public class Plane3D {
    private Vector3D normalVector;
    private Vector3D startPointVector;

    public Vector3D getNormalVector() {
        return normalVector;
    }

    public Vector3D getStartPointVector() {
        return startPointVector;
    }

    public Plane3D(Vector3D startPointVector, Vector3D normalVector) {
        this.normalVector = normalVector.unitVector();
        this.startPointVector = startPointVector;
    }
}
