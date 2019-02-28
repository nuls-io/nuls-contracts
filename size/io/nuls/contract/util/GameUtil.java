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
package io.nuls.contract.util;

import java.math.BigDecimal;
import java.math.BigInteger;

import static io.nuls.contract.sdk.Utils.getRandomSeed;
/**
 * @author: PierreLuo
 * @date: 2019/1/7
 */
public class GameUtil {

    public static BigDecimal toNuls(BigInteger na) {
        return new BigDecimal(na).movePointLeft(8);
    }

    public static BigInteger toNa(BigDecimal nuls) {
        return nuls.scaleByPowerOfTen(8).toBigInteger();
    }

    public static int random(long endHeight, int count, int range) {
        BigInteger orginSeed = getRandomSeed(endHeight, count, "sha3");
        if (orginSeed.equals(BigInteger.ZERO)) {
            return -1;
        }
        BigInteger wrapperRange = BigInteger.valueOf((long) range);
        BigInteger mod = orginSeed.mod(wrapperRange);
        return mod.add(BigInteger.ONE).intValue();
    }

}
