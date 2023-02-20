package rasterops;

//import model.Line;
import objectdata.Vertex;
import objectdata.solids.Axis;
//import raster.LineRasterizer;
import objectdata.solids.Solid;
import transforms.Col;
import transforms.Mat4;
import transforms.Point3D;
import transforms.Vec3D;

import java.util.List;

public class Renderer {

    private final int rasterWidth, rasterHeight;
//    private final LineRasterizer lineRasterizer;

    private final Triangler triangler;
    private Solid selectedSolid;
    private Mat4 proj;
    private Mat4 view;

    public Renderer(Triangler triangler,Mat4 view, Mat4 proj, int rasterWidth, int rasterHeight) {
//        this.lineRasterizer = lineRasterizer;
        this.triangler = triangler;
        this.view = view;
        this.proj = proj;
        this.rasterWidth = rasterWidth;
        this.rasterHeight = rasterHeight;
    }

    public void renderSolid(Solid solid){

        //MVP

        Mat4 MVP = solid.getModel().mul(view).mul(proj);

        for (int i = 0; i < solid.getIb().size(); i += 3) {
            int index1 = solid.getIb().get(i);
            int index2 = solid.getIb().get(i+1);
            int index3 = solid.getIb().get(i+2);

            Col color = null;

            switch (i){
                case 0: color = new Col(0xff0000); break;
                case 3: color = new Col(0x00ff00); break;
                case 6: color = new Col(0x0000ff); break;
                case 9: color = new Col(0x00ffff); break;
            }

            Point3D vertex1 = solid.getVb().get(index1);
            Point3D vertex2 = solid.getVb().get(index2);
            Point3D vertex3 = solid.getVb().get(index3);

            vertex1 = vertex1.mul(MVP);
            vertex2 = vertex2.mul(MVP);
            vertex3 = vertex3.mul(MVP);


            if (-vertex1.getW() <= vertex1.getX() && vertex1.getX() <= vertex1.getW()){
                if (-vertex1.getW() <= vertex1.getY() && vertex1.getY() <= vertex1.getW()){
                    if (0 <= vertex1.getZ() && vertex1.getZ() <= vertex1.getW()){
                        if (-vertex2.getW() <= vertex2.getX() && vertex2.getX() <= vertex2.getW()) {
                            if (-vertex2.getW() <= vertex2.getY() && vertex2.getY() <= vertex2.getW()) {
                                if (0 <= vertex2.getZ() && vertex2.getZ() <= vertex2.getW()) {
                                    if (-vertex2.getW() <= vertex2.getX() && vertex2.getX() <= vertex2.getW()) {
                                        if (-vertex2.getW() <= vertex2.getY() && vertex2.getY() <= vertex2.getW()) {
                                            if (0 <= vertex2.getZ() && vertex2.getZ() <= vertex2.getW()) {

                                                Point3D v1Dehomog = vertex1.mul(1 / vertex1.getW());
                                                Point3D v2Dehomog = vertex2.mul(1 / vertex2.getW());
                                                Point3D v3Dehomog = vertex3.mul(1 / vertex3.getW());

                                                Vertex v1 = new Vertex(transformToWindow(new Vec3D(v1Dehomog)), color);
                                                Vertex v2 = new Vertex(transformToWindow(new Vec3D(v2Dehomog)), color);
                                                Vertex v3 = new Vertex(transformToWindow(new Vec3D(v3Dehomog)), color);

                                                triangler.draw(v1, v2, v3);



                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

/*
    public void renderAxis(Boolean x, Boolean y, Boolean z) {

        Axis axis = new Axis();
        Mat4 MVP = axis.getModel().mul(view).mul(proj);

        Point3D vertex0 = axis.getVb().get(0);
        vertex0 = vertex0.mul(MVP);
        Point3D v0Dehomog = vertex0.mul(1 / vertex0.getW());
        Vec3D v0 = transformToWindow(new Vec3D(v0Dehomog));


        if (x) {
            Point3D vertexX = axis.getVb().get(1);
            vertexX = vertexX.mul(MVP);
            Point3D vXDehomog = vertexX.mul(1 / vertexX.getW());
            Vec3D vX = transformToWindow(new Vec3D(vXDehomog));
            lineRasterizer.rasterize(new Line((int) Math.round(v0.getX()), (int) Math.round(v0.getY()), (int) Math.round(vX.getX()), (int) Math.round(vX.getY())), 0xff0000);
        }

        if (y) {
            Point3D vertexY = axis.getVb().get(2);
            vertexY = vertexY.mul(MVP);
            Point3D vYDehomog = vertexY.mul(1 / vertexY.getW());
            Vec3D vY = transformToWindow(new Vec3D(vYDehomog));
            lineRasterizer.rasterize(new Line((int) Math.round(v0.getX()), (int) Math.round(v0.getY()), (int) Math.round(vY.getX()), (int) Math.round(vY.getY())), 0x00ff00);
        }

        if (z) {
            Point3D vertexZ = axis.getVb().get(3);
            vertexZ = vertexZ.mul(MVP);
            Point3D vZDehomog = vertexZ.mul(1 / vertexZ.getW());
            Vec3D vZ = transformToWindow(new Vec3D(vZDehomog));
            lineRasterizer.rasterize(new Line((int) Math.round(v0.getX()), (int) Math.round(v0.getY()), (int) Math.round(vZ.getX()), (int) Math.round(vZ.getY())), 0x0000ff);
        }

    }

 */


    public void renderScene(List<Solid> solids){
        for (Solid solid: solids) {
            renderSolid(solid);
        }
    }

    private Vec3D transformToWindow(Vec3D v){
        return v.mul(new Vec3D(1, -1, 1))
                .add(new Vec3D(1, 1, 0))
                .mul(new Vec3D((rasterWidth-1)/2., (rasterHeight-1)/2., 1));
    }

    public void setView(Mat4 mat){
        this.view = mat;
    }

    public void setProj(Mat4 proj) {
        this.proj = proj;
    }
    public void setSelectedSolid(Solid selectedSolid){
        this.selectedSolid = selectedSolid;
    }
}
