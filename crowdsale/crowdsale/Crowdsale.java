package crowdsale;

import io.nuls.contract.sdk.Address;
import io.nuls.contract.sdk.Event;
import io.nuls.contract.sdk.Msg;
import io.nuls.contract.sdk.annotation.Payable;
import io.nuls.contract.sdk.annotation.View;

import java.math.BigDecimal;

import static io.nuls.contract.sdk.Utils.emit;
import static io.nuls.contract.sdk.Utils.require;

public class Crowdsale {

    // 代币地址
    private Address token;

    // 众筹NULS的钱包
    private Address wallet;

    // 兑换比例
    private BigDecimal rate;

    // 实际筹到的钱
    private BigDecimal nulsRaised = BigDecimal.ZERO;

    // token的最大支持的小数位数
    private int decimals;

    private boolean isRetriveTokenDecimals = false;

    @View
    public Address getToken() {
        return token;
    }

    @View
    public Address getWallet() {
        return wallet;
    }


    @View
    public BigDecimal getRate() {
        return rate;
    }

    @View
    public BigDecimal getNulsRaised() {
        return nulsRaised;
    }

    /**
     * 代币购买事件
     */
    class TokenPurchaseEvent implements Event {

        // 买方
        private Address purchaser;

        // 受益人（可能是代买受益人可能是自己或者他人）
        private Address beneficiary;

        // 花费的NULS
        private BigDecimal value;

        // token数量
        private BigDecimal amount;


        public TokenPurchaseEvent(Address purchaser, Address beneficiary, BigDecimal value, BigDecimal amount) {
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

        public BigDecimal getValue() {
            return value;
        }

        public void setValue(BigDecimal value) {
            this.value = value;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
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
                    ", value=" + (value != null ? value.toPlainString() : "0") +
                    ", amount=" + (amount != null ? amount.toPlainString() : "0") +
                    '}';
        }

    }

    protected Crowdsale(BigDecimal rate, Address wallet, Address token) {
        require(rate != null && rate.compareTo(BigDecimal.ZERO) > 0);
        require(wallet != null);
        require(token != null);

        this.rate = rate;
        this.wallet = wallet;
        this.token = token;
    }

    /**
     * 买代币(为什么传受益人beneficiary 可能是代买的情况)
     *
     * @param beneficiary
     */
    @Payable
    public void buyTokens(Address beneficiary) {
        //随消息发送的Na数 2nuls=2*10^8na
        // BigInteger amount = Msg.value();
        BigDecimal amount = new BigDecimal(Msg.value());
        //转成nuls
        BigDecimal nuls = amount.divide(new BigDecimal(100000000));
        //验证地址 不为空  amount不为空 大于0
        preValidatePurchase(beneficiary, nuls);
        //amount*rate*10^decimal
        BigDecimal tokens = getTokenAmount(nuls);
        //加上发送的数量
        nulsRaised = nulsRaised.add(nuls);
        //调用  （address）token.call 给beneficiary 代币
        processPurchase(beneficiary, tokens);
        //发送事件 Msg.sender()创建合约的地址 私钥导入的
        emit(new TokenPurchaseEvent(Msg.sender(), beneficiary, nuls, tokens));

        updatePurchasingState(beneficiary, nuls);
        //合约向wallet转账
        forwardFunds();
        postValidatePurchase(beneficiary, nuls);
    }

    /**
     * 预验证购买 看众筹是否关闭  验证beneficiary amount参数
     *
     * @param beneficiary
     * @param amount
     */
    protected void preValidatePurchase(Address beneficiary, BigDecimal amount) {
        require(beneficiary != null, "beneficiary != null");
        require(amount != null && amount.compareTo(BigDecimal.ZERO) > 0, "amount require and amount must be greater than 0");
    }

    protected void postValidatePurchase(Address beneficiary, BigDecimal amount) {
        // optional override
    }

    protected int getTokenDecimals() {
        if(!isRetriveTokenDecimals) {
            try {
                String decimal = token.callWithReturnValue("decimals", null, null, null);
                int dec = Integer.valueOf(decimal);
                this.decimals = dec;
                isRetriveTokenDecimals = true;
            } catch (Exception e) {
                require(false, "Illegal token address.");
            }
        }
        return this.decimals;
    }

    protected void deliverTokens(Address beneficiary, BigDecimal tokenAmount) {
        String[][] args = new String[][]{{beneficiary.toString()}, {tokenAmount.toPlainString()}};
        token.call("transfer", null, args, null);
    }

    protected void processPurchase(Address beneficiary, BigDecimal tokenAmount) {
        deliverTokens(beneficiary, tokenAmount);
    }

    protected void updatePurchasingState(Address beneficiary, BigDecimal amount) {
        // optional override
    }

    protected BigDecimal getTokenAmount(BigDecimal amount) {
        return amount.multiply(rate).scaleByPowerOfTen(getTokenDecimals());
    }

    //合约向wallet转账
    protected void forwardFunds() {
        wallet.transfer(Msg.value());
    }

}
