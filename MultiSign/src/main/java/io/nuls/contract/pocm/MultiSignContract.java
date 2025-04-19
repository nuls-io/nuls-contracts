package io.nuls.contract.pocm;

import io.nuls.contract.pocm.event.*;
import io.nuls.contract.pocm.model.AssetInfo;
import io.nuls.contract.pocm.model.AssetView;
import io.nuls.contract.pocm.ownership.Ownable;
import io.nuls.contract.sdk.*;
import io.nuls.contract.sdk.annotation.JSONSerializable;
import io.nuls.contract.sdk.annotation.Payable;
import io.nuls.contract.sdk.annotation.PayableMultyAsset;
import io.nuls.contract.sdk.annotation.View;
import io.nuls.contract.sdk.token.AssetWrapper;
import io.nuls.contract.sdk.token.NRC20Wrapper;
import io.nuls.contract.sdk.token.Token;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static io.nuls.contract.sdk.Utils.emit;
import static io.nuls.contract.sdk.Utils.require;

public class MultiSignContract extends Ownable implements Contract {

    static final String MESSAGE_PREFIX = "\u0019Ethereum Signed Message:\n";
    private Set<String> managers = new HashSet<String>();
    private Set<String> operators = new HashSet<String>();
    private Set<String> completedTxs = new HashSet<String>();
    private Map<String, AssetInfo> assetMap = new HashMap<String, AssetInfo>();
    // Minimum signature ratio 71%
    private int RATIO = 71;
    // denominator
    private int DENOMINATOR = 100;
    private int MIN_SIGNATURES = 1;

    private void onlyOperator() {
        require(operators.contains(Msg.sender().toString()), "Only the operators of the contract can execute it.");
    }

    public MultiSignContract() {
        if (Msg.sender().toString().startsWith("NULS")) {
            operators.add("NULSd6HgZfCjA5zSx6Z4U2fLrWBaxDEvFsFPF");
            managers.add("NULSd6Hghz2H5vrFKndj1QnzJ33sonT9o81uY");
            managers.add("NULSd6HghrKLxNMSAjPGhnz6QdsVRAUL5Zc8v");
            managers.add("NULSd6Hgf1cY1WpZor9R1FWdqnNHJdMUX2ZpR");
            managers.add("NULSd6Hge3Sw9C6YqNxGGqwWoY45ASA7ic3RS");
            managers.add("NULSd6HgbdES1q9LYC5tnPBwqUGTHQryzzADN");
            managers.add("NULSd6HgX9J8Gbn8Uurq3psiFagzvbt3JzEGP");
            managers.add("NULSd6HghtRtCpoUsCWDCqbeFZ5fLt3oynkYQ");
        } else {
            operators.add("tNULSeBaN2zmez6T5k4o67iS3M9StrAL5iKSLv");
            managers.add("tNULSeBaN3ZgBdnMqfuqBNobiwEw8SMzrFjfdb");
            managers.add("tNULSeBaN7VeQLM3LS5gHEKx1uXSkgKcPLVcLw");
            managers.add("tNULSeBaMz3aA5NNyc8hY8GAWz3n2cpSASiP3y");
            managers.add("tNULSeBaN3nhvHCZ7oXk3MNVpCpnE7NjqkTv8R");
            managers.add("tNULSeBaN8s7cJwAfa9oQN5Y6vcJc3Xhssq4tw");
            managers.add("tNULSeBaNAm6ptEes3HuFQsjzGsEm3JF9SGc6c");
            managers.add("tNULSeBaMuozJq3gg8xB6ffb2urQNBQ3taC4Fj");
        }
        MIN_SIGNATURES = this.calMinSignatures(managers.size());
    }

    @Payable
    @PayableMultyAsset
    public void amountEnter(BigInteger nrc20Amount, Address nrc20Contract) {
        String pocm = Msg.sender().toString();
        AssetInfo assetInfo = assetMap.get(pocm);
        if (assetInfo == null) {
            assetInfo = new AssetInfo();
            assetMap.put(pocm, assetInfo);
        }
        if (nrc20Amount.compareTo(BigInteger.ZERO) > 0) {
            NRC20Wrapper nrc20Wrapper = new NRC20Wrapper(nrc20Contract);
            nrc20Wrapper.transferFrom(Msg.sender(), Msg.address(), nrc20Amount);
            assetInfo.addNrc20(nrc20Amount, nrc20Contract);
            // 资产转入事件, nrc20
            emit(new DepositNrc20Funds(pocm, nrc20Amount, nrc20Contract.toString()));
        }
        BigInteger main = Msg.value();
        if (main.compareTo(BigInteger.ZERO) > 0) {
            assetInfo.addNuls(main);
            // 资产转入事件, nuls
            emit(new DepositNulsFunds(pocm, main));
        }
        MultyAssetValue[] assetValues = Msg.multyAssetValues();
        if (assetValues == null) {
            return;
        }
        for (MultyAssetValue assetValue : assetValues) {
            BigInteger amount = assetValue.getValue();
            String assetKey = assetValue.getAssetChainId() + "-" + assetValue.getAssetId();
            assetInfo.addCrossAsset(amount, assetKey);
            // 资产转入事件, crossAsset
            emit(new DepositCrossAssetFunds(pocm, amount, assetKey));
        }
    }

    public void withdraw(String txKey, String sender, String nulsAmount, String tokenAmount,
                         String assetKey, String receiver,
                         String[] signatures, String[] pubs) {
        onlyOperator();
        require(!completedTxs.contains(txKey), "Transaction has been completed");
        String encodePacked = Utils.encodePacked(new String[]{txKey, sender, nulsAmount, tokenAmount, assetKey, receiver});
        this.validateSign(encodePacked, signatures, pubs);
        this.withdrawReal(sender, new BigInteger(nulsAmount), new BigInteger(tokenAmount), assetKey, new Address(receiver));
        completedTxs.add(txKey);
        emit(new TxComplete(txKey));
    }

    public void withdrawBatch(String txKey, String[] sender, String[] nulsAmount, String[] tokenAmount,
                              String[] assetKey, String[] receiver,
                              String[] signatures, String[] pubs) {
        onlyOperator();
        require(!completedTxs.contains(txKey), "Transaction has been completed");
        int length = sender.length, k = 1;
        String[] data = new String[1 + length * 5];
        data[0] = txKey;
        for (int i = 0; i < length; i++) {
            data[k++] = sender[i];
            data[k++] = nulsAmount[i];
            data[k++] = tokenAmount[i];
            data[k++] = assetKey[i];
            data[k++] = receiver[i];
        }
        String encodePacked = Utils.encodePacked(data);
        this.validateSign(encodePacked, signatures, pubs);
        for (int i = 0; i < length; i++) {
            this.withdrawReal(sender[i], new BigInteger(nulsAmount[i]), new BigInteger(tokenAmount[i]), assetKey[i], new Address(receiver[i]));
        }
        completedTxs.add(txKey);
        emit(new TxComplete(txKey));
    }

    public void addOperator(Address operator) {
        onlyOwner();
        operators.add(operator.toString());
    }

    public void removeOperator(Address operator) {
        onlyOwner();
        operators.remove(operator.toString());
    }

    @View
    public boolean isOperator(Address address) {
        return operators.contains(address.toString());
    }

    @View
    public boolean isManager(Address address) {
        return managers.contains(address.toString());
    }

    @View
    public boolean isCompletedTx(String txKey) {
        return completedTxs.contains(txKey);
    }

    @View
    public int getMinSignatures() {
        return MIN_SIGNATURES;
    }

    @View
    @JSONSerializable
    public AssetView getAsset(String sender, Address nrc20Address, String crossAssetKey) {
        AssetInfo assetInfo = assetMap.get(sender);
        if (assetInfo == null) {
            return new AssetView();
        }
        BigInteger nuls = assetInfo.getNuls();
        BigInteger nrc20Amount = BigInteger.ZERO;
        BigInteger crossAssetAmount = BigInteger.ZERO;
        if (nrc20Address != null) {
            nrc20Amount = assetInfo.getNrc20().get(nrc20Address);
        }
        nrc20Amount = nrc20Amount == null ? BigInteger.ZERO : nrc20Amount;
        if (crossAssetKey != null && crossAssetKey.length() > 0) {
            crossAssetAmount = assetInfo.getCrossAsset().get(crossAssetKey);
        }
        crossAssetAmount = crossAssetAmount == null ? BigInteger.ZERO : crossAssetAmount;
        return new AssetView(nuls, nrc20Amount, crossAssetAmount);
    }

    private int calMinSignatures(int managerCounts) {
        if (managerCounts == 0) {
            return 0;
        }
        int numerator = RATIO * managerCounts + DENOMINATOR - 1;
        return numerator / DENOMINATOR;
    }

    private void validateSign(String data, String[] signatures, String[] pubs) {
        int count = calcSign(data, signatures, pubs);
        require(count >= MIN_SIGNATURES, "Not Enough Signatures");
    }

    private int calcSign(String data, String[] signatures, String[] pubs) {
        int dataLength = data.length();
        String hexPrefix = this.hexEncode((MESSAGE_PREFIX + dataLength / 2).getBytes());
        String hash = keccakHash(hexPrefix + data);
        int count = 0;
        Set<String> checkDuplicate = new HashSet<String>();
        for (int i = 0, len = pubs.length; i < len; i++) {
            String pub = pubs[i];
            String address = Utils.getAddressByPublicKey(pub);
            if (!managers.contains(address)) continue;
            if (!checkDuplicate.add(address)) continue;
            if (!Utils.verifySignatureData(hash, signatures[i], pub)) continue;
            count++;
        }
        return count;
    }

    private String keccakHash(String hex) {
        return (String) Utils.invokeExternalCmd("keccak", new String[]{hex});
    }

    private String hexEncode(byte[] src) {
        StringBuilder sb = new StringBuilder(src.length * 2);
        int i;

        for (i = 0; i < src.length; i++) {
            if (((int) src[i] & 0xff) < 0x10) {
                sb.append("0");
            }
            sb.append(Long.toString((int) src[i] & 0xff, 16));
        }

        return sb.toString();
    }

    private void withdrawReal(String sender, BigInteger nulsAmount, BigInteger tokenAmount,
                              String assetKey, Address receiver) {
        AssetInfo assetInfo = assetMap.get(sender);
        require(assetInfo != null, "NULL POCM");
        if (nulsAmount.compareTo(BigInteger.ZERO) > 0) {
            assetInfo.subNuls(nulsAmount);
            receiver.transfer(nulsAmount);
            emit(new WithdrawNulsFunds(sender, receiver.toString(), nulsAmount));
        }
        if (tokenAmount.compareTo(BigInteger.ZERO) == 0) {
            return;
        }
        Token wrapper;
        if (assetKey.indexOf("-") != -1) {
            emit(new WithdrawCrossAssetFunds(sender, receiver.toString(), tokenAmount, assetKey));
            assetInfo.subCrossAsset(tokenAmount, assetKey);
            String[] split = assetKey.split("-");
            wrapper = new AssetWrapper(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
        } else {
            emit(new WithdrawNrc20Funds(sender, receiver.toString(), tokenAmount, assetKey));
            Address nrc20Address = new Address(assetKey);
            assetInfo.subNrc20(tokenAmount, nrc20Address);
            wrapper = new NRC20Wrapper(nrc20Address);
        }
        wrapper.transfer(receiver, tokenAmount);
    }
}