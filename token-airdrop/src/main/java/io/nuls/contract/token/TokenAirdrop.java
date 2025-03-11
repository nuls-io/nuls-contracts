package io.nuls.contract.token;

import io.nuls.contract.ownership.Ownable;
import io.nuls.contract.sdk.Address;
import io.nuls.contract.sdk.Contract;
import io.nuls.contract.sdk.Msg;

import java.math.BigInteger;

import static io.nuls.contract.sdk.Utils.require;

/**
 * Token Airdrop
 */
public class TokenAirdrop extends Ownable implements Contract {

    public TokenAirdrop() {

    }

    public void airdrop(Address nrc20, String[] tos, String[] values) {
        onlyOwner();
        require(!Msg.address().equals(nrc20), "Do nothing by yourself");
        require(nrc20.isContract(), "[" + nrc20.toString() + "] is not a contract address");
        String[][] args;
        String methodName = "transfer";
        for (int i = 0, len = tos.length; i < len; i++) {
            args = new String[][]{
                    new String[]{tos[i]},
                    new String[]{values[i]}};
            nrc20.call(methodName, "", args, BigInteger.ZERO);
        }
    }

    public void airdropLocked(Address nrc20, String[] tos, String[] values, long time) {
        onlyOwner();
        require(!Msg.address().equals(nrc20), "Do nothing by yourself");
        require(nrc20.isContract(), "[" + nrc20.toString() + "] is not a contract address");
        String[][] args;
        String methodName = "transferLocked";
        String[] timeArg = new String[]{time + ""};
        for (int i = 0, len = tos.length; i < len; i++) {
            args = new String[][]{
                    new String[]{tos[i]},
                    new String[]{values[i]},
                    timeArg
            };
            nrc20.call(methodName, "", args, BigInteger.ZERO);
        }
    }

    public void airdropLockedDiffTime(Address nrc20, String[] tos, String[] values, String[] times) {
        onlyOwner();
        require(!Msg.address().equals(nrc20), "Do nothing by yourself");
        require(nrc20.isContract(), "[" + nrc20.toString() + "] is not a contract address");
        String[][] args;
        String methodName = "transferLocked";
        for (int i = 0, len = tos.length; i < len; i++) {
            args = new String[][]{
                    new String[]{tos[i]},
                    new String[]{values[i]},
                    new String[]{times[i]}
            };
            nrc20.call(methodName, "", args, BigInteger.ZERO);
        }
    }

    public void airdropFrom(Address nrc20, String from, String[] tos, String[] values) {
        onlyOwner();
        require(!Msg.address().equals(nrc20), "Do nothing by yourself");
        require(nrc20.isContract(), "[" + nrc20.toString() + "] is not a contract address");
        String[][] args;
        String methodName = "transferFrom";
        String[] fromArg = new String[]{from};
        for (int i = 0, len = tos.length; i < len; i++) {
            args = new String[][]{
                    fromArg,
                    new String[]{tos[i]},
                    new String[]{values[i]}};
            nrc20.call(methodName, "(Address from, Address to, BigInteger value) return boolean", args, BigInteger.ZERO);
        }
    }

    public void airdropFromSender(Address nrc20, String from, String[] tos, String[] values) {
        require(Msg.sender().equals(from), "Only the owner of the token can execute it.");
        require(!Msg.address().equals(nrc20), "Do nothing by yourself");
        require(nrc20.isContract(), "[" + nrc20.toString() + "] is not a contract address");
        String[][] args;
        String methodName = "transferFrom";
        String[] fromArg = new String[]{from};
        for (int i = 0, len = tos.length; i < len; i++) {
            args = new String[][]{
                    fromArg,
                    new String[]{tos[i]},
                    new String[]{values[i]}};
            nrc20.call(methodName, "(Address from, Address to, BigInteger value) return boolean", args, BigInteger.ZERO);
        }
    }

}