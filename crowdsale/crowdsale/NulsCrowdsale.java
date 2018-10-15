package crowdsale;

import crowdsale.distribution.RefundableCrowdsale;
import io.nuls.contract.sdk.Address;
import io.nuls.contract.sdk.Block;
import io.nuls.contract.sdk.Contract;
import io.nuls.contract.sdk.Msg;
import io.nuls.contract.sdk.annotation.Payable;
import io.nuls.contract.sdk.annotation.View;

import java.math.BigInteger;
import java.util.Map;

import static io.nuls.contract.sdk.Utils.emit;
import static io.nuls.contract.sdk.Utils.require;

public class NulsCrowdsale extends RefundableCrowdsale  implements Contract {
    private Address owner;//合约所有者

    public NulsCrowdsale(long openingTime, long closingTime, BigInteger rate, Address wallet, Address token, BigInteger goal,Address owner) {
        super(openingTime, closingTime, rate, wallet, token, goal);
        this.owner=owner;
    }
    @Override
    @Payable
    public void _payable() {
        super.onlyWhileOpen();//众筹在规定的之间内
        require(isFinalized(),"isFinalized()");
        require(!super.goalReached(),"goal Reached");
        super.buyTokens(Msg.sender());
        super.getVault().deposit(Msg.sender());//保存这个地址 存了多少钱
    }
    @Override
    @View
    public Address getOwner() {
        return owner;
    }

    @Override
    public void onlyOwner() {
        //Msg.sender() 消息发送者地址
        require(Msg.sender().equals(owner),"Msg.sender not equals owner");
    }

    /**
     * 合约所有权转让事件
     * @param newOwner
     */
    @Override
    public void transferOwnership(Address newOwner) {
        onlyOwner();
        emit(new OwnershipTransferredEvent(owner, newOwner));
        owner = newOwner;
    }

    /**
     *  合约所有权放弃事件
     */
    @Override
    public void renounceOwnership() {
        onlyOwner();
        emit(new OwnershipRenouncedEvent(owner));
        owner = null;
    }

    /**
     * 购买代币(没达到目标值之前可以购买)
     * @param beneficiary
     */
    @Payable
    public void  buyTokens(Address beneficiary){
        super.onlyWhileOpen();//众筹在规定的之间内
        require(isFinalized(),"isFinalized()");
        require(!super.goalReached(),"goal Reached");
        super.buyTokens(beneficiary);
        super.getVault().deposit( beneficiary);//保存这个地址 存了多少钱

   }

    /**
     * 索赔退款
     */
   /* public void claimRefund() {
        super.claimRefund();
    }*/
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
     *  退款（给消息发送人）
     */
    public void refund(){
        super.getVault().deposit(Msg.sender());

    }
    /**
     *  退款给所有人
     */
    /*public void refundAll(){
        onlyOwner();
        Map<Address, BigInteger> map= super.getVault().getDepositedMap();

    }*/

    /**
     * 查看谁交了多少钱
     */
    public Map<Address, BigInteger> refundAll(){
        onlyOwner();
        Map<Address, BigInteger> map= super.getVault().getDepositedMap();
        return map;
    }
    /**
     * 获取区块时间
     * @return
     */
    public Long getBlockTime(){
        return Block.timestamp();
    }
}
