

import objectdata.solids.Cube;
import objectdata.solids.Pyramid;
import objectdata.solids.Solid;
import rasterdata.ColorRaster;
import rasterdata.ZBuffer;
import rasterops.Renderer;
import rasterops.Triangler;
import transforms.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;


public class Canvas3D {

    private final JFrame frame;
    private final JPanel panel;
    private final ColorRaster raster;
    private final Col backgroundColor = new Col(0x000000);
//    private final LineRasterizer lineRasterizer;
//    private final WireRenderer wireRenderer;
    private final Renderer wireRenderer;
    private final Triangler triangler;
    private final ZBuffer zBuffer;
    private List<Solid> solids;
    private Solid selectedSolid;
    private final Cube cube;
    private final Pyramid pyramid;
//    private final SinWave sinWave;
    private Mat4 prespProj;
    private Mat4 orthProj;
    private Camera camera;
    private double camerspeed = 0.3d;
    private int cameraX, cameraY, rotationX, rotationY, translationX, translationY;
    private Boolean runAnimation;
    private int editMode = 0;

    public Canvas3D(int width, int height) {

        frame = new JFrame();

        frame.setLayout(new BorderLayout());
        frame.setTitle("PGRF1");
        frame.setResizable(false);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);



        raster = new ColorRaster(width, height, backgroundColor);
        zBuffer = new ZBuffer(raster);
//        lineRasterizer = new GraphicsLineRasterizer(raster);

        solids = new ArrayList<>();
        cube = new Cube();
        pyramid = new Pyramid();
//        pyramid.setModel(pyramid.getModel().mul(new Mat4Transl(4, 0,0)));
        selectedSolid = pyramid;
//        sinWave = new SinWave();
//        sinWave.setModel(sinWave.getModel().mul(new Mat4Transl(8,0,0)));
//        solids.add(cube);
        solids.add(pyramid);
//        solids.add(triangle);
//        solids.add(sinWave);
        runAnimation = false;

        prespProj = new Mat4PerspRH(Math.toRadians(60), height/(double)width, 0.1, 50);
        orthProj = new Mat4OrthoRH(10, 10, 0.1, 50);

        camera = new Camera(new Vec3D(4,-5,1),
                Math.toRadians(90),   //azimuth
                Math.toRadians(0),   //zenith
                10,
                false);



        triangler = new Triangler(zBuffer);
        wireRenderer = new Renderer(triangler, camera.getViewMatrix(), prespProj, width, height);
//        wireRenderer.setSelectedSolid(selectedSolid);

        Runnable animation = () ->{
            try {
                while (true){
                    if (runAnimation) {
                        double step = Math.toRadians(1);
                        cube.setModel(cube.getModel().mul(new Mat4RotXYZ(step, step, step)));
                        display();
                        TimeUnit.MILLISECONDS.sleep(10);
                    }
                    if (!runAnimation){TimeUnit.MILLISECONDS.sleep(100);}
                }
            }catch (Exception e){e.printStackTrace();}
        };
        Thread animationThread = new Thread(animation);
        animationThread.start();

        panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                (raster).present(g);
            }
        };
        panel.setPreferredSize(new Dimension(width, height));

        panel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_W){
                    camera = camera.forward(camerspeed);
                    display();
                }
                if (e.getKeyCode() == KeyEvent.VK_S){
                    camera = camera.backward(camerspeed);
                    display();
                }
                if (e.getKeyCode() == KeyEvent.VK_A){
                    camera = camera.left(camerspeed);
                    display();
                }
                if (e.getKeyCode() == KeyEvent.VK_D){
                    camera = camera.right(camerspeed);
                    display();
                }
                if (e.getKeyCode() == KeyEvent.VK_O) {
                    wireRenderer.setProj(orthProj);
                    System.out.println("orth");
                    display();
                }
                if (e.getKeyCode() == KeyEvent.VK_P){
                    wireRenderer.setProj(prespProj);
                    System.out.println("presp");
                    display();
                }
                if (Objects.nonNull(selectedSolid)) {
                    if (e.getKeyCode() == KeyEvent.VK_R) {
                        editMode = 1;
                    }
                    if (e.getKeyCode() == KeyEvent.VK_T) {
                        editMode = 2;
                    }
                    if (e.getKeyCode() == KeyEvent.VK_Z) {
                        editMode = 3;
                    }
                }
                if (e.getKeyCode() == KeyEvent.VK_X){
                    selectNextSolid();
                }
                if (e.getKeyCode() == KeyEvent.VK_SPACE){
                    runAnimation = !runAnimation;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_R || e.getKeyCode() == KeyEvent.VK_T || e.getKeyCode() == KeyEvent.VK_Z){
                    editMode = 0;
                }
            }
        });

        panel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {

                switch (editMode){
                    case 0:
                        int deltaXcamera = cameraX - e.getX();
                        double azimuth = deltaXcamera / 1000.;
                        camera = camera.withAzimuth(camera.getAzimuth() + azimuth);

                        int deltaYcamera = cameraY - e.getY();
                        double zenith = deltaYcamera / 1000.;
                        camera = camera.withZenith(camera.getZenith() + zenith);

                        cameraX = e.getX();
                        cameraY = e.getY();

                        display();
                        break;

                    case 1:
                        double deltaXrotation = (rotationX - e.getX())/100.;
                        double deltaYrotation = (rotationY - e.getY())/100.;

                        selectedSolid.setModel(selectedSolid.getModel().mul(new Mat4RotXYZ(deltaXrotation, deltaYrotation, 0)));

                        rotationX = e.getX();
                        rotationY = e.getY();

                        display();
                        break;
                    case 2:
                        double deltaXtranslation = (translationX - e.getX())/-100.;
                        double deltyYtranslation = (translationY - e.getY())/100.;

                        selectedSolid.setModel(selectedSolid.getModel().mul(new Mat4Transl(deltaXtranslation, deltyYtranslation, 0)));

                        translationX = e.getX();
                        translationY = e.getY();

                        display();
                        break;
                }
            }
        });

        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {

                switch (editMode) {
                    case 0:
                        cameraX = e.getX();
                        cameraY = e.getY();
                        break;
                    case 1:
                        rotationX = e.getX();
                        rotationY = e.getY();
                        break;
                    case 2:
                        translationX = e.getX();
                        translationY = e.getY();
                        break;
                }
            }

        });
        panel.addMouseWheelListener(new MouseAdapter() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                switch (editMode){
                    case 0:
                        camera = camera.addRadius(e.getPreciseWheelRotation());
                        display();
                        break;
                    case 1:
                        selectedSolid.setModel(selectedSolid.getModel().mul(new Mat4RotZ(Math.toRadians(e.getPreciseWheelRotation()))));
                        display();
                        break;
                    case 2:
                        selectedSolid.setModel(selectedSolid.getModel().mul(new Mat4Transl(0,0,e.getPreciseWheelRotation()/100)));
                        display();
                        break;
                    case 3:
                        if (e.getPreciseWheelRotation() < 0){
                            selectedSolid.setModel(selectedSolid.getModel().mul(new Mat4Scale(0.9)));
                        }else selectedSolid.setModel(selectedSolid.getModel().mul(new Mat4Scale(1.1)));

                        display();
                        break;
                }
            }
        });


        frame.add(panel, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);

        panel.requestFocus();
        panel.requestFocusInWindow();

    }

    private void selectNextSolid(){
        if (Objects.nonNull(selectedSolid)){
            if (solids.get(solids.size()-1).equals(selectedSolid)){
                selectedSolid = null;
            }else selectedSolid = solids.get(solids.indexOf(selectedSolid) + 1);
        }
        else {selectedSolid = solids.get(0);}
        wireRenderer.setSelectedSolid(selectedSolid);
        display();
    }

    public void display(){
        zBuffer.clear();
        wireRenderer.setView(camera.getViewMatrix());

       wireRenderer.renderScene(solids);
//        Vertex v1 = new Vertex(new Vec3D(50, 50, 0.5), new Col(255, 0, 0));
//        Vertex v2 = new Vertex(new Vec3D(150, 400, 0.5), new Col(0, 255, 0));
//        Vertex v3 = new Vertex(new Vec3D(250, 150, 0.5), new Col(0, 0, 255));
//        triangler.draw(v1, v2, v3);
//        wireRenderer.renderAxis(true, true, true);

        panel.repaint();
    }

    public void start(){
        display();
    }
}