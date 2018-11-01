package contract.func;

import contract.model.PixelEntity;

public interface PixelInterface {

    /**
     * buy a pixel
     * @param x
     * @param y
     * @return
     */
    void buy(Short x, Short y, Short red, Short blue, Short yellow);

    /**
     * get pixel info
     * @param x
     * @param y
     * @return
     */
    PixelEntity queryPixel(Short x, Short y);

    /**
     * get game info
     * @return
     */
    String info();

    /**
     * used to get the bonus
     * @return
     */
    boolean tryStop();

}
