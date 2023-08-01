package io.nuls.contract;

import io.nuls.contract.event.CrossChainTokenTransferInEvent;
import io.nuls.contract.event.CrossChainTokenTransferOutEvent;
import io.nuls.contract.model.NRC20Amount;
import io.nuls.contract.ownable.Ownable;
import io.nuls.contract.sdk.*;
import io.nuls.contract.sdk.annotation.Payable;
import io.nuls.contract.sdk.annotation.PayableMultyAsset;
import io.nuls.contract.sdk.annotation.Required;
import io.nuls.contract.sdk.annotation.View;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import static io.nuls.contract.sdk.Utils.require;

/**
 */
public class CrossTokenSystemContract extends Ownable implements Contract {

    /**
     * key - chainId + "-" + assetsId
     * value - NRC20Amount: {nrc20: "", value: 0}
     */
    private Map<String, NRC20Amount> nrc20CrossChainTransferOutMap = new HashMap<String, NRC20Amount>();

    /**
     * 合约资产跨链转出时调用的方法
     */
    @PayableMultyAsset
    public boolean onNRC20Received(@Required Address from, @Required String to, @Required BigInteger value) {
        Address nrc20 = Msg.sender();
        // 检查转出人对系统合约的token授权额度
        // 检查转出人是否有足够的token
        // 转移转出人的token到系统合约当中
        // 生成并发出合约资产跨链转出交易
        String[] args = new String[]{
                from.toString(),
                to,
                value.toString()};
        String[] result = (String[]) Utils.invokeExternalCmd("cc_tokenOutCrossChain", args);
        Integer chainId = Integer.parseInt(result[0]);
        Integer assetsId = Integer.parseInt(result[1]);
        String txHash = result[2];
        BigInteger txFee = new BigInteger(result[3]);
        // 系统合约记录当前资产转出总量
        String key = chainId + "-" + assetsId;
        NRC20Amount nrc20Amount = nrc20CrossChainTransferOutMap.get(key);
        if(nrc20Amount == null) {
            nrc20CrossChainTransferOutMap.put(key, new NRC20Amount(nrc20, value));
        } else {
            nrc20Amount.addValue(value);
        }
        MultyAssetValue[] multyAssetValues = Msg.multyAssetValues();
        require(multyAssetValues != null && multyAssetValues.length == 1, "Insufficient fees paid for cross-chain transfers[0]");
        MultyAssetValue nulsValue = multyAssetValues[0];
        BigInteger fromTransferIn = nulsValue.getValue();
        BigInteger refund = fromTransferIn.subtract(txFee);
        require(refund.compareTo(BigInteger.ZERO) >= 0, "Insufficient fees paid for cross-chain transfers[1]");
        // 退还剩余的费用
        from.transfer(refund, nulsValue.getAssetChainId(), nulsValue.getAssetId());
        // 发出合约资产跨链转出事件
        Utils.emit(new CrossChainTokenTransferOutEvent(txHash, nrc20, from, to, value, chainId, assetsId));
        return true;
    }

    /**
     * 合约资产跨链转入时调用的方法
     */
    public void crossChainTokenTransfer(@Required Address nrc20, @Required String from, @Required Address to, @Required BigInteger value, @Required Integer chainId, @Required Integer assetsId) {
        onlySystem();
        // 检查是否有足够的跨链转出的token
        String key = chainId + "-" + assetsId;
        NRC20Amount nrc20Amount = nrc20CrossChainTransferOutMap.get(key);
        require(nrc20Amount != null, "not exist token, key: ["+key+"] ");
        require(nrc20.equals(nrc20Amount.getNrc20()), "NRC20-Address does not match, input nrc20: ["+nrc20+"], storage nrc20: ["+nrc20Amount.getNrc20().toString()+"]");
        require(nrc20Amount.getValue().compareTo(value) >= 0, "not enough token, input value: ["+value+"], storage value: ["+nrc20Amount.getValue()+"]");
        // 更新记录的token，减掉跨链转入的token
        nrc20Amount.subtractValue(value);
        // 转移token到相应地址上
        this.transferOtherNRC20(nrc20, to, value);
        // 发出合约资产跨链转入事件
        Utils.emit(new CrossChainTokenTransferInEvent(nrc20, from, to, value, chainId, assetsId));
    }

    public void transferOtherNRC20(Address nrc20, Address to, BigInteger value) {
        onlySystemOrOwner();
        require(!Msg.address().equals(nrc20), "Do nothing by yourself");
        require(nrc20.isContract(), "[" + nrc20.toString() + "] is not a contract address");
        String[][] args = new String[][]{new String[]{Msg.address().toString()}};
        String balance = nrc20.callWithReturnValue("balanceOf", "", args, BigInteger.ZERO);
        require(new BigInteger(balance).compareTo(value) >= 0, "No enough balance");

        String methodName = "transfer";
        String[][] args1 = new String[][]{
                new String[]{to.toString()},
                new String[]{value.toString()}};
        nrc20.call(methodName, "(Address to, BigInteger value) return boolean", args1, BigInteger.ZERO);
    }

    private void onlySystem() {
        require(Msg.sender() == null, "Only system can execute it.");
    }

    private void onlySystemOrOwner() {
        require(Msg.sender() == null || Msg.sender().equals(owner), "Only system or the owner of the contract can execute it.");
    }

    public void dataMigration(String[] keys, String[] nrc20s, String[] values) {
        onlyOwner();
        int keyLength = keys.length;
        int nrc20Length = nrc20s.length;
        int valueLength = values.length;
        require(keyLength == nrc20Length, "array length not match[0]");
        require(valueLength == nrc20Length, "array length not match[1]");
        BigInteger value;
        String nrc20;
        for(int i = 0; i < keyLength; i++) {
            nrc20 = nrc20s[i];
            value = new BigInteger(values[i]);
            nrc20CrossChainTransferOutMap.put(keys[i], new NRC20Amount(new Address(nrc20), value));
        }
    }

    @View
    public String viewNrc20Map() {
        return Utils.obj2Json(nrc20CrossChainTransferOutMap);
    }

}