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

package io.nuls.token.interfaces;

import io.nuls.contract.sdk.Address;
import io.nuls.contract.sdk.Event;
import io.nuls.contract.sdk.annotation.Required;
import io.nuls.contract.sdk.annotation.View;

import java.math.BigInteger;

/**
 * @author: PierreLuo
 * @date: 2019-06-04
 */
public interface INRC721 {

    /**
     * Count all NFTs assigned to an owner
     * @param owner An address for whom to query the balance
     * @return The number of NFTs owned by `owner`, possibly zero
     */
    @View
    int balanceOf(@Required Address owner);

    /**
     * Find the owner of an NFT
     * @param tokenId The identifier for an NFT
     * @return The address of the owner of the NFT
     */
    @View
    Address ownerOf(@Required BigInteger tokenId);

    /**
     * Transfers the ownership of an NFT from one address to another address.
     * When transfer is complete, this function checks if `to` is a smart contract.
     * If so, it calls `onERC721Received` on `to` and throws if the return value is `false`.
     * @throws revert unless `Msg.sender()` is the current owner, an authorized
     *          operator, or the approved address for this NFT.
     * @throws revert if `from` is not the current owner.
     * @throws revert if `tokenId` is not a valid NFT.
     * @param from The current owner of the NFT
     * @param to The new owner
     * @param tokenId The NFT to transfer
     * @param data Additional data with no specified format, sent in call to `to`
     */
    void safeTransferFrom(@Required Address from, @Required Address to, @Required BigInteger tokenId, @Required String data);

    /**
     * Transfers the ownership of an NFT from one address to another address.
     * This works identically to the other function with an extra data parameter,
     *  except this function just sets data to "".
     * @param from The current owner of the NFT
     * @param to The new owner
     * @param tokenId The NFT to transfer
     */
    void safeTransferFrom(@Required Address from, @Required Address to, @Required BigInteger tokenId);

    /**
     * Transfer ownership of an NFT -- THE CALLER IS RESPONSIBLE
     *  TO CONFIRM THAT `to` IS CAPABLE OF RECEIVING NFTS OR ELSE
     *  THEY MAY BE PERMANENTLY LOST
     * @throws Revert unless `Msg.sender()` is the current owner, an authorized
     *  operator, or the approved address for this NFT.
     * @throws Revert if `from` is not the current owner.
     * @throws Revert if `tokenId` is not a valid NFT.
     * @param from The current owner of the NFT
     * @param to The new owner
     * @param tokenId The NFT to transfer
     */
    void transferFrom(@Required Address from, @Required Address to, @Required BigInteger tokenId);

    /**
     * Change or reaffirm the approved address for an NFT
     * @throws revert unless `Msg.sender()` is the current NFT owner, or an authorized
     *  operator of the current owner.
     * @param approved The new approved NFT controller
     * @param tokenId The NFT to approve
     */
    void approve(@Required Address to, @Required BigInteger tokenId);

    /**
     * Enable or disable approval for a third party ("operator") to manage
     *  all of `Msg.sender()`'s assets.
     * Emits the ApprovalForAll event. The contract MUST allow multiple operators per owner.
     * @param operator Address to add to the set of authorized operators
     * @param approved True if the operator is approved, false to revoke approval
     */
    void setApprovalForAll(@Required Address operator, @Required boolean approved);

    /**
     * Get the approved address for a single NFT.
     * @throws revert if there is none or if `tokenId` is not a valid NFT.
     * @param tokenId The NFT to find the approved address for
     * @return The approved address for this NFT.
     */
    @View
    Address getApproved(@Required BigInteger tokenId);

    /**
     * Query if an address is an authorized operator for another address
     * @param owner The address that owns the NFTs
     * @param operator The address that acts on behalf of the owner
     * @return True if `operator` is an approved operator for `owner`, false otherwise
     */
    @View
    boolean isApprovedForAll(@Required Address owner, @Required Address operator);

    /**
     * This emits when ownership of any NFT changes by any mechanism.
     *  This event emits when NFTs are created (`from` is NULL) and destroyed.
     *  At the time of any transfer, the approved address for that NFT (if any) is reset to none.
     *  <p>Exception: during contract creation, any number of NFTs may be created and assigned without emitting Transfer.</p>
     */
    class Transfer implements Event {
        private Address from;
        private Address to;
        private BigInteger tokenId;

        public Transfer(Address from, Address to, @Required BigInteger tokenId) {
            this.from = from;
            this.to = to;
            this.tokenId = tokenId;
        }

        public Address getFrom() {
            return from;
        }

        public void setFrom(Address from) {
            this.from = from;
        }

        public Address getTo() {
            return to;
        }

        public void setTo(Address to) {
            this.to = to;
        }

        public BigInteger getTokenId() {
            return tokenId;
        }

        public void setTokenId(BigInteger tokenId) {
            this.tokenId = tokenId;
        }
    }

    /**
     * This emits when the approved address for an NFT is changed or reaffirmed.
     *  When a Transfer event emits, this also indicates that the approved address for that NFT (if any) is reset to none.
     */
    class Approval implements Event {
        private Address owner;
        private Address approved;
        private BigInteger tokenId;

        public Approval(@Required Address owner, @Required Address approved, @Required BigInteger tokenId) {
            this.owner = owner;
            this.approved = approved;
            this.tokenId = tokenId;
        }

        public Address getOwner() {
            return owner;
        }

        public void setOwner(Address owner) {
            this.owner = owner;
        }

        public Address getApproved() {
            return approved;
        }

        public void setApproved(Address approved) {
            this.approved = approved;
        }

        public BigInteger getTokenId() {
            return tokenId;
        }

        public void setTokenId(BigInteger tokenId) {
            this.tokenId = tokenId;
        }
    }

    /**
     * This emits when an operator is enabled or disabled for an owner.
     * The operator can manage all NFTs of the owner.
     */
    class ApprovalForAll implements Event {
        private Address owner;
        private Address operator;
        private Boolean approved;

        public ApprovalForAll(@Required Address owner, @Required Address operator, @Required Boolean approved) {
            this.owner = owner;
            this.operator = operator;
            this.approved = approved;
        }

        public Address getOwner() {
            return owner;
        }

        public void setOwner(Address owner) {
            this.owner = owner;
        }

        public Address getOperator() {
            return operator;
        }

        public void setOperator(Address operator) {
            this.operator = operator;
        }

        public Boolean getApproved() {
            return approved;
        }

        public void setApproved(Boolean approved) {
            this.approved = approved;
        }
    }
}
