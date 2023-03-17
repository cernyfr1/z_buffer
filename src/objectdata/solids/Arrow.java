package objectdata.solids;

import objectdata.Vertex;
import transforms.Col;
import transforms.Mat4;
import transforms.Mat4Identity;
import transforms.Point3D;

import java.util.List;

public class Arrow implements Solid {

    private final List<Vertex> vertices;
    private final List<Integer> indices;
    private final List<Integer> wireIndices;
    private final List<Part> parts;
    private Mat4 model;

    public Arrow(){
        Col white = new Col(255, 255, 255);
        vertices = List.of(
                new Vertex(new Point3D(0,0,0), white),
                new Vertex(new Point3D(0.8,0,0), white),
                new Vertex(new Point3D(1.0,0,0), white),

                new Vertex(new Point3D(0.8,-0.2,0.2), white),
                new Vertex(new Point3D(0.8,-0.2,-0.2), white),
                new Vertex(new Point3D(0.8,0.2,0.2), white),
                new Vertex(new Point3D(0.8,0.2,-0.2), white)
        );

        indices = List.of(
                0,1,
                1,3,5, 1,5,6, 1,6,4, 1,4,3,
                2,3,5, 2,5,6, 2,6,4, 2,4,3
        );

        wireIndices = List.of(
                0,1,
                1,3,1,4,1,5,1,6,
                3,4,4,6,6,5,5,3,
                2,3,2,4,2,5,2,6
                );

        parts = List.of(
                new Part(Topology.LINE_LIST, 0, 1),
                new Part(Topology.TRIANGLE_LIST, 2, 4),
                new Part(Topology.TRIANGLE_LIST, 14, 4)
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
