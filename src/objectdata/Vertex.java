package objectdata;

import linalg.Vectorizable;
import transforms.*;
import transforms.Point3D;

public class Vertex implements Vectorizable<Vertex>, Transformable<Vertex> {

    private final Point3D position;
    private final Col color;

    public Vertex(Point3D position, Col color){
        this.position = position;
        this.color = color;
    }

    @Override
    public Vertex mul(double t) {
        return new Vertex(
                position.mul(t),
                color.mul(t)
        );
    }

    @Override
    public Vertex dehomog() {
        return new Vertex(
                this.getPosition().mul(1/this.getPosition().getW()),
                this.getColor());
    }

    @Override
    public Vertex toViewport(int width, int height) {
        Point3D p = new Point3D(this.getPosition().dehomog().get()
                .mul(new Vec3D(1, -1, 1))
                .add(new Vec3D(1, 1, 0))
                .mul(new Vec3D((width-1)/2., (height-1)/2., 1)));
        return new Vertex(p, this.getColor());
    }

    @Override
    public Vertex add(Vertex other) {
        return new Vertex(
                position.add(other.position),
                color.add(other.color)
        );
    }

    public Point3D getPosition() {
        return position;
    }

    public Col getColor() {
        return color;
    }

    @Override
    public Vertex transformed(Mat4 transformation) {
        return new Vertex(this.getPosition().mul(transformation),this.getColor());
    }

    @Override
    public String toString() {
        return this.getPosition().getX() + " : " +
                this.getPosition().getY() + " : " +
                this.getPosition().getZ() + " : " +
                this.getPosition().getW() + " : " +
                this.getColor().toString();
    }
}
