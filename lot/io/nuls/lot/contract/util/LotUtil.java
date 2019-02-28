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
package io.nuls.lot.contract.util;

import io.nuls.contract.sdk.Utils;

import java.math.BigInteger;

import static io.nuls.contract.sdk.Utils.getRandomSeed;

/**
 * @author: PierreLuo
 * @date: 2019-02-18
 */
public class LotUtil {

    public static int[] extractWinners(long endHeight, int count, int usersCount, int winnersNumber) {
        BigInteger orginSeed = getRandomSeed(endHeight, count, "sha3");
        //BigInteger orginSeed = new BigInteger("20574417149658526540052655865087612050331728896268994550572235049893847109103");
        if (orginSeed.equals(BigInteger.ZERO)) {
            return null;
        }
        int[] users = new int[usersCount];
        for (int i = 0; i < usersCount; i++) {
            users[i] = i;
        }

        BigInteger remainingNumber = BigInteger.valueOf((long) usersCount);
        int[] result = new int[winnersNumber];
        int resultIndex = 0;
        BigInteger mod = orginSeed.mod(remainingNumber);
        int modValue = mod.intValue();
        result[resultIndex++] = users[modValue];
        int realLength = compressArray(users, modValue, usersCount);

        String orginStr = orginSeed.toString();
        int length = orginStr.length();
        for (int i = 1; i < winnersNumber; i++) {
            remainingNumber = remainingNumber.subtract(BigInteger.ONE);
            int c = orginStr.charAt(length - i);
            BigInteger multiply = orginSeed.multiply(BigInteger.valueOf(c));
            String s = Utils.sha3(multiply.toByteArray());
            //String s = Sha3Hash.sha3(multiply.toByteArray());
            byte[] decode = decode(s);
            BigInteger bigInteger = new BigInteger(decode);
            mod = bigInteger.mod(remainingNumber);
            modValue = mod.intValue();
            result[resultIndex++] = users[modValue];
            realLength = compressArray(users, modValue, realLength);
        }
        sort(result);
        return result;
    }

    //public static void main(String[] args) {
    //    int[] ints = extractWinners(1, 1, 20, 10);
    //    System.out.println(Arrays.toString(ints));
    //}

    private static void sort(int[] array) {
        for (int index = 1, length = array.length; index < length; index++) {
            int temp = array[index];
            int leftindex = index - 1;
            while (leftindex >= 0 && array[leftindex] > temp) {
                array[leftindex + 1] = array[leftindex];
                leftindex--;
            }
            array[leftindex + 1] = temp;
        }
    }

    private static int compressArray(int[] users, int index, int realLengh) {
        for (int i = index, length = realLengh - 1; i < length; i++) {
            users[i] = users[i + 1];
        }
        return realLengh - 1;
    }

    private static byte[] decode(String hexString) {
        byte[] bts = new byte[hexString.length() / 2];
        for (int i = 0; i < bts.length; i++) {
            bts[i] = (byte) Integer.parseInt(hexString.substring(2 * i, 2 * i + 2), 16);
        }
        return bts;
    }

}
