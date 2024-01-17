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

package io.nuls.token.nrc1155.interfaces;

import io.nuls.contract.sdk.Address;
import io.nuls.contract.sdk.Event;
import io.nuls.contract.sdk.annotation.Required;
import io.nuls.contract.sdk.annotation.View;

import java.math.BigInteger;

public interface INRC1155 {

    /**
     @notice Transfers `_value` amount of an `_id` from the `_from` address to the `_to` address specified (with safety call).
     @dev Caller must be approved to manage the tokens being transferred out of the `_from` account (see "Approval" section of the standard).
     MUST revert if `_to` is the zero address.
     MUST revert if balance of holder for token `_id` is lower than the `_value` sent.
     MUST revert on any other error.
     MUST emit the `TransferSingle` event to reflect the balance change (see "Safe Transfer Rules" section of the standard).
     After the above conditions are met, this function MUST check if `_to` is a smart contract (e.g. code size > 0). If so, it MUST call `onERC1155Received` on `_to` and act appropriately (see "Safe Transfer Rules" section of the standard).
     @param _from    Source address
     @param _to      Target address
     @param _id      ID of the token type
     @param _value   Transfer amount
     @param _data    Additional data with no specified format, MUST be sent unaltered in call to `onERC1155Received` on `_to`
    */
    void safeTransferFrom(@Required Address _from, @Required Address _to, @Required BigInteger _id, @Required BigInteger _value, @Required String _data);


    /**
     @notice Transfers `_values` amount(s) of `_ids` from the `_from` address to the `_to` address specified (with safety call).
     @dev Caller must be approved to manage the tokens being transferred out of the `_from` account (see "Approval" section of the standard).
     MUST revert if `_to` is the zero address.
     MUST revert if length of `_ids` is not the same as length of `_values`.
     MUST revert if any of the balance(s) of the holder(s) for token(s) in `_ids` is lower than the respective amount(s) in `_values` sent to the recipient.
     MUST revert on any other error.
     MUST emit `TransferSingle` or `TransferBatch` event(s) such that all the balance changes are reflected (see "Safe Transfer Rules" section of the standard).
     Balance changes and events MUST follow the ordering of the arrays (_ids[0]/_values[0] before _ids[1]/_values[1], etc).
     After the above conditions for the transfer(s) in the batch are met, this function MUST check if `_to` is a smart contract (e.g. code size > 0). If so, it MUST call the relevant `ERC1155TokenReceiver` hook(s) on `_to` and act appropriately (see "Safe Transfer Rules" section of the standard).
     @param _from    Source address
     @param _to      Target address
     @param _ids     IDs of each token type (order and length must match _values array)
     @param _values  Transfer amounts per token type (order and length must match _ids array)
     @param _data    Additional data with no specified format, MUST be sent unaltered in call to the `ERC1155TokenReceiver` hook(s) on `_to`
    */
    void safeBatchTransferFrom(@Required Address _from, @Required Address _to, @Required String[] _ids, @Required String[] _values, @Required String _data);

    /**
     @notice Get the balance of an account's tokens.
     @param _owner  The address of the token holder
     @param _id     ID of the token
     @return        The _owner's balance of the token type requested
    */
    @View
    BigInteger balanceOf(@Required Address _owner, @Required BigInteger _id);

    /**
     @notice Get the balance of multiple account/token pairs
     @param _owners The addresses of the token holders
     @param _ids    ID of the tokens
     @return        The _owner's balance of the token types requested (i.e. balance for each (owner, id) pair)
     */
    @View
    BigInteger[] balanceOfBatch(@Required String[] _owners, @Required String[] _ids);

    /**
     @notice Enable or disable approval for a third party ("operator") to manage all of the caller's tokens.
     @dev MUST emit the ApprovalForAll event on success.
     @param _operator  Address to add to the set of authorized operators
     @param _approved  True if the operator is approved, false to revoke approval
     */
    void setApprovalForAll(@Required Address _operator, @Required boolean _approved);

    /**
     @notice Queries the approval status of an operator for a given owner.
     @param _owner     The owner of the tokens
     @param _operator  Address of authorized operator
     @return           True if the operator is approved, false if not
    */
    @View
    boolean isApprovedForAll(@Required Address _owner, @Required Address _operator);

    /**
     @dev Either `TransferSingle` or `TransferBatch` MUST emit when tokens are transferred, including zero value transfers as well as minting or burning (see "Safe Transfer Rules" section of the standard).
     The `_operator` argument MUST be the address of an account/contract that is approved to make the transfer (SHOULD be msg.sender).
     The `_from` argument MUST be the address of the holder whose balance is decreased.
     The `_to` argument MUST be the address of the recipient whose balance is increased.
     The `_id` argument MUST be the token type being transferred.
     The `_value` argument MUST be the number of tokens the holder balance is decreased by and match what the recipient balance is increased by.
     When minting/creating tokens, the `_from` argument MUST be set to `null` (i.e. zero address).
     When burning/destroying tokens, the `_to` argument MUST be set to `null` (i.e. zero address).
     */
    class TransferSingle implements Event {
        private Address _operator;
        private Address _from;
        private Address _to;
        private BigInteger _id;
        private BigInteger _value;

        public TransferSingle(Address _operator, Address _from, Address _to, BigInteger _id, BigInteger _value) {
            this._operator = _operator;
            this._from = _from;
            this._to = _to;
            this._id = _id;
            this._value = _value;
        }
    }

    /**
     @dev Either `TransferSingle` or `TransferBatch` MUST emit when tokens are transferred, including zero value transfers as well as minting or burning (see "Safe Transfer Rules" section of the standard).
     The `_operator` argument MUST be the address of an account/contract that is approved to make the transfer (SHOULD be msg.sender).
     The `_from` argument MUST be the address of the holder whose balance is decreased.
     The `_to` argument MUST be the address of the recipient whose balance is increased.
     The `_ids` argument MUST be the list of tokens being transferred.
     The `_values` argument MUST be the list of number of tokens (matching the list and order of tokens specified in _ids) the holder balance is decreased by and match what the recipient balance is increased by.
     When minting/creating tokens, the `_from` argument MUST be set to `0x0` (i.e. zero address).
     When burning/destroying tokens, the `_to` argument MUST be set to `0x0` (i.e. zero address).
     */
    class TransferBatch implements Event {
        private Address _operator;
        private Address _from;
        private Address _to;
        private BigInteger[] _ids;
        private BigInteger[] _values;

        public TransferBatch(Address _operator, Address _from, Address _to, BigInteger[] _ids, BigInteger[] _values) {
            this._operator = _operator;
            this._from = _from;
            this._to = _to;
            this._ids = _ids;
            this._values = _values;
        }
    }

    /**
     @dev MUST emit when approval for a second party/operator address to manage all tokens for an owner address is enabled or disabled (absence of an event assumes disabled).
     */
    class ApprovalForAll implements Event {
        private Address _owner;
        private Address _operator;
        private Boolean _approved;

        public ApprovalForAll(Address _owner, Address _operator, Boolean _approved) {
            this._owner = _owner;
            this._operator = _operator;
            this._approved = _approved;
        }
    }

    /**
     @dev MUST emit when the URI is updated for a token ID.
     URIs are defined in RFC 3986.
     The URI MUST point to a JSON file that conforms to the "ERC-1155 Metadata URI JSON Schema".
     */
    class URI implements Event {
        private String _value;
        private BigInteger _id;

        public URI(String _value, BigInteger _id) {
            this._value = _value;
            this._id = _id;
        }
    }

}
