package ownership;

import io.nuls.contract.sdk.Address;
import io.nuls.contract.sdk.Msg;
import io.nuls.contract.sdk.annotation.View;

import static io.nuls.contract.sdk.Utils.emit;
import static io.nuls.contract.sdk.Utils.require;

public class OwnableImpl implements Ownable {

    private Address owner;//合约所有者

    public OwnableImpl() {
        this.owner = Msg.sender();
    }

    @Override
    @View
    public Address getOwner() {
        return owner;////合约所有者为合约创建者
    }

    @Override
    public void onlyOwner() {
        //判断合约使用者是不是合约拥有者，是合约拥有者才能执行
        require(Msg.sender().equals(owner),"Msg.sender().equals(owner)");
    }

    /**
     * 转让合约所有权：只有合约所有者能使用，转让合约所有权给newOwner
     * @param newOwner
     */
    @Override
    public void transferOwnership(Address newOwner) {
        onlyOwner();
        emit(new OwnershipTransferredEvent(owner, newOwner));
        owner = newOwner;
    }

    /**
     * 放弃合约
     */
    @Override
    public void renounceOwnership() {
        onlyOwner();
        emit(new OwnershipRenouncedEvent(owner));
        owner = null;
    }

}
