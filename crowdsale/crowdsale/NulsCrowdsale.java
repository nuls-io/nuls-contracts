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
        //众筹归集钱包不能购买代币
        if (!Msg.sender().equals(getWallet())) {
            //众筹在规定的之间内
            super.onlyWhileOpen();
            //是否关闭
            require(!isFinalized(), "It has been over!");
            //是否达到预定值
            require(!super.goalReached(), "goal reached!");
            super.buyTokens(Msg.sender());
            //保存这个地址 存了多少钱
            super.getVault().deposit(Msg.sender());
        } else {
            require(isFinalized(), "It hasn't been over yet!");
            require(getWallet().equals(Msg.sender()), "The address must be the wallet address of this contract.");
        }
    }

    @Override
    @View
    public Address getOwner() {
        return owner;
    }

    @Override
    @View
    public void onlyOwner() {
        // 消息发送者地址
        require(Msg.sender().equals(owner), "sender is not the owner");
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
        //众筹归集钱包不能购买代币
        require(!getWallet().equals(beneficiary), "The address can not buy token");
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
        onlyOwner();
        super.getVault().enableRefunds();
        this.finalized();
    }

    /**
     * 退款（给消息发送人） 首先调用 enableRefunds() 处于退款状态后, 然后要wallet钱包给众筹合约地址转账 才能退款
     */
    public void refund() {
        super.claimRefund();
    }

    @View
    public boolean hasClosed() {
        return super.hasClosed() || isFinalized();
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
