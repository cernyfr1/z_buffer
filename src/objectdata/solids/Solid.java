package objectdata.solids;

import objectdata.Vertex;
import transforms.Mat4;

import java.util.List;

public interface Solid {

    List<Vertex> getVertices();
    List<Integer> getIndices();
    List<Part> getParts();
    Mat4 getModel();
    void setModel(Mat4 model);
}
