package rasterdata;

import transforms.Col;

public class ZBuffer {

    private final Raster<Col> colorRaster;
    private final Raster<Double> depthRaster;

    public ZBuffer(Raster<Col> colorRaster) {
        this.colorRaster = colorRaster;
        this.depthRaster = new DepthRaster(colorRaster.getWidth(), colorRaster.getHeight());
    }

    public void setPixel(int x, int y, double z, Col pixel){
        if (depthRaster.getPixel(x, y).get() > z){
            colorRaster.setPixel(x, y, pixel);
            depthRaster.setPixel(x, y, z);
        }
    }

    public void clear(){
        colorRaster.clear();
        depthRaster.clear();
    }

    public int getWidth(){
        return colorRaster.getWidth();
    }
    public int getHeight(){
        return colorRaster.getHeight();
    }
}
