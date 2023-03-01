package rasterops;

import linalg.Lerp;
import objectdata.Vertex;
import rasterdata.ZBuffer;

public class Liner {

    private final Lerp lerp = new Lerp();
    private final ZBuffer zBuffer;

    public Liner(ZBuffer zBuffer){
        this.zBuffer = zBuffer;
    }

    public void draw(Vertex v1, Vertex v2){

        final Vertex vMin = (v1.getPosition().getY() < v2.getPosition().getY()) ? v1 : v2;
        final Vertex vMax = (vMin == v1) ? v2 : v1;

        final int min = (int)vMin.getPosition().getY();
        final double max = vMax.getPosition().getY();

        for (int y = min; y < max; y++){
            final double t = (y - vMin.getPosition().getY())/(vMax.getPosition().getY() - vMin.getPosition().getY());
            final Vertex v = lerp.compute(vMin, vMax, t);
            zBuffer.setPixel((int)v.getPosition().getX(), (int)v.getPosition().getY(), v.getPosition().getZ(), v.getColor());
        }


        //TODO: main axis fix
    }
}
