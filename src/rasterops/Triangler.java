package rasterops;

import linalg.Lerp;
import objectdata.Vertex;
import rasterdata.ZBuffer;
import transforms.Col;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class Triangler {

    private final Lerp lerp = new Lerp();
    private final ZBuffer zBuffer;

    public Triangler(ZBuffer zBuffer) {
        this.zBuffer = zBuffer;
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

    void drawFirstHalf(Vertex a, Vertex b, Vertex c, Col color){  //TODO: implementovat orezani
        final int yMin = (int)a.getPosition().getY();
        final double yMax = b.getPosition().getY();

        for (int y = yMin; y < yMax; y++) {
            final double t1 = (y - a.getPosition().getY()) / (c.getPosition().getY() - a.getPosition().getY());
            final double t2 = (y - a.getPosition().getY()) / (b.getPosition().getY() - a.getPosition().getY());
            if (t1 > 0 && t2 > 0) {
                final Vertex v1 = lerp.compute(a, c, t1);
                final Vertex v2 = lerp.compute(a, b, t2);
                final Vertex vMin = (v1.getPosition().getX() < v2.getPosition().getX()) ? v1 : v2;
                final Vertex vMax = (vMin == v1) ? v2 : v1;
                final int xMin = (int) vMin.getPosition().getX();
                final double xMax = vMax.getPosition().getX();

                for (int x = xMin; x < xMax; x++) {
                    final double t = (x - xMin) / (xMax - xMin);
                    final Vertex v = lerp.compute(vMin, vMax, t);

                    zBuffer.setPixel(x, y, v.getPosition().getZ(), (color == null) ? v.getColor() : color);
                }
            }
        }
    }

    void drawSecondHalf(Vertex a, Vertex b, Vertex c, Col color){
        final int yMin = (int)b.getPosition().getY();
        final double yMax = c.getPosition().getY();
        for (int y = yMin; y < yMax; y++) {
            final double t1 = (y - a.getPosition().getY()) / (c.getPosition().getY() - a.getPosition().getY());
            final double t2 = (y - b.getPosition().getY()) / (c.getPosition().getY() - b.getPosition().getY());
            if (t1 > 0 && t2 > 0) {
                final Vertex v1 = lerp.compute(a, c, t1);
                final Vertex v2 = lerp.compute(b, c, t2);
                final Vertex vMin = (v1.getPosition().getX() < v2.getPosition().getX()) ? v1 : v2;
                final Vertex vMax = (vMin == v1) ? v2 : v1;
                final int xMin = (int) vMin.getPosition().getX();
                final double xMax = vMax.getPosition().getX();

                for (int x = xMin; x < xMax; x++) {
                    final double t = (x - xMin) / (xMax - xMin);
                    final Vertex v = lerp.compute(vMin, vMax, t);
                    zBuffer.setPixel(x, y, v.getPosition().getZ(), (color == null) ? v.getColor() : color);
                }
            }
        }
    }

    public void draw(Vertex v1, Vertex v2, Vertex v3, Col color){
        List<Vertex> ordered = ordered(v1, v2, v3);
        drawFirstHalf(ordered.get(0), ordered.get(1), ordered.get(2), color);
        drawSecondHalf(ordered.get(0), ordered.get(1), ordered.get(2), color);

    }


}
