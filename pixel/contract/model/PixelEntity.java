package contract.model;

import contract.util.Money;
import io.nuls.contract.sdk.Address;

import static contract.func.PixelConstant.INIT_PRICE;

public class PixelEntity {

    private Short x;
    private Short y;
    private Address currentOwner = null;
    private Money price = INIT_PRICE;
    private Short red = 199;
    private Short yellow = 72;
    private Short blue = 5;

    public Short getX() {
        return x;
    }

    public void setX(Short x) {
        this.x = x;
    }

    public Short getY() {
        return y;
    }

    public void setY(Short y) {
        this.y = y;
    }

    public Address getCurrentOwner() {
        return currentOwner;
    }

    public void setCurrentOwner(Address currentOwner) {
        this.currentOwner = currentOwner;
    }

    public Money getPrice() {
        return price;
    }

    public void setPrice(Money price) {
        this.price = price;
    }

    public Short getRed() {
        return red;
    }

    public void setRed(Short red) {
        this.red = red;
    }

    public Short getYellow() {
        return yellow;
    }

    public void setYellow(Short yellow) {
        this.yellow = yellow;
    }

    public Short getBlue() {
        return blue;
    }

    public void setBlue(Short blue) {
        this.blue = blue;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PixelEntity that = (PixelEntity) o;

        if (x != null ? !x.equals(that.x) : that.x != null) return false;
        if (y != null ? !y.equals(that.y) : that.y != null) return false;
        if (currentOwner != null ? !currentOwner.equals(that.currentOwner) : that.currentOwner != null) return false;
        if (price != null ? !price.equals(that.price) : that.price != null) return false;
        if (red != null ? !red.equals(that.red) : that.red != null) return false;
        if (yellow != null ? !yellow.equals(that.yellow) : that.yellow != null) return false;
        return blue != null ? blue.equals(that.blue) : that.blue == null;
    }

    @Override
    public int hashCode() {
        int result = x != null ? x.hashCode() : 0;
        result = 31 * result + (y != null ? y.hashCode() : 0);
        result = 31 * result + (currentOwner != null ? currentOwner.hashCode() : 0);
        result = 31 * result + (price != null ? price.hashCode() : 0);
        result = 31 * result + (red != null ? red.hashCode() : 0);
        result = 31 * result + (yellow != null ? yellow.hashCode() : 0);
        result = 31 * result + (blue != null ? blue.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PixelEntity{" +
                "x=" + x +
                ", y=" + y +
                ", currentOwner=" + currentOwner +
                ", price=" + price +
                ", red=" + red +
                ", yellow=" + yellow +
                ", blue=" + blue +
                '}';
    }
}
