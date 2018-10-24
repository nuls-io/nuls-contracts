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

    public RefundVault getVault() {
        return vault;
    }

    public RefundableCrowdsale(long openingTime, long closingTime, BigDecimal rate, Address wallet, Address token, BigDecimal goal) {
        super(openingTime, closingTime, rate, wallet, token);
        require(goal.compareTo(BigDecimal.ZERO) > 0);
        vault = new RefundVault(wallet);
        this.goal = goal;
    }


    public void claimRefund() {
        //没有结束
        require(isFinalized());
        //没达到目标金额
        require(!goalReached());

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

    @Override
    protected void finalization() {
        if (goalReached()) {
            vault.close();
        } else {
            vault.enableRefunds();
        }

        super.finalization();
    }


}
