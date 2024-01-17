/**
 * MIT License
 * <p>
 * Copyright (c) 2017-2018 nuls.io
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package io.nuls.token.base;

import io.nuls.contract.sdk.Address;
import io.nuls.contract.sdk.Msg;
import io.nuls.contract.sdk.annotation.Required;
import io.nuls.contract.sdk.annotation.View;
import io.nuls.token.interfaces.INRC721;
import io.nuls.token.model.Counter;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import static io.nuls.contract.sdk.Utils.emit;
import static io.nuls.contract.sdk.Utils.require;

/**
 * @author: PierreLuo
 * @date: 2019-06-04
 */
public class NRC721Base extends NRC165Base implements INRC721 {

    private Map<BigInteger, Address> tokenOwner = new HashMap<BigInteger, Address>();
    private Map<BigInteger, Address> tokenApprovals = new HashMap<BigInteger, Address>();
    private Map<Address, Counter> ownedTokensCount = new HashMap<Address, Counter>();
    private Map<Address, Map<Address, Boolean>> operatorApprovals = new HashMap<Address, Map<Address, Boolean>>();

    public NRC721Base() {
        super.registerInterface("INRC721");
    }

    @Override
    @View
    public int balanceOf(@Required Address owner) {
        Counter balance = ownedTokensCount.get(owner);
        if(balance == null) {
            return 0;
        }
        return balance.current();
    }

    @Override
    @View
    public Address ownerOf(@Required BigInteger tokenId) {
        Address address = tokenOwner.get(tokenId);
        require(address != null, "NRC721: owner query for nonexistent token");
        return address;
    }

    @Override
    public void safeTransferFrom(@Required Address from, @Required Address to, @Required BigInteger tokenId, @Required String data) {
        transferFrom(from, to, tokenId);
        // checkOnNRC721Received 的作用是当to是合约地址时，那么to这个合约必须实现`onNRC721Received`函数 / data 的作用是附加备注
        require(checkOnNRC721Received(from, to, tokenId, data), "NRC721: transfer to non NRC721Receiver implementer");

    }

    @Override
    public void safeTransferFrom(@Required Address from, @Required Address to, @Required BigInteger tokenId) {
        safeTransferFrom(from, to, tokenId, "");
    }

    @Override
    public void transferFrom(@Required Address from, @Required Address to, @Required BigInteger tokenId) {
        require(isApprovedOrOwner(Msg.sender(), tokenId), "NRC721: transfer caller is not owner nor approved");

        transferFromBase(from, to, tokenId);
    }

    @Override
    public void approve(@Required Address to, @Required BigInteger tokenId) {
        Address owner = ownerOf(tokenId);
        require(!to.equals(owner), "NRC721: approval to current owner");

        require(Msg.sender().equals(owner) || isApprovedForAll(owner, Msg.sender()),
                "NRC721: approve caller is not owner nor approved for all"
        );

        tokenApprovals.put(tokenId, to);
        emit(new Approval(owner, to, tokenId));
    }

    @Override
    public void setApprovalForAll(@Required Address operator, @Required boolean approved) {
        Address sender = Msg.sender();
        require(!operator.equals(sender), "NRC721: approve to caller");

        Map<Address, Boolean> approvalsMap = operatorApprovals.get(sender);
        if(approvalsMap == null) {
            approvalsMap = new HashMap<Address, Boolean>();
            operatorApprovals.put(sender, approvalsMap);
        }
        approvalsMap.put(operator, approved);
        emit(new ApprovalForAll(sender, operator, approved));
    }

    @Override
    @View
    public Address getApproved(@Required BigInteger tokenId) {
        require(exists(tokenId), "NRC721: approved query for nonexistent token");

        return tokenApprovals.get(tokenId);
    }

    @Override
    @View
    public boolean isApprovedForAll(@Required Address owner, @Required Address operator) {
        Map<Address, Boolean> approvalsMap = operatorApprovals.get(owner);
        if(approvalsMap == null) {
            return false;
        }
        Boolean isApproved = approvalsMap.get(operator);
        if(isApproved == null) {
            return false;
        }
        return isApproved;
    }

    protected boolean checkOnNRC721Received(Address from, Address to, BigInteger tokenId, String data) {
        if(!to.isContract()) {
            return true;
        }
        String[][] args = new String[][]{
                new String[]{Msg.sender().toString()},
                new String[]{from.toString()},
                new String[]{tokenId.toString()},
                new String[]{data}};
        String returnValue = to.callWithReturnValue("onNRC721Received", null, args, BigInteger.ZERO);
        return Boolean.valueOf(returnValue);
    }

    protected boolean exists(BigInteger tokenId) {
        Address owner = tokenOwner.get(tokenId);
        return owner != null;
    }

    protected boolean isApprovedOrOwner(Address spender, BigInteger tokenId) {
        require(exists(tokenId), "NRC721: operator query for nonexistent token");
        Address owner = ownerOf(tokenId);
        return (spender.equals(owner) || spender.equals(getApproved(tokenId)) || isApprovedForAll(owner, spender));
    }

    protected void transferFromBase(Address from, Address to, BigInteger tokenId) {
        require(ownerOf(tokenId).equals(from), "NRC721: transfer of token that is not own");

        clearApproval(tokenId);

        ownedTokensCount.get(from).decrement();
        Counter counter = ownedTokensCount.get(to);
        if(counter == null) {
            counter = new Counter();
            ownedTokensCount.put(to, counter);
        }
        counter.increment();

        tokenOwner.put(tokenId, to);

        emit(new Transfer(from, to, tokenId));
    }

    protected void mintBase(Address to, BigInteger tokenId) {
        require(!exists(tokenId), "NRC721: token already minted");

        tokenOwner.put(tokenId, to);
        Counter counter = ownedTokensCount.get(to);
        if(counter == null) {
            counter = new Counter();
            ownedTokensCount.put(to, counter);
        }
        counter.increment();

        emit(new Transfer(null, to, tokenId));
    }

    protected void burnBase(Address owner, BigInteger tokenId) {
        require(ownerOf(tokenId).equals(owner), "NRC721: burn of token that is not own");

        clearApproval(tokenId);

        ownedTokensCount.get(owner).decrement();
        tokenOwner.remove(tokenId);

        emit(new Transfer(owner, null, tokenId));
    }

    protected void burnBase(BigInteger tokenId) {
        burnBase(ownerOf(tokenId), tokenId);
    }

    private void clearApproval(BigInteger tokenId) {
        tokenApprovals.remove(tokenId);
    }
}
