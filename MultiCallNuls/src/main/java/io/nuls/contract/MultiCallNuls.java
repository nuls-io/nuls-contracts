package io.nuls.contract;

import io.nuls.contract.sdk.Address;
import io.nuls.contract.sdk.Contract;
import io.nuls.contract.sdk.annotation.JSONSerializable;
import io.nuls.contract.sdk.annotation.View;

import java.math.BigInteger;
import java.util.Arrays;

public class MultiCallNuls implements Contract {

    public MultiCallNuls() {

    }

    @JSONSerializable
    @View
    public String[] aggregate(String[] contracts, String[] methods, String[] args) {
        return this.aggregateStrict(contracts, methods, args, true);
    }

    @JSONSerializable
    @View
    public String[] aggregateStrict(String[] contracts, String[] methods, String[] args, boolean strict) {
        int length = contracts.length;
        String[] results = new String[length];
        for (int i = 0; i < length; i++) {
            String arg = args[i];
            String[][] _args = null;
            if (arg != null && !arg.isEmpty()) {
                String[] split = arg.split(",");
                int len = split.length;
                _args = new String[len][];
                for (int k = 0; k < len; k++) {
                    String innerArg = split[k];
                    _args[k] = innerArg.split(":");
                }
            }
            String result;
            if (!strict) {
                try {
                    result = new Address(contracts[i]).callWithReturnValue(methods[i], null, _args, BigInteger.ZERO);
                } catch (Exception e) {
                    result = "";
                }
            } else {
                result = new Address(contracts[i]).callWithReturnValue(methods[i], null, _args, BigInteger.ZERO);
            }
            results[i] = result;
        }
        return results;
    }

    @JSONSerializable
    @View
    public String[] userNftBalances(String user, String[] nfts) {
        int length = nfts.length;
        String[] result = new String[length];
        String nft;
        for (int i = 0; i < length; i++) {
            nft = nfts[i];
            String count = new Address(nft).callWithReturnValue("balanceOf", null, new String[][]{new String[]{user}}, BigInteger.ZERO);
            result[i] = count;
        }
        return result;
    }

    @JSONSerializable
    @View
    public String[] userTokenBalancesAndNulsAvailable(String user, String[] nrc20s) {
        int length = nrc20s.length;
        String[] result = new String[length];
        String nrc20, balance;
        for (int i = 0; i < length; i++) {
            nrc20 = nrc20s[i];
            if (nrc20.length() == 0) {
                balance = new Address(user).balance().toString();
            } else {
                balance = new Address(nrc20).callWithReturnValue("balanceOf", null, new String[][]{new String[]{user}}, BigInteger.ZERO);
            }
            result[i] = balance;
        }
        return result;
    }

    @JSONSerializable
    @View
    public String[] userTokenBalancesAndNulsTotal(String user, String[] nrc20s) {
        int length = nrc20s.length;
        String[] result = new String[length];
        String nrc20, balance;
        for (int i = 0; i < length; i++) {
            nrc20 = nrc20s[i];
            if (nrc20.length() == 0) {
                balance = new Address(user).totalBalance().toString();
            } else {
                balance = new Address(nrc20).callWithReturnValue("balanceOf", null, new String[][]{new String[]{user}}, BigInteger.ZERO);
            }
            result[i] = balance;
        }
        return result;
    }

    @JSONSerializable
    @View
    public String[] userTokenBalancesAndAssetAvailable(String user, String[] nrc20s) {
        int addrLength = user.length();
        int length = nrc20s.length;
        String[] result = new String[length];
        String nrc20, balance;
        for (int i = 0; i < length; i++) {
            nrc20 = nrc20s[i];
            int nrc20Len = nrc20.length();
            if (nrc20Len == 0) {
                balance = new Address(user).balance().toString();
            } else if (nrc20Len < addrLength) {
                String[] split = nrc20.split("-");
                balance = new Address(user).balance(
                        Integer.parseInt(split[0]),
                        Integer.parseInt(split[1])
                ).toString();
            } else {
                balance = new Address(nrc20).callWithReturnValue("balanceOf", null, new String[][]{new String[]{user}}, BigInteger.ZERO);
            }
            result[i] = balance;
        }
        return result;
    }

    @JSONSerializable
    @View
    public String[] userTokenBalancesAndAssetTotal(String user, String[] nrc20s) {
        int addrLength = user.length();
        int length = nrc20s.length;
        String[] result = new String[length];
        String nrc20, balance;
        for (int i = 0; i < length; i++) {
            nrc20 = nrc20s[i];
            int nrc20Len = nrc20.length();
            if (nrc20Len == 0) {
                balance = new Address(user).totalBalance().toString();
            } else if (nrc20Len < addrLength) {
                String[] split = nrc20.split("-");
                balance = new Address(user).totalBalance(
                        Integer.parseInt(split[0]),
                        Integer.parseInt(split[1])
                ).toString();
            } else {
                balance = new Address(nrc20).callWithReturnValue("balanceOf", null, new String[][]{new String[]{user}}, BigInteger.ZERO);
            }
            result[i] = balance;
        }
        return result;
    }

}