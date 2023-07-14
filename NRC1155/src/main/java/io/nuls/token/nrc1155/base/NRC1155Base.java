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
package io.nuls.token.nrc1155.base;

import io.nuls.contract.sdk.Address;
import io.nuls.contract.sdk.Msg;
import io.nuls.contract.sdk.annotation.Required;
import io.nuls.contract.sdk.annotation.View;
import io.nuls.token.nrc1155.interfaces.INRC1155;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import static io.nuls.contract.sdk.Utils.*;

/**
 * @author: PierreLuo
 * @date: 2023/4/23
 */
public class NRC1155Base extends NRC165Base implements INRC1155 {

    private Map<BigInteger, Map<Address, BigInteger>> _balances = new HashMap<BigInteger, Map<Address, BigInteger>>();
    private Map<Address, Map<Address, Boolean>> _operatorApprovals = new HashMap<Address, Map<Address, Boolean>>();
    private String _uri;

    public NRC1155Base(String uri_) {
        super.registerInterface("INRC1155");
        _setURI(uri_);
    }

    private void _setURI(String newuri) {
        _uri = newuri;
    }

    @View
    public String uri() {
        return _uri;
    }

    @Override
    public void safeTransferFrom(@Required Address from, @Required Address to, @Required BigInteger id, @Required BigInteger amount, @Required String data) {
        require(
                from.equals(_msgSender()) || isApprovedForAll(from, _msgSender()),
                "ERC1155: caller is not token owner or approved"
        );
        _safeTransferFrom(from, to, id, amount, data);
    }

    @Override
    public void safeBatchTransferFrom(Address from, Address to, String[] ids, String[] amounts, String data) {
        require(
                from.equals(_msgSender()) || isApprovedForAll(from, _msgSender()),
                "ERC1155: caller is not token owner or approved"
        );
        _safeBatchTransferFrom(from, to, str2bigArray(ids), str2bigArray(amounts), data);
    }

    @Override
    @View
    public BigInteger balanceOf(@Required Address account, @Required BigInteger id) {
        Map<Address, BigInteger> map = _balances.get(id);
        if (map == null) {
            return BigInteger.ZERO;
        }
        BigInteger balance = map.get(account);
        return balance == null ? BigInteger.ZERO : balance;
    }

    @Override
    @View
    public BigInteger[] balanceOfBatch(String[] accounts, String[] ids) {
        require(accounts.length == ids.length, "ERC1155: accounts and ids length mismatch");
        BigInteger[] batchBalances = new BigInteger[accounts.length];

        for (int i = 0; i < accounts.length; ++i) {
            batchBalances[i] = balanceOf(new Address(accounts[i]), new BigInteger(ids[i]));
        }

        return batchBalances;
    }

    @Override
    public void setApprovalForAll(Address operator, boolean approved) {
        _setApprovalForAll(_msgSender(), operator, approved);
    }

    @Override
    public boolean isApprovedForAll(Address _owner, Address _operator) {
        return _operatorApprovalsOf(_owner, _operator);
    }

    /**
     * @dev Creates `amount` tokens of token type `id`, and assigns them to `to`.
     *
     * Emits a {TransferSingle} event.
     *
     * Requirements:
     *
     * - `to` cannot be the zero address.
     * - If `to` refers to a smart contract, it must implement {IERC1155Receiver-onERC1155Received} and return the
     * acceptance magic value.
     */
    protected void _mint(Address to, BigInteger id, BigInteger amount, String data) {
        require(to != null, "ERC1155: mint to the empty address");

        Address operator = _msgSender();
        BigInteger[] ids = new BigInteger[]{id};
        BigInteger[] amounts = new BigInteger[]{amount};

        _beforeTokenTransfer(operator, null, to, ids, amounts, data);

        _putBalance(to, id, balanceOf(to, id).add(amount));
        emit(new TransferSingle(operator, null, to, id, amount));

        _afterTokenTransfer(operator, null, to, ids, amounts, data);

        _doSafeTransferAcceptanceCheck(operator, null, to, id, amount, data);
    }

    /**
     * @dev xref:ROOT:erc1155.adoc#batch-operations[Batched] version of {_mint}.
     *
     * Emits a {TransferBatch} event.
     *
     * Requirements:
     *
     * - `ids` and `amounts` must have the same length.
     * - If `to` refers to a smart contract, it must implement {IERC1155Receiver-onERC1155BatchReceived} and return the
     * acceptance magic value.
     */
    protected void _mintBatch(
            Address to,
            BigInteger[] ids,
            BigInteger[] amounts,
            String data
    ) {
        require(to != null, "ERC1155: mint to the empty address");
        require(ids.length == amounts.length, "ERC1155: ids and amounts length mismatch");

        Address operator = _msgSender();

        _beforeTokenTransfer(operator, null, to, ids, amounts, data);

        for (int i = 0; i < ids.length; i++) {
            _putBalance(to, ids[i], balanceOf(to, ids[i]).add(amounts[i]));
        }

        emit(new TransferBatch(operator, null, to, ids, amounts));

        _afterTokenTransfer(operator, null, to, ids, amounts, data);

        _doSafeBatchTransferAcceptanceCheck(operator, null, to, ids, amounts, data);
    }

    /**
     * @dev Destroys `amount` tokens of token type `id` from `from`
     *
     * Emits a {TransferSingle} event.
     *
     * Requirements:
     *
     * - `from` cannot be the zero address.
     * - `from` must have at least `amount` tokens of token type `id`.
     */
    protected void _burn(Address from, BigInteger id, BigInteger amount) {
        require(from != null, "ERC1155: burn from the empty address");

        Address operator = _msgSender();
        BigInteger[] ids = new BigInteger[]{id};
        BigInteger[] amounts = new BigInteger[]{amount};

        _beforeTokenTransfer(operator, from, null, ids, amounts, "");

        BigInteger fromBalance = balanceOf(from, id);
        require(fromBalance.compareTo(amount) >= 0, "ERC1155: burn amount exceeds balance");
        _putBalance(from, id, fromBalance.subtract(amount));

        emit(new TransferSingle(operator, from, null, id, amount));

        _afterTokenTransfer(operator, from, null, ids, amounts, "");
    }

    /**
     * @dev xref:ROOT:erc1155.adoc#batch-operations[Batched] version of {_burn}.
     *
     * Emits a {TransferBatch} event.
     *
     * Requirements:
     *
     * - `ids` and `amounts` must have the same length.
     */
    protected void  _burnBatch(Address from, BigInteger[] ids, BigInteger[] amounts) {
        require(from != null, "ERC1155: burn from the empty address");
        require(ids.length == amounts.length, "ERC1155: ids and amounts length mismatch");

        Address operator = _msgSender();

        _beforeTokenTransfer(operator, from, null, ids, amounts, "");

        for (int i = 0; i < ids.length; i++) {
            BigInteger id = ids[i];
            BigInteger amount = amounts[i];

            BigInteger fromBalance = balanceOf(from, id);
            require(fromBalance.compareTo(amount) >= 0, "ERC1155: burn amount exceeds balance");
            _putBalance(from, id, fromBalance.subtract(amount));
        }

        emit(new TransferBatch(operator, from, null, ids, amounts));

        _afterTokenTransfer(operator, from, null, ids, amounts, "");
    }

    protected Address _msgSender() {
        return Msg.sender();
    }

    /**
     * @dev Approve `operator` to operate on all of `owner` tokens
     * <p>
     * Emits an {ApprovalForAll} event.
     */
    protected void _setApprovalForAll(Address owner, Address operator, Boolean approved) {
        require(!owner.equals(operator), "ERC1155: setting approval status for self");
        _putOperatorApprovals(owner, operator, approved);
        emit(new ApprovalForAll(owner, operator, approved));
    }

    protected void _putBalance(Address account, BigInteger id, BigInteger amount) {
        Map<Address, BigInteger> map = _balances.get(id);
        if (map == null) {
            map = new HashMap<Address, BigInteger>();
            _balances.put(id, map);
        }
        map.put(account, amount);
    }

    protected void _putOperatorApprovals(Address owner, Address operator, Boolean approved) {
        Map<Address, Boolean> map = _operatorApprovals.get(owner);
        if (map == null) {
            map = new HashMap<Address, Boolean>();
            _operatorApprovals.put(owner, map);
        }
        map.put(operator, approved);
    }

    protected Boolean _operatorApprovalsOf(Address owner, Address operator) {
        Map<Address, Boolean> map = _operatorApprovals.get(owner);
        if (map == null) {
            return false;
        }
        Boolean result = map.get(operator);
        return result == null ? false : result;
    }

    private void _safeTransferFrom(Address from, Address to, BigInteger id, BigInteger amount, String data) {

        Address operator = _msgSender();
        BigInteger[] ids = new BigInteger[]{id};
        BigInteger[] amounts = new BigInteger[]{amount};

        _beforeTokenTransfer(operator, from, to, ids, amounts, data);

        BigInteger fromBalance = balanceOf(from, id);
        require(fromBalance.compareTo(amount) >= 0, "ERC1155: insufficient balance for transfer");
        _putBalance(from, id, fromBalance.subtract(amount));
        _putBalance(to, id, balanceOf(to, id).add(amount));

        emit(new TransferSingle(operator, from, to, id, amount));

        _afterTokenTransfer(operator, from, to, ids, amounts, data);

        _doSafeTransferAcceptanceCheck(operator, from, to, id, amount, data);
    }

    private void _doSafeTransferAcceptanceCheck(Address operator, Address from, Address to, BigInteger id, BigInteger amount, String data) {
        if (!to.isContract()) {
            return;
        }
        String[][] args = new String[][]{
                new String[]{operator.toString()},
                new String[]{from.toString()},
                new String[]{id.toString()},
                new String[]{amount.toString()},
                new String[]{data}};
        String returnValue = to.callWithReturnValue("onERC1155Received", null, args, BigInteger.ZERO);
        Boolean check = Boolean.valueOf(returnValue);
        if (!check) {
            revert("ERC1155: transfer to non-ERC1155Receiver implementer");
        }
    }

    protected void _afterTokenTransfer(Address operator, Address from, Address to, BigInteger[] ids, BigInteger[] amounts, String data) {
    }

    protected void _beforeTokenTransfer(Address operator, Address from, Address to, BigInteger[] ids, BigInteger[] amounts, String data) {
    }

    protected void _safeBatchTransferFrom(Address from, Address to, BigInteger[] ids, BigInteger[] amounts, String data) {
        require(ids.length == amounts.length, "ERC1155: ids and amounts length mismatch");

        Address operator = _msgSender();

        _beforeTokenTransfer(operator, from, to, ids, amounts, data);

        for (int i = 0; i < ids.length; ++i) {
            BigInteger id = ids[i];
            BigInteger amount = amounts[i];

            BigInteger fromBalance = balanceOf(from, id);
            require(fromBalance.compareTo(amount) >= 0, "ERC1155: insufficient balance for transfer");
            _putBalance(from, id, fromBalance.subtract(amount));
            _putBalance(to, id, balanceOf(to, id).add(amount));
        }

        emit(new TransferBatch(operator, from, to, ids, amounts));

        _afterTokenTransfer(operator, from, to, ids, amounts, data);

        _doSafeBatchTransferAcceptanceCheck(operator, from, to, ids, amounts, data);
    }

    private void _doSafeBatchTransferAcceptanceCheck(Address operator, Address from, Address to, BigInteger[] ids, BigInteger[] amounts, String data) {
        if (!to.isContract()) {
            return;
        }
        String[][] args = new String[][]{
                new String[]{operator.toString()},
                new String[]{from.toString()},
                big2strArray(ids),
                big2strArray(amounts),
                new String[]{data}};
        String returnValue = to.callWithReturnValue("onERC1155BatchReceived", null, args, BigInteger.ZERO);
        Boolean check = Boolean.valueOf(returnValue);
        if (!check) {
            revert("ERC1155: transfer to non-ERC1155Receiver implementer");
        }
    }

    protected String[] big2strArray(BigInteger[] bigArray) {
        String[] result = new String[bigArray.length];
        for (int i = 0; i < bigArray.length; i++) {
            result[i] = bigArray[i].toString();
        }
        return result;
    }

    protected BigInteger[] str2bigArray(String[] strArrays) {
        BigInteger[] result = new BigInteger[strArrays.length];
        for (int i = 0; i < strArrays.length; i++) {
            result[i] = new BigInteger(strArrays[i]);
        }
        return result;
    }
}
