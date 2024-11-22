package io.nuls.contract.token;

import io.nuls.contract.sdk.*;
import io.nuls.contract.sdk.annotation.Payable;
import io.nuls.contract.sdk.annotation.Required;
import io.nuls.contract.sdk.annotation.View;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static io.nuls.contract.sdk.Utils.emit;
import static io.nuls.contract.sdk.Utils.require;

public class CrossLockedToken extends Ownable implements Contract, Token {

    private final String name;
    private final String symbol;
    private final int decimals;
    private BigInteger totalSupply = BigInteger.ZERO;

    private Map<Address, BigInteger> balances = new HashMap<Address, BigInteger>();
    private Map<Address, Map<Address, BigInteger>> allowed = new HashMap<Address, Map<Address, BigInteger>>();

    private Map<Address, Map<Long, BigInteger>> lockedBalances = new HashMap<Address, Map<Long, BigInteger>>();
    /**
     * token跨链系统处理合约
     */
    private Address CROSS_TOKEN_SYSTEM_CONTRACT;

    @Payable
    public void _payable() {
        this.deposit(Msg.value());
    }

    @Override
    @View
    public String name() {
        return name;
    }

    @Override
    @View
    public String symbol() {
        return symbol;
    }

    @Override
    @View
    public int decimals() {
        return decimals;
    }

    @Override
    @View
    public BigInteger totalSupply() {
        return totalSupply;
    }

    public CrossLockedToken(@Required String name, @Required String symbol, @Required BigInteger initialAmount, @Required int decimals) {
        this.name = name;
        this.symbol = symbol;
        this.decimals = decimals;
        totalSupply = initialAmount.multiply(BigInteger.TEN.pow(decimals));
        balances.put(Msg.sender(), totalSupply);
        emit(new TransferEvent(null, Msg.sender(), totalSupply));
    }

    @Override
    @View
    public BigInteger allowance(@Required Address owner, @Required Address spender) {
        Map<Address, BigInteger> ownerAllowed = allowed.get(owner);
        if (ownerAllowed == null) {
            return BigInteger.ZERO;
        }
        BigInteger value = ownerAllowed.get(spender);
        if (value == null) {
            value = BigInteger.ZERO;
        }
        return value;
    }

    @Override
    public boolean transferFrom(@Required Address from, @Required Address to, @Required BigInteger value) {
        subtractAllowed(from, Msg.sender(), value);
        subtractBalance(from, value);
        addBalance(to, value);
        emit(new TransferEvent(from, to, value));
        return true;
    }

    /**
     * 从指定地址批量转账
     *
     * @param from
     * @param targets
     * @param values
     * @return
     */
    public boolean batchTransferFrom(@Required Address from, @Required String[] targets, @Required long[] values) {
        checkBatchData(targets, values);
        for (int i = 0; i < targets.length; i++) {
            Address to = new Address(targets[i]);
            BigInteger value = BigInteger.valueOf(values[i]);
            subtractAllowed(from, Msg.sender(), value);
            subtractBalance(from, value);
            addBalance(to, value);
            emit(new TransferEvent(from, to, value));
        }
        return true;
    }

    @Override
    @View
    public BigInteger balanceOf(@Required Address owner) {
        require(owner != null);
        //先检查锁定的Token是否可用
        updateLockedBalance(owner);

        BigInteger balance = balances.get(owner);
        if (balance == null) {
            balance = BigInteger.ZERO;
        }
        return balance;
    }

    /**
     * 查询账户锁定的Token数量
     *
     * @param owner
     * @return
     */
    @View
    public BigInteger lockedBalanceOf(@Required Address owner) {
        require(owner != null);
        //先检查锁定的Token是否可用
        updateLockedBalance(owner);
        BigInteger balance = BigInteger.ZERO;
        Map<Long, BigInteger> lockedBalance = lockedBalances.get(owner);
        if (lockedBalance != null && lockedBalance.size() > 0) {
            Iterator<BigInteger> iter = lockedBalance.values().iterator();
            while (iter.hasNext()) {
                balance = balance.add(iter.next());
            }
        }

        return balance;
    }


    @Override
    public boolean transfer(@Required Address to, @Required BigInteger value) {
        subtractBalance(Msg.sender(), value);
        addBalance(to, value);
        emit(new TransferEvent(Msg.sender(), to, value));
        return true;
    }


    /**
     * 批量转账
     *
     * @param targets
     * @param values
     * @return
     */
    public boolean batchTransfer(@Required String[] targets, @Required long[] values) {
        checkBatchData(targets, values);
        for (int i = 0; i < targets.length; i++) {
            Address to = new Address(targets[i]);
            BigInteger value = BigInteger.valueOf(values[i]);
            subtractBalance(Msg.sender(), value);
            addBalance(to, value);
            emit(new TransferEvent(Msg.sender(), to, value));
        }
        return true;
    }

    /**
     * 带锁定高度的转账
     *
     * @param to
     * @param value
     * @param lockedTime，为0表示不锁定
     * @return
     */
    public boolean transferLocked(@Required Address to, @Required BigInteger value, @Required long lockedTime) {
        subtractBalance(Msg.sender(), value);
        if (Block.timestamp() >= lockedTime) {
            addBalance(to, value);
        } else {
            //锁定中......
            addLockedBalance(to, value, lockedTime);
        }
        emit(new TransferEvent(Msg.sender(), to, value));
        return true;
    }

    @Override
    public boolean approve(@Required Address spender, @Required BigInteger value) {
        setAllowed(Msg.sender(), spender, value);
        emit(new ApprovalEvent(Msg.sender(), spender, value));
        return true;
    }

    public boolean increaseApproval(@Required Address spender, @Required BigInteger addedValue) {
        addAllowed(Msg.sender(), spender, addedValue);
        emit(new ApprovalEvent(Msg.sender(), spender, allowance(Msg.sender(), spender)));
        return true;
    }

    public boolean decreaseApproval(@Required Address spender, @Required BigInteger subtractedValue) {
        check(subtractedValue);
        BigInteger oldValue = allowance(Msg.sender(), spender);
        if (subtractedValue.compareTo(oldValue) > 0) {
            setAllowed(Msg.sender(), spender, BigInteger.ZERO);
        } else {
            subtractAllowed(Msg.sender(), spender, subtractedValue);
        }
        emit(new ApprovalEvent(Msg.sender(), spender, allowance(Msg.sender(), spender)));
        return true;
    }

    /**
     * 加载token跨链系统处理合约
     */
    public void loadCrossTokenSystemContract() {
        CROSS_TOKEN_SYSTEM_CONTRACT = new Address((String) Utils.invokeExternalCmd("sc_getCrossTokenSystemContract", null));
    }

    /**
     * token跨链转账
     *
     * @param to    平行链地址
     * @param value 转账金额
     * @return
     */
    @Payable
    public boolean transferCrossChain(@Required String to, @Required BigInteger value) {
        Address from = Msg.sender();
        // 授权系统合约可使用转出者的token资产(跨链部分)
        this.approve(crossTokenSystemContract(), value);

        // 调用系统合约，记录token资产跨链转出总额，生成合约资产跨链转账交易
        String methodName = "onNRC20Received";
        String[][] args = new String[][]{
                new String[]{from.toString()},
                new String[]{to},
                new String[]{value.toString()}};
        String returnValue = crossTokenSystemContract().callWithReturnValue(methodName, null, args, Msg.value());
        return Boolean.parseBoolean(returnValue);
    }

    private void addAllowed(Address address1, Address address2, BigInteger value) {
        BigInteger allowance = allowance(address1, address2);
        check(allowance);
        check(value);
        setAllowed(address1, address2, allowance.add(value));
    }

    private void subtractAllowed(Address address1, Address address2, BigInteger value) {
        BigInteger allowance = allowance(address1, address2);
        check(allowance, value, "Insufficient approved token");
        setAllowed(address1, address2, allowance.subtract(value));
    }

    private void setAllowed(Address address1, Address address2, BigInteger value) {
        check(value);
        Map<Address, BigInteger> address1Allowed = allowed.get(address1);
        if (address1Allowed == null) {
            address1Allowed = new HashMap<Address, BigInteger>();
            allowed.put(address1, address1Allowed);
        }
        address1Allowed.put(address2, value);
    }

    private void addBalance(Address address, BigInteger value) {
        //先检查锁定的Token是否可用
        updateLockedBalance(address);

        BigInteger balance = balanceOf(address);
        check(value, "The value must be greater than or equal to 0.");
        check(balance);
        balances.put(address, balance.add(value));
    }

    private void subtractBalance(Address address, BigInteger value) {
        //先检查锁定的Token是否可用
        updateLockedBalance(address);

        BigInteger balance = balanceOf(address);
        check(balance, value, "Insufficient balance of token.");
        balances.put(address, balance.subtract(value));
    }

    /**
     * 添加锁定记录
     *
     * @param address
     * @param value
     * @param lockedTime
     */
    private void addLockedBalance(Address address, BigInteger value, long lockedTime) {
        require(address != null);

        //先检查锁定的Token是否可用
        updateLockedBalance(address);

        check(value, "The value must be greater than or equal to 0.");
        Map<Long, BigInteger> lockedBalance = lockedBalances.get(address);
        if (lockedBalance == null) {
            lockedBalance = new HashMap<Long, BigInteger>();
        }
        if (lockedBalance.containsKey(lockedTime)) {
            BigInteger initBalance = lockedBalance.get(lockedTime);
            check(initBalance);
            lockedBalance.put(lockedTime, initBalance.add(value));
        } else {
            lockedBalance.put(lockedTime, value);
        }
        lockedBalances.put(address, lockedBalance);
    }

    /**
     * 检查锁定的Token是否达到可用
     *
     * @param address
     */
    private void updateLockedBalance(Address address) {
        long currentTime = Block.timestamp();
        Map<Long, BigInteger> lockedBalance = lockedBalances.get(address);
        if (lockedBalance != null && lockedBalance.size() > 0) {
            Iterator<Long> iter = lockedBalance.keySet().iterator();
            while (iter.hasNext()) {
                long lockedTime = iter.next();
                if (currentTime >= lockedTime) {
                    BigInteger availBalance = lockedBalance.get(lockedTime);
                    BigInteger balance = balances.get(address);
                    if (balance == null) {
                        balance = BigInteger.ZERO;
                    }
                    balances.put(address, balance.add(availBalance));
                    iter.remove();
                }
            }
        }
    }

    private void check(BigInteger value) {
        require(value != null && value.compareTo(BigInteger.ZERO) >= 0);
    }

    private void check(BigInteger value1, BigInteger value2) {
        check(value1);
        check(value2);
        require(value1.compareTo(value2) >= 0);
    }

    private void check(BigInteger value, String msg) {
        require(value != null && value.compareTo(BigInteger.ZERO) >= 0, msg);
    }

    private void check(BigInteger value1, BigInteger value2, String msg) {
        check(value1);
        check(value2);
        require(value1.compareTo(value2) >= 0, msg);
    }

    private void checkBatchData(String[] targets, long[] values) {
        require(targets.length == values.length, "The number of addresses and amounts for batch transfer should be the same");
    }

    private Address crossTokenSystemContract() {
        if (CROSS_TOKEN_SYSTEM_CONTRACT == null) {
            loadCrossTokenSystemContract();
        }
        return CROSS_TOKEN_SYSTEM_CONTRACT;
    }

    @Payable
    public boolean deposit(BigInteger v) {
        BigInteger val = Msg.value();
        Utils.require(val.compareTo(BigInteger.ZERO) > 0 && val.compareTo(v) >= 0, "wNULS: Value is invalid");
        BigInteger bL;
        if (this.balances.get(Msg.sender()) != null) {
            BigInteger iv = this.balances.get(Msg.sender());
            bL = iv.add(val);
        } else {
            bL = val;
        }

        this.totalSupply = this.totalSupply.add(val);
        this.balances.put(Msg.sender(), bL);
        Utils.emit(new TransferEvent(null, Msg.sender(), val));
        return true;
    }

    public boolean withdraw(BigInteger wad, Address recipient) {
        Utils.require(recipient != null, "Destination is invalid");
        BigInteger bOf = this.balances.get(Msg.sender());
        Utils.require(bOf.compareTo(wad) >= 0, "Balance lower than the amount you want to transfer");
        this.totalSupply = this.totalSupply.subtract(wad);
        BigInteger bOfWad = bOf.subtract(wad);
        this.balances.put(Msg.sender(), bOfWad);
        recipient.transfer(wad);
        Utils.emit(new TransferEvent(Msg.sender(), null, wad));
        return true;
    }
}
