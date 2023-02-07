package rasterdata;

import transforms.Col;

import java.util.Optional;

public class DepthRaster implements Raster<Double>{

    private final double[][] array;

    public DepthRaster(int width, int height) {
        array = new double[width][height];
        clear();
    }

    @Override
    public int getWidth() {
        return array.length;
    }

    @Override
    public int getHeight() {
        return array[0].length;
    }

    @Override
    public boolean setPixel(int x, int y, Double pixel) {
        if(isValidAddress(x, y)){
            array[x][y] = pixel;
            return true;
        }
        return false;
    }

    @Override
    public Optional<Double> getPixel(int x, int y) {
        if(x > 0 && x < getWidth() && y > 0 && y < getHeight()){
            return Optional.of(array[x][y]);
        }

        return Optional.empty();
    }

    @Override
    public void clear() {
        for(int x = 0; x < getWidth(); x++){
            for(int y = 0; y < getHeight(); y++){
                array[x][y] = 1.0;
            }
        }
    }
}
