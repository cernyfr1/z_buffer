package objectdata;

import transforms.Mat4;

public interface Transformable<V> {

    V transformed(Mat4 transformation);
    V dehomog();
    V toViewport(int width, int height);
}
