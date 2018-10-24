package crowdsale.distribution.utils;

import io.nuls.contract.sdk.Address;
import io.nuls.contract.sdk.Event;
import io.nuls.contract.sdk.Msg;
import io.nuls.contract.sdk.annotation.Payable;
import io.nuls.contract.sdk.annotation.View;
import ownership.OwnableImpl;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import static io.nuls.contract.sdk.Utils.emit;
import static io.nuls.contract.sdk.Utils.require;


/**
 * 可退款的众筹 记录了状态  哪个地址给了多少钱  众筹的钱包
 */
public class RefundVault extends OwnableImpl {

    // 激活
    public int Active = 1;
    // 退款
    public int Refunding = 2;
    // 关闭
    public int Closed = 3;
    /**
     * 存放
     */
    private Map<Address, BigInteger> deposited = new HashMap<Address, BigInteger>();
    // 众筹的钱包
    private Address wallet;
    // 状态
    private int state;

    /**
     * 获取这个address众筹金额
     *
     * @param address
     * @return
     */
    @View
    public BigInteger getDeposited(Address address) {
        BigInteger value = deposited.get(address);
        if (value == null) {
            value = BigInteger.ZERO;
        }
        return value;
    }

    @View
    public Address getWallet() {
        return wallet;
    }

    @View
    public Map<Address, BigInteger> getDepositedMap() {
        return deposited;
    }

    @View
    public int getState() {
        return state;
    }

    class Closed implements Event {

    }

    class RefundsEnabled implements Event {

    }

    class Refunded implements Event {

        // 退款地址
        private Address beneficiary;

        // 多少币
        private BigInteger amount;

        public Refunded(Address beneficiary, BigInteger amount) {
            this.beneficiary = beneficiary;
            this.amount = amount;
        }

        @Override
        public String toString() {
            return "Refunded{" +
                    "beneficiary=" + beneficiary +
                    ", amount=" + amount +
                    '}';
        }

    }

    public RefundVault(Address wallet) {
        super();
        this.wallet = wallet;
        this.state = Active;
    }

    /**
     * 这个地址存放多少钱
     *
     * @param investor
     */
    @Payable
    public void deposit(Address investor) {
        //onlyOwner();
        require(state == Active, "state == Active");
        BigInteger value = deposited.get(investor);
        if (value == null) {
            value = BigInteger.ZERO;
        }
        deposited.put(investor, value.add(Msg.value()));
    }

    /**
     * 关闭 众筹
     */
    public void close() {
        onlyOwner();
        require(state == Active);
        state = Closed;
        emit(new Closed());
        wallet.transfer(Msg.address().balance());
    }

    /**
     * 允许退款
     */
    public void enableRefunds() {
        onlyOwner();
        require(state == Active, "state == Active");
        state = Refunding;
        emit(new RefundsEnabled());
    }

    /**
     * 退款给这个地址
     *
     * @param investor
     */
    public void refund(Address investor) {
        require(state == Refunding, "state == Refunding");
        BigInteger depositedValue = deposited.get(investor);
        if (depositedValue == null) {
            depositedValue = BigInteger.ZERO;
        }
        deposited.put(investor, BigInteger.ZERO);
        investor.transfer(depositedValue);
        emit(new Refunded(investor, depositedValue));
    }

}
