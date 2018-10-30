package crowdsale.distribution;

import crowdsale.distribution.utils.RefundVault;
import io.nuls.contract.sdk.Address;
import io.nuls.contract.sdk.Msg;
import io.nuls.contract.sdk.annotation.View;

import java.math.BigDecimal;
import java.math.BigInteger;

import static io.nuls.contract.sdk.Utils.require;

public abstract class RefundableCrowdsale extends FinalizableCrowdsale {

    //众筹目标金额
    private BigDecimal goal;

    private RefundVault vault;

    @View
    public BigDecimal getGoal() {
        return goal;
    }

    protected RefundVault getVault() {
        return vault;
    }

    protected RefundableCrowdsale(long openingTime, long closingTime, BigDecimal rate, Address wallet, Address token, BigDecimal goal) {
        super(openingTime, closingTime, rate, wallet, token);
        require(goal.compareTo(BigDecimal.ZERO) > 0, "Goal amount must be greater than 0");
        vault = new RefundVault(wallet);
        this.goal = goal;
    }


    protected void claimRefund() {
        //没有结束
        require(isFinalized(), "It hasn't been over yet!");

        //退款给 Msg.sender()
        vault.refund(Msg.sender());
    }

    /**
     * 是否达到目标值
     *
     * @return
     */
    @View
    public boolean goalReached() {
        return getNulsRaised().compareTo(goal) >= 0;
    }

}
