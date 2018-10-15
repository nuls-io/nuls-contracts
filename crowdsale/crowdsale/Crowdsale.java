package crowdsale;

import io.nuls.contract.sdk.Address;
import io.nuls.contract.sdk.Event;
import io.nuls.contract.sdk.Msg;
import io.nuls.contract.sdk.annotation.Payable;
import io.nuls.contract.sdk.annotation.View;

import java.math.BigInteger;

import static io.nuls.contract.sdk.Utils.emit;
import static io.nuls.contract.sdk.Utils.require;

public class Crowdsale {

    private Address token;//代币地址

    private Address wallet;//打到众筹币的钱包

    private BigInteger rate;

    private BigInteger nulsRaised = BigInteger.ZERO;//实际筹到的钱

    @View
    public Address getToken() {
        return token;
    }

    @View
    public Address getWallet() {
        return wallet;
    }


    @View
    public BigInteger getRate() {
        return rate;
    }

    @View
    public BigInteger getNulsRaised() {
        return nulsRaised;
    }

    /**
     * 代币购买事件
     */
    class TokenPurchaseEvent implements Event {

        private Address purchaser;//买方

        private Address beneficiary;//受益人（可能是代买受益人可能是自己或者他人）

        private BigInteger value;
       //数量
        private BigInteger amount;

        public TokenPurchaseEvent(Address purchaser, Address beneficiary, BigInteger value, BigInteger amount) {
            this.purchaser = purchaser;
            this.beneficiary = beneficiary;
            this.value = value;
            this.amount = amount;
        }

        public Address getPurchaser() {
            return purchaser;
        }

        public void setPurchaser(Address purchaser) {
            this.purchaser = purchaser;
        }

        public Address getBeneficiary() {
            return beneficiary;
        }

        public void setBeneficiary(Address beneficiary) {
            this.beneficiary = beneficiary;
        }

        public BigInteger getValue() {
            return value;
        }

        public void setValue(BigInteger value) {
            this.value = value;
        }

        public BigInteger getAmount() {
            return amount;
        }

        public void setAmount(BigInteger amount) {
            this.amount = amount;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TokenPurchaseEvent that = (TokenPurchaseEvent) o;

            if (purchaser != null ? !purchaser.equals(that.purchaser) : that.purchaser != null) return false;
            if (beneficiary != null ? !beneficiary.equals(that.beneficiary) : that.beneficiary != null) return false;
            if (value != null ? !value.equals(that.value) : that.value != null) return false;
            return amount != null ? amount.equals(that.amount) : that.amount == null;
        }

        @Override
        public int hashCode() {
            int result = purchaser != null ? purchaser.hashCode() : 0;
            result = 31 * result + (beneficiary != null ? beneficiary.hashCode() : 0);
            result = 31 * result + (value != null ? value.hashCode() : 0);
            result = 31 * result + (amount != null ? amount.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "TokenPurchaseEvent{" +
                    "purchaser=" + purchaser +
                    ", beneficiary=" + beneficiary +
                    ", value=" + value +
                    ", amount=" + amount +
                    '}';
        }

    }

    public Crowdsale(BigInteger rate, Address wallet, Address token) {
        require(rate != null && rate.compareTo(BigInteger.ZERO) > 0);
        require(wallet != null);
        require(token != null);

        this.rate = rate;
        this.wallet = wallet;
        this.token = token;
    }

//    function () external payable {
//        buyTokens(msg.sender);
//    }

    /**
     * 买代币(为什么传受益人beneficiary 可能是代买的情况)
     * @param beneficiary
     */
    @Payable
    public void buyTokens(Address beneficiary) {
        //随消息发送的Na数 2nuls=2*10^8na
        BigInteger amount = Msg.value();
        //转成nuls
        BigInteger nuls= amount.divide(BigInteger.valueOf(100000000));
        //验证地址 不为空  weiAmount不为空 大于0
        preValidatePurchase(beneficiary, nuls);
        //weiAmount*rate
        BigInteger tokens = getTokenAmount(nuls);
        //加上发送的数量
        nulsRaised = nulsRaised.add(nuls);
        //调用  （address）token.call 给beneficiary 代币
        processPurchase(beneficiary, tokens);
        //发送事件 Msg.sender()创建合约的地址 私钥导入的
        emit(new TokenPurchaseEvent(Msg.sender(),beneficiary, nuls, tokens));

        updatePurchasingState(beneficiary, nuls);
        //合约向wallet转账
        forwardFunds();
        postValidatePurchase(beneficiary, nuls);
    }

    protected void preValidatePurchase(Address beneficiary, BigInteger weiAmount) {
        require(beneficiary != null,"beneficiary != null");
        require(weiAmount != null && weiAmount.compareTo(BigInteger.ZERO) > 0,"weiAmount != null && weiAmount.compareTo(BigInteger.ZERO) > 0");
    }

    protected void postValidatePurchase(Address beneficiary, BigInteger weiAmount) {
        // optional override
    }

    protected void deliverTokens(Address beneficiary, BigInteger tokenAmount) {
        String[][] args = new String[][]{{beneficiary.toString()}, {tokenAmount.toString()}};
        token.call("transfer", null, args, null);
    }

    protected void processPurchase(Address beneficiary, BigInteger tokenAmount) {
        deliverTokens(beneficiary, tokenAmount);
    }

    protected void updatePurchasingState(Address beneficiary, BigInteger weiAmount) {
        // optional override
    }

    protected BigInteger getTokenAmount(BigInteger wei) {
        return wei.multiply(rate);
    }
    //合约向wallet转账
    protected void forwardFunds() {
       // require(-1 > 0,wallet+"####Msg.value()="+Msg.value());
        wallet.transfer(Msg.value());
    }

}
