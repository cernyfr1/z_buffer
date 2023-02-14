package rasterops;

import linalg.Lerp;
import objectdata.Vertex;
import rasterdata.ColorRaster;
import rasterdata.Raster;
import transforms.Col;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class Triangler {

    private final Lerp lerp = new Lerp();
    private final Raster<Col> colorRaster;

    public Triangler(Raster<Col> colorRaster) {
        this.colorRaster = colorRaster;
    }

    private List<Vertex> ordered(Vertex v1, Vertex v2, Vertex v3){
        return Stream.of(v1, v2, v3).sorted(new Comparator<Vertex>() {
            @Override
            public int compare(Vertex o1, Vertex o2) {
                if (o1.getPosition().getY() < o2.getPosition().getY()){
                    return -1;}
                else if (o1.getPosition().getY() > o2.getPosition().getY()){
                    return 1;}
                return 0;
            }
        }).toList();
    }

    void drawFirstHalf(Vertex a, Vertex b, Vertex c){  //TODO: implementovat orezani
        final int yMin = (int)a.getPosition().getY();
        final double yMax = b.getPosition().getY();

        for (int y = yMin; y < yMax; y++){
            final double t1 = (y - a.getPosition().getY()) / (c.getPosition().getY() - a.getPosition().getY());
            final double t2 = (y - a.getPosition().getY()) / (b.getPosition().getY() - a.getPosition().getY());
            final Vertex v1 = lerp.compute(a, c, t1);
            final Vertex v2 = lerp.compute(a, b, t2);
            final Vertex vMin = (v1.getPosition().getX() < v2.getPosition().getX())? v1 : v2;
            final Vertex vMax = (vMin == v1)? v2 : v1;
            final int xMin = (int) vMin.getPosition().getX();
            final int xMax = (int) vMax.getPosition().getX();

            for (int x = xMin; x < xMax; x++){
                final double t = (x - xMin) / (xMax - xMin);
                final Vertex v = lerp.compute(vMin, vMax, t);
                colorRaster.setPixel(x, y, v.getColor());
            }
        }
    }

    void drawSecondHalf(Vertex a, Vertex b, Vertex c){
        //TODO
    }

    void draw(Vertex v1, Vertex v2, Vertex v3){
        List<Vertex> ordered = ordered(v1, v2, v3);
        drawFirstHalf(ordered.get(0), ordered.get(1), ordered.get(2));
        drawSecondHalf(ordered.get(0), ordered.get(1), ordered.get(2));

    }


}
