package rasterops;

import linalg.Lerp;
import objectdata.Vertex;
import rasterdata.ZBuffer;
import transforms.Col;

public class Liner {

    private final Lerp lerp = new Lerp();
    private final ZBuffer zBuffer;

    public Liner(ZBuffer zBuffer){
        this.zBuffer = zBuffer;
    }

    public void draw(Vertex v1, Vertex v2, Col color){

        final double deltaX = Math.abs(v2.getPosition().getX() - v1.getPosition().getX());
        final double deltaY = Math.abs(v2.getPosition().getY() - v1.getPosition().getY());

        if (deltaX > deltaY){
            final Vertex vMin = (v1.getPosition().getX() < v2.getPosition().getX()) ? v1 : v2;
            final Vertex vMax = (vMin == v1) ? v2 : v1;

            final int min = (int)vMin.getPosition().getX();
            final double max = vMax.getPosition().getX();

            for (int x = min; x < max; x++){
                final double t = (x - vMin.getPosition().getX())/(vMax.getPosition().getX() - vMin.getPosition().getX());
                final Vertex v = lerp.compute(vMin, vMax, t);
                zBuffer.setPixel((int)Math.round(v.getPosition().getX()), (int)Math.round(v.getPosition().getY()), v.getPosition().getZ(), (color == null) ? v.getColor() : color);
            }
        }else {

            final Vertex vMin = (v1.getPosition().getY() < v2.getPosition().getY()) ? v1 : v2;
            final Vertex vMax = (vMin == v1) ? v2 : v1;

            final int min = (int) vMin.getPosition().getY();
            final double max = vMax.getPosition().getY();

            for (int y = min; y < max; y++) {
                final double t = (y - vMin.getPosition().getY()) / (vMax.getPosition().getY() - vMin.getPosition().getY());
                final Vertex v = lerp.compute(vMin, vMax, t);
                zBuffer.setPixel((int)Math.round(v.getPosition().getX()), (int)Math.round(v.getPosition().getY()), v.getPosition().getZ(), (color == null) ? v.getColor() : color);
            }
        }
    }
}
