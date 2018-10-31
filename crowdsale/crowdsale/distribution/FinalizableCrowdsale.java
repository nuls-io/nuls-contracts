package crowdsale.distribution;

import crowdsale.validation.TimedCrowdsale;
import io.nuls.contract.sdk.Address;
import io.nuls.contract.sdk.Event;
import io.nuls.contract.sdk.annotation.View;
import ownership.Ownable;

import java.math.BigDecimal;
import java.math.BigInteger;

import static io.nuls.contract.sdk.Utils.emit;
import static io.nuls.contract.sdk.Utils.require;

/**
 * 可终结的众筹
 */
public abstract class FinalizableCrowdsale extends TimedCrowdsale implements Ownable {

    private boolean isFinalized = false;

    protected boolean isFinalized() {
        return isFinalized;
    }

    class Finalized implements Event {

    }

    protected void finalized() {
        require(!isFinalized, "It has been finalized.");
        require(!hasClosed(), "It has been closed.");

        emit(new Finalized());
        isFinalized = true;
    }

    protected void finalization() {
        finalized();
    }

    protected FinalizableCrowdsale(long openingTime, long closingTime, BigDecimal rate, Address wallet, Address token) {
        super(openingTime, closingTime, rate, wallet, token);
    }

}
