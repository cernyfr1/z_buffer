package rasterdata;

import java.util.Optional;

public interface Raster<P> {

    int getWidth();
    int getHeight();
    boolean setPixel(int x, int y, P pixel);
    Optional<P> getPixel(int x, int y);
    void clear();
    default boolean isValidAddress(int x, int y){
       return x > 0 && x < getWidth() && y > 0 && y < getHeight();
    }

}
