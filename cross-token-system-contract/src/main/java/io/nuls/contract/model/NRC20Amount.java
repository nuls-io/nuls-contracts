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

import java.math.BigInteger;

/**
 * @author: PierreLuo
 * @date: 2019-10-15
 */
public class NRC20Amount {
    private Address nrc20;
    private BigInteger value;

    public NRC20Amount(Address nrc20, BigInteger value) {
        this.nrc20 = nrc20;
        this.value = value;
    }

    public NRC20Amount(BigInteger value) {
        this.value = value;
    }

    public NRC20Amount() {
        this.value = BigInteger.ZERO;
    }

    public BigInteger getValue() {
        return value;
    }

    public void addValue(BigInteger value) {
        this.value = this.value.add(value);
    }

    public void subtractValue(BigInteger value) {
        this.value = this.value.subtract(value);
    }

    public Address getNrc20() {
        return nrc20;
    }

    public void setNrc20(Address nrc20) {
        this.nrc20 = nrc20;
    }

    public void setValue(BigInteger value) {
        this.value = value;
    }
}
