package crowdsale;

import crowdsale.distribution.RefundableCrowdsale;
import io.nuls.contract.sdk.Address;
import io.nuls.contract.sdk.Block;
import io.nuls.contract.sdk.Contract;
import io.nuls.contract.sdk.Msg;
import io.nuls.contract.sdk.annotation.Payable;
import io.nuls.contract.sdk.annotation.View;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

import static io.nuls.contract.sdk.Utils.emit;
import static io.nuls.contract.sdk.Utils.require;

public class NulsCrowdsale extends RefundableCrowdsale implements Contract {

    //合约所有者
    private Address owner;

    public NulsCrowdsale(long openingTime, long closingTime, BigDecimal rate, Address wallet, Address token, BigDecimal goal, Address owner) {
        super(openingTime, closingTime, rate, wallet, token, goal);
        this.owner = owner;
    }

    /**
     * 向众筹合约地址转nuls的时候会触发
     */
    @Override
    @Payable
    public void _payable() {
        //众筹目标钱包不能转给众筹合约
        if (!Msg.sender().equals(getWallet())) {
            //众筹在规定的之间内
            super.onlyWhileOpen();
            //是否手动关闭
            require(!isFinalized(), "finalized!");
            //是否达到预定值
            require(!super.goalReached(), "goal reached!");
            super.buyTokens(Msg.sender());
            //保存这个地址 存了多少钱
            super.getVault().deposit(Msg.sender());
        }
    }

    @Override
    @View
    public Address getOwner() {
        return owner;
    }

    @Override
    public void onlyOwner() {
        //Msg.sender() 消息发送者地址
        require(Msg.sender().equals(owner), "msg.sender not equals owner");
    }

    /**
     * 合约所有权转让事件
     *
     * @param newOwner
     */
    @Override
    public void transferOwnership(Address newOwner) {
        onlyOwner();
        emit(new OwnershipTransferredEvent(owner, newOwner));
        owner = newOwner;
    }

    /**
     * 合约所有权放弃事件
     */
    @Override
    public void renounceOwnership() {
        onlyOwner();
        emit(new OwnershipRenouncedEvent(owner));
        owner = null;
    }

    /**
     * 购买代币(没达到目标值之前可以购买)
     *
     * @param beneficiary
     */
    @Payable
    public void buyTokens(Address beneficiary) {
        //众筹在规定的之间内
        super.onlyWhileOpen();
        require(!isFinalized(), "finalized!");
        require(!super.goalReached(), "goal reached!");
        super.buyTokens(beneficiary);
        //保存这个地址 存了多少钱
        super.getVault().deposit(beneficiary);

    }


    /**
     * 停止众筹
     */
    public void finalized() {
        super.finalized();
    }

    /**
     * 允许退款（必须是自己创建的才能操作）
     */
    public void enableRefunds() {
        super.getVault().enableRefunds();
    }

    /**
     * 退款（给消息发送人） 首先要wallet钱包给众筹合约地址转账 然后调用 enableRefunds() 处于退款状态后 才能退款
     */
    @Payable
    public void refund() {
        super.getVault().refund(Msg.sender());

    }

    /**
     * 查看谁交了多少钱
     */
    @View
    public Map<Address, BigInteger> detail() {
        onlyOwner();
        Map<Address, BigInteger> map = super.getVault().getDepositedMap();
        return map;
    }
}
