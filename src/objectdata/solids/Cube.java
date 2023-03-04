package objectdata.solids;

import objectdata.Vertex;
import transforms.Col;
import transforms.Mat4;
import transforms.Mat4Identity;
import transforms.Point3D;

import java.util.List;

public class Cube implements Solid{

    private final List<Vertex> vertices;
    private final List<Integer> indices;
    private final List<Part> parts;
    private Mat4 model;

    public Cube() {
        vertices = List.of(
                new Vertex(new Point3D(-0.5,0.5,-0.5), new Col(255,255,255)),
                new Vertex(new Point3D(0.5,0.5,-0.5), new Col(255,255,255)),
                new Vertex(new Point3D(0.5,0.5,0.5), new Col(255,0,0)),
                new Vertex(new Point3D(-0.5,0.5,0.5), new Col(255,0,0)),

                new Vertex(new Point3D(-0.5,-0.5,-0.5), new Col(255,0,0)),
                new Vertex(new Point3D(0.5,-0.5,-0.5), new Col(255,0,0)),
                new Vertex(new Point3D(0.5,-0.5,0.5), new Col(255,0,0)),
                new Vertex(new Point3D(-0.5,-0.5,0.5), new Col(255,0,0))
        );

        indices = List.of(
                0,4,5, 0,5,1,
                1,5,6, 1,6,2,
                2,6,7, 2,7,3,
                3,7,4, 3,4,0,
                0,1,3, 3,1,2,
                4,5,7, 7,5,6
        );

        parts = List.of(
                new Part(Topology.TRIANGLE_LIST, 0, 12)
        );

        model = new Mat4Identity();
    }

    @Override
    public List<Vertex> getVertices() {
        return vertices;
    }

    @Override
    public List<Integer> getIndices() {
        return indices;
    }

    @Override
    public List<Part> getParts() {
        return parts;
    }

    @Override
    public Mat4 getModel() {
        return model;
    }

    @Override
    public void setModel(Mat4 model) {
        this.model = model;
    }
}
