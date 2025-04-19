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

import io.nuls.contract.sdk.Address;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import static io.nuls.contract.sdk.Utils.require;

/**
 * @author: PierreLuo
 * @date: 2021/8/6
 */
public class AssetInfo {
    private String pocm;
    private BigInteger nuls = BigInteger.ZERO;
    private Map<Address, BigInteger> nrc20 = new HashMap<Address, BigInteger>();
    private Map<String, BigInteger> crossAsset = new HashMap<String, BigInteger>();

    public String getPocm() {
        return pocm;
    }

    public void setPocm(String pocm) {
        this.pocm = pocm;
    }

    public BigInteger getNuls() {
        return nuls;
    }

    public Map<Address, BigInteger> getNrc20() {
        return nrc20;
    }

    public Map<String, BigInteger> getCrossAsset() {
        return crossAsset;
    }

    public void addNuls(BigInteger amount) {
        nuls = nuls.add(amount);
    }

    public void subNuls(BigInteger amount) {
        require(nuls.compareTo(amount) >= 0, "Insufficient balance of nuls");
        nuls = nuls.subtract(amount);
    }

    public void addNrc20(BigInteger amount, Address nrc20Address) {
        BigInteger total = nrc20.get(nrc20Address);
        if (total == null) {
            total = amount;
        } else {
            total = total.add(amount);
        }
        nrc20.put(nrc20Address, total);
    }

    public void subNrc20(BigInteger amount, Address nrc20Address) {
        BigInteger total = nrc20.get(nrc20Address);
        require(total != null, "NULL nrc20");
        require(total.compareTo(amount) >= 0, "Insufficient balance of nrc20");
        total = total.subtract(amount);
        nrc20.put(nrc20Address, total);
    }

    public void addCrossAsset(BigInteger amount, String assetKey) {
        BigInteger total = crossAsset.get(assetKey);
        if (total == null) {
            total = amount;
        } else {
            total = total.add(amount);
        }
        crossAsset.put(assetKey, total);
    }

    public void subCrossAsset(BigInteger amount, String assetKey) {
        BigInteger total = crossAsset.get(assetKey);
        require(total != null, "NULL cross asset");
        require(total.compareTo(amount) >= 0, "Insufficient balance of cross asset");
        total = total.subtract(amount);
        crossAsset.put(assetKey, total);
    }
}
