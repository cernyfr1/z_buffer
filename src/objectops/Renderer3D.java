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
import java.util.List;

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

                            Vertex v1Final = v1.dehomog().toViewport(rasterWidth, rasterHeight);
                            Vertex v2Final = v2.dehomog().toViewport(rasterWidth, rasterHeight);
                            Vertex v3Final = v3.dehomog().toViewport(rasterWidth, rasterHeight);

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
        //TODO
        //sort vertices by Z
        //if last.z < 0
            //if middle.z < 0
                //return one triangle
            //else return two triangles
        return null;
    }
    public void setProjMatrix(Mat4 projMatrix){
        this.projMatrix = projMatrix;
    }
    public  void setSelectedSolid(NewSolid selectedSolid){
        this.selectedSolid = selectedSolid;
    }
}
