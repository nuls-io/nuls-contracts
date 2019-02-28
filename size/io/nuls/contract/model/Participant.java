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
package io.nuls.contract.model;

import io.nuls.contract.sdk.Address;

import java.math.BigDecimal;

/**
 * @author: PierreLuo
 * @date: 2019/1/7
 */
public class Participant {

    private Address address;

    //TODO 0-small, 1-big
    private int selection;

    private BigDecimal bet;

    private String luckyNumberHash;

    private int luckyNumber;

    private boolean isWinner;

    public Participant(Address address, int selection) {
        this.address = address;
        this.selection = selection;
    }

    public Participant(Address address, int selection, String luckyNumberHash, int luckyNumber, boolean isWinner) {
        this.address = address;
        this.selection = selection;
        this.luckyNumberHash = luckyNumberHash;
        this.luckyNumber = luckyNumber;
        this.isWinner = isWinner;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public int getSelection() {
        return selection;
    }

    public void setSelection(int selection) {
        this.selection = selection;
    }

    public String getLuckyNumberHash() {
        return luckyNumberHash;
    }

    public void setLuckyNumberHash(String luckyNumberHash) {
        this.luckyNumberHash = luckyNumberHash;
    }

    public int getLuckyNumber() {
        return luckyNumber;
    }

    public void setLuckyNumber(int luckyNumber) {
        this.luckyNumber = luckyNumber;
    }

    public boolean isWinner() {
        return isWinner;
    }

    public void setWinner(boolean winner) {
        isWinner = winner;
    }

    public BigDecimal getBet() {
        return bet;
    }

    public void setBet(BigDecimal bet) {
        this.bet = bet;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"address\":")
                .append("\"").append(address.toString()).append("\"");
        sb.append(",\"selection\":")
                .append(selection == 0 ? "\"small\"" : (selection == 1 ? "\"big\"" : "\"-\""));
        sb.append(",\"isWinner\":")
                .append(isWinner);
        sb.append(",\"bet\":")
                .append(bet == null ? "\"-\"" : bet.toPlainString());
        sb.append('}');
        return sb.toString();
    }


}
