package objectdata.solids;

import transforms.Mat4;
import transforms.Mat4Identity;
import transforms.Point3D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class Solid {
    protected List<Point3D> vb = new ArrayList<>();
    protected List<Integer> ib = new ArrayList<>();
    private Mat4 model = new Mat4Identity();

    public List<Point3D> getVb() {
        return vb;
    }

    public List<Integer> getIb() {
        return ib;
    }

    protected void addIndices(Integer... indices) {
        ib.addAll(Arrays.asList(indices));
    }

    public void setModel(Mat4 model) {
        this.model = model;
    }

    public Mat4 getModel() {
        return model;
    }
}
