package contract;

import contract.event.PixelBuyEvent;
import contract.func.PixelInterface;
import contract.model.PixelEntity;
import contract.util.Money;
import io.nuls.contract.sdk.Address;
import io.nuls.contract.sdk.Block;
import io.nuls.contract.sdk.Contract;
import io.nuls.contract.sdk.Msg;
import io.nuls.contract.sdk.annotation.Payable;
import io.nuls.contract.sdk.annotation.Required;
import io.nuls.contract.sdk.annotation.View;

import java.math.BigInteger;

import static contract.func.PixelConstant.*;
import static io.nuls.contract.sdk.Utils.*;

/**
 * a simple nuls-smart-contract like eos pixel master
 * @author ad19900913@outlook.com
 */
public class PixelContract implements Contract, PixelInterface {

    protected PixelEntity [][] pixels;

    private int count = 0;
    private byte status = START;
    private long buyHeight = 0;
    private static Address lastBuyer = null;

    public PixelContract(@Required Short size) {
        pixels = new PixelEntity[size][size];
        for (short i = 0; i < pixels.length; i++) {
            PixelEntity[] pixel = pixels[i];
            for (short j = 0; j < pixel.length; j++) {
                PixelEntity pixelEntity = new PixelEntity();
                pixelEntity.setX((short) (j+1));
                pixelEntity.setY((short) (i+1));
                pixel[j] = pixelEntity;
            }
        }
    }

    @Payable
    @Override
    public void buy(@Required Short x, @Required Short y, @Required Short red, @Required Short blue, @Required Short yellow) {
        require(!tryStop(), "game over");
        PixelEntity pixelEntity = queryPixel(x,y);
        Address buyer = Msg.sender();
        Money buyValue = Money.valueOf(Msg.value().longValue());
        Address currentOwner = pixelEntity.getCurrentOwner();

        //validate buyValue is enough
        Money price = pixelEntity.getPrice();
        require((price.value()) <= buyValue.value(), "buyValue not enough");

        if (currentOwner != null) {
            currentOwner.transfer(BigInteger.valueOf(price.multiply(80).divide(100).value()));//给像素原所有者转账90%
        }

        long remain = buyValue.minus(price).value();
        if (remain > 0) {
            buyer.transfer(BigInteger.valueOf(remain));//return extra nuls
        }
        pixelEntity.setCurrentOwner(buyer);//set new owner
        pixelEntity.setPrice(price.multiply(110).divide(100));//new price float up 10%
        pixelEntity.setRed(red);
        pixelEntity.setBlue(blue);
        pixelEntity.setYellow(yellow);
        buyHeight = Block.currentBlockHeader().getHeight();
        lastBuyer = buyer;
        emit(new PixelBuyEvent((long) pseudoRandom(count++), buyer, pixelEntity));
    }

    @View
    @Override
    public PixelEntity queryPixel(@Required Short x, @Required Short y) {
        require(1 <= x && x <= SIZE, "x out of range");
        require(1 <= y && y <= SIZE, "y out of range");
        return pixels[y-1][x-1];
    }

    @View
    @Override
    public String info() {
        String result = "";
        result += "status-" + status + "\r\n";
        result += "balance-" + Msg.address().balance() + "\r\n";
        result += "lastBuyer-" + lastBuyer + "\r\n";
        return result;
    }

    public boolean tryStop(){
        long height = Block.currentBlockHeader().getHeight();
        if (buyHeight > 0 && height - buyHeight > 8640 && Msg.address().balance().longValue() > 0) {
            lastBuyer.transfer(Msg.address().balance());
            status = STOP;
            return true;
        }
        return false;
    }

    /**
     * 测试阶段保留，清空合约nuls余额，便于删除合约
     * @return
     */
    public void clear(@Required String addr) {
        Address contractAddress = Msg.address();
        BigInteger balance = contractAddress.balance();
        Address address = new Address(addr);
        address.transfer(balance);
    }

}
