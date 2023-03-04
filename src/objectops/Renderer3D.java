package objectops;

import linalg.Lerp;
import objectdata.Vertex;
import objectdata.solids.NewSolid;
import objectdata.solids.Part;
import rasterdata.ZBuffer;
import rasterops.Liner;
import rasterops.Triangler;
import transforms.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class Renderer3D {

    private int rasterWidth;
    private int rasterHeight;
    private final Triangler triangler;
    private final Lerp lerp = new Lerp();
    private final Liner liner;
    private Mat4 projMatrix;
    private NewSolid selectedSolid;

    public Renderer3D(ZBuffer zBuffer, Mat4 projMatrix, NewSolid selectedSolid){
        this.selectedSolid = selectedSolid;
        this.projMatrix = projMatrix;
        this.rasterWidth = zBuffer.getWidth();
        this.rasterHeight = zBuffer.getHeight();
        triangler = new Triangler(zBuffer);
        liner = new Liner(zBuffer);

    }

    public void renderScene(List<NewSolid> solids, Mat4 viewMatrix){
        final Mat4 VP = viewMatrix.mul(projMatrix);
        for (NewSolid s : solids) {
            drawSolid(s, s.getModel().mul(VP));
        }

    }

    private void drawSolid(NewSolid solid, Mat4 transformation){
        final List<Vertex> vertices = new ArrayList<>();
        for (int i = 0; i < solid.getVertices().size(); i++){
            vertices.add(solid.getVertices().get(i).transformed(transformation));
        }
        final List<Integer> indices = solid.getIndices();

        for (Part part : solid.getParts()){
            switch (part.getTopology()){
                case LINE_LIST -> {
                    for (int i = part.getOffset(); i < part.getOffset() + 2 * part.getCount(); i += 2) {
                        final Vertex v1 = vertices.get(indices.get(i));
                        final Vertex v2 = vertices.get(indices.get(i+1));

                        if (!isOutOfView(List.of(v1,v2))){
                            final List<Vertex> line = clipZ(v1, v2);
                            liner.draw(
                                    line.get(0).dehomog().toViewport(rasterWidth,rasterHeight),
                                    line.get(1).dehomog().toViewport(rasterWidth, rasterHeight)
                            );



                        }
                    }
                }
                case TRIANGLE_LIST -> {
                    for (int i = part.getOffset(); i < part.getOffset() + 3*part.getCount(); i += 3) {
                        final Vertex v1 = vertices.get(indices.get(i));
                        final Vertex v2 = vertices.get(indices.get(i + 1));
                        final Vertex v3 = vertices.get(indices.get(i + 2));
                        if (!isOutOfView(List.of(v1, v2, v3))) {
                            //clipZ
                            final List<Vertex> triangles = clipZ(v1, v2, v3);

                            Vertex v1Final = triangles.get(0).dehomog().toViewport(rasterWidth, rasterHeight);
                            Vertex v2Final = triangles.get(1).dehomog().toViewport(rasterWidth, rasterHeight);
                            Vertex v3Final = triangles.get(2).dehomog().toViewport(rasterWidth, rasterHeight);

                            if (triangles.size() == 4){
                                Vertex v1BFinal = triangles.get(3).dehomog().toViewport(rasterWidth, rasterHeight);
                                triangler.draw(v1BFinal, v2Final, v3Final);
                            }
                            triangler.draw(v1Final, v2Final, v3Final);
                        }
                    }
                }
            }

        }
    }

    private boolean isOutOfView(List<Vertex> vertices){


        final boolean isTooLeft = vertices.stream().allMatch(v -> v.getPosition().getX() >= -v.getPosition().getW());
        final boolean isTooRight = vertices.stream().allMatch(v -> v.getPosition().getX() <= v.getPosition().getW());
        final boolean isTooUp = vertices.stream().allMatch(v -> v.getPosition().getY() >= -v.getPosition().getW());
        final boolean isTooDown = vertices.stream().allMatch(v -> v.getPosition().getY() <= v.getPosition().getW());
        final boolean isTooClose = vertices.stream().allMatch(v -> v.getPosition().getZ() >= 0);
        final boolean isTooFar =vertices.stream().allMatch(v -> v.getPosition().getZ() <= v.getPosition().getW());

        boolean[] booleans = new boolean[]{isTooLeft, isTooRight, isTooUp, isTooDown, isTooClose, isTooFar};
        for (boolean b : booleans) if (!b) return true;
        return false;
    }


    private List<Vertex> clipZ(Vertex v1, Vertex v2){
        final Vertex min = (v1.getPosition().getZ() < v2.getPosition().getZ()) ? v1 : v2;
        final Vertex max = (min == v1) ? v2 : v1;
        final double range = max.getPosition().getZ() - min.getPosition().getZ();
        if (min.getPosition().getZ() < 0) {

            final double t = (0 - min.getPosition().getZ()) / range;
            final Vertex P = lerp.compute(min, max, t);
            return List.of(P, max);
        }
        return List.of(min, max);
    }

    private List<Vertex> clipZ(Vertex v1, Vertex v2, Vertex v3){
        //sort vertices by Z
        List<Vertex> ordered = Stream.of(v1, v2, v3).sorted(new Comparator<Vertex>() {
            @Override
            public int compare(Vertex o1, Vertex o2) {
                if (o1.getPosition().getZ() < o2.getPosition().getZ()){
                    return -1;}
                else if (o1.getPosition().getZ() > o2.getPosition().getZ()){
                    return 1;}
                return 0;
            }
        }).toList();
        if (ordered.get(1).getPosition().getZ() >= 0){ //je prostredni uvnitr?
            if (ordered.get(0).getPosition().getZ() >= 0){ //je prvni uvnitr?
                return ordered;
            }
            else { // return dva trojuhelniky
                final double t1 = (0 - v1.getPosition().getZ()) / (v2.getPosition().getZ() - v1.getPosition().getZ());
                final double t2 = (0 - v1.getPosition().getY()) / (v3.getPosition().getZ() - v1.getPosition().getZ());
                final Vertex v1A = lerp.compute(v1, v2, t1);
                final Vertex v1B = lerp.compute(v1, v3, t2);
                return List.of(v1A, v2, v3, v1B);
            }
        }
        else { //return oriznuty trojuhelnik
            final double t1 = (0 - v2.getPosition().getZ()) / (v3.getPosition().getZ() - v2.getPosition().getZ());
            final double t2 = (0 - v1.getPosition().getY()) / (v3.getPosition().getZ() - v1.getPosition().getZ());
            final Vertex v2New = lerp.compute(v2, v3, t1);
            final Vertex v1New = lerp.compute(v1, v3, t2);
            return List.of(v1New, v2New, v3);
        }
    }
    public void setProjMatrix(Mat4 projMatrix){
        this.projMatrix = projMatrix;
    }
    public  void setSelectedSolid(NewSolid selectedSolid){
        this.selectedSolid = selectedSolid;
    }
}
