package objectdata.solids;

import objectdata.Vertex;
import transforms.Col;
import transforms.Mat4;
import transforms.Mat4Identity;
import transforms.Point3D;

import java.util.List;

public class Diamond implements Solid{

    private final List<Vertex> vertices;
    private final List<Integer> indices;
    private final List<Integer> wireIndices;
    private final List<Part> parts;
    private Mat4 model;

    public Diamond() {
        vertices = List.of(
                new Vertex(new Point3D(-0.5,0,-0.5), new Col(255,0,0)),
                new Vertex(new Point3D(0.5,0,-0.5), new Col(0,255,0)),
                new Vertex(new Point3D(0.5,0,0.5), new Col(255,0,0)),
                new Vertex(new Point3D(-0.5,0,0.5), new Col(0,255,0)),

                new Vertex(new Point3D(0,1,0), new Col(255, 255, 0)),
                new Vertex(new Point3D(0,-1,0), new Col(0,0,255))
        );

        indices = List.of(
                0,1,4,1,2,4,2,3,4,3,0,4,
                0,1,5,1,2,5,2,3,5,3,0,5
        );

        wireIndices = List.of(
                0,1,1,2,2,3,3,0,
                0,4,1,4,2,4,3,4,
                0,5,1,5,2,5,3,5);

        parts = List.of(
                new Part(Topology.TRIANGLE_LIST, 0, 4),
                new Part(Topology.TRIANGLE_LIST, 12, 4)
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
    public List<Integer> getWireIndices() {
        return wireIndices;
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
