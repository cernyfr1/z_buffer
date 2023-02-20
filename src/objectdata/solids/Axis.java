package objectdata.solids;

import transforms.Point3D;

public class Axis extends Solid{
    public Axis(){
        vb.add(new Point3D(0,0,0));
        vb.add(new Point3D(1,0,0));
        vb.add(new Point3D(0,1,0));
        vb.add(new Point3D(0,0,1));

        addIndices(0,1, 0,2, 0,3);
    }

}
