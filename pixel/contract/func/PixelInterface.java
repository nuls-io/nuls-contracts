package contract.func;

import contract.model.PixelEntity;
import io.nuls.contract.sdk.Address;
import io.nuls.contract.sdk.annotation.Required;

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

}
