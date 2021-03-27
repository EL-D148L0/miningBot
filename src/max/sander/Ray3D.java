package max.sander;

public class Ray3D {
    private Vector3D directionVector;
    private Vector3D startPointVector;

    public Vector3D getDirectionVector() {
        return directionVector;
    }

    public Vector3D getStartPointVector() {
        return startPointVector;
    }

    public Ray3D(Vector3D directionVector, Vector3D startPointVector) {
        this.directionVector = directionVector.unitVector();
        this.startPointVector = startPointVector;
    }
    public Ray3D(BlockPos startPoint, BlockPos secondPoint) {
        this.startPointVector = new Vector3D(startPoint.toDoubleArray());
        Vector3D dirVector = new Vector3D(secondPoint.toDoubleArray()).minus(startPointVector);
        this.directionVector = dirVector.unitVector();
    }
}
