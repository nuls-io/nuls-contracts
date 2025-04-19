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
package io.nuls.contract.pocm.model;

import java.math.BigInteger;

/**
 * @author: PierreLuo
 * @date: 2021/8/6
 */
public class AssetView {
    private BigInteger nuls = BigInteger.ZERO;
    private BigInteger nrc20 = BigInteger.ZERO;
    private BigInteger crossAsset = BigInteger.ZERO;

    public AssetView() {}

    public AssetView(BigInteger nuls, BigInteger nrc20, BigInteger crossAsset) {
        this.nuls = nuls;
        this.nrc20 = nrc20;
        this.crossAsset = crossAsset;
    }

    public BigInteger getNuls() {
        return nuls;
    }

    public void setNuls(BigInteger nuls) {
        this.nuls = nuls;
    }

    public BigInteger getNrc20() {
        return nrc20;
    }

    public void setNrc20(BigInteger nrc20) {
        this.nrc20 = nrc20;
    }

    public BigInteger getCrossAsset() {
        return crossAsset;
    }

    public void setCrossAsset(BigInteger crossAsset) {
        this.crossAsset = crossAsset;
    }
}
