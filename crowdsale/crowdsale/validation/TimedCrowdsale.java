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

    protected void onlyWhileOpen() {
        require(Block.timestamp() >= openingTime, "It hasn't been opened yet!");
        require(Block.timestamp() <= closingTime, "It has been over!");
    }

    protected TimedCrowdsale(long openingTime, long closingTime, BigDecimal rate, Address wallet, Address token) {
        super(rate, wallet, token);
        require(openingTime <= closingTime, "Open time should be lower than close time.");
        require(closingTime >= Block.timestamp(), "Close time cant't be lower than now.");

        this.openingTime = openingTime;
        this.closingTime = closingTime;
    }

    protected boolean hasClosed() {
        return Block.timestamp() > closingTime;
    }

}
