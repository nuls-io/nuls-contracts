package crowdsale.validation;

import crowdsale.Crowdsale;
import io.nuls.contract.sdk.Address;
import io.nuls.contract.sdk.Block;
import io.nuls.contract.sdk.annotation.View;

import java.math.BigDecimal;


import static io.nuls.contract.sdk.Utils.require;

public class TimedCrowdsale extends Crowdsale {

    private long openingTime;
    private long closingTime;

    @View
    public long getOpeningTime() {
        return openingTime;
    }

    @View
    public long getClosingTime() {
        return closingTime;
    }

    public void onlyWhileOpen() {
        require(Block.timestamp() >= openingTime && Block.timestamp() <= closingTime, "Block.timestamp() >= openingTime && Block.timestamp() <= closingTime");
    }

    public TimedCrowdsale(long openingTime, long closingTime, BigDecimal rate, Address wallet, Address token) {
        super(rate, wallet, token);
        // require(openingTime >= Block.timestamp(),"openingTime >= Block.timestamp()");
        require(closingTime >= Block.timestamp(), "closingTime>= Block.timestamp()");
        require(closingTime >= openingTime, "closingTime >= openingTime");

        this.openingTime = openingTime;
        this.closingTime = closingTime;
    }

    public boolean hasClosed() {
        return Block.timestamp() > closingTime;
    }

    /**
     * 预验证购买 看众筹是否关闭  验证beneficiary weiAmount参数
     *
     * @param beneficiary
     * @param weiAmount
     */
    @Override
    protected void preValidatePurchase(Address beneficiary, BigDecimal weiAmount) {
        //onlyWhileOpen();
        super.preValidatePurchase(beneficiary, weiAmount);
    }

}
