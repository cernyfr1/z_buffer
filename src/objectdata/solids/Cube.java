package objectdata.solids;

import transforms.Point3D;

public class Cube extends Solid {

    public Cube() {
        vb.add(new Point3D(-1, -1, 1));
        vb.add(new Point3D(1, -1, 1));
        vb.add(new Point3D(1, 1, 1));
        vb.add(new Point3D(-1, 1, 1));
        vb.add(new Point3D(-1, -1, -1));
        vb.add(new Point3D(1, -1, -1));
        vb.add(new Point3D(1, 1, -1));
        vb.add(new Point3D(-1, 1, -1));

        addIndices(0, 1, 1, 2, 2, 3, 3, 0);
        addIndices(4, 5, 5, 6, 6, 7, 7, 4);
        addIndices(0, 4, 1, 5, 2, 6, 3, 7);
    }
}
