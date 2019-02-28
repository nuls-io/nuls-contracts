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
package io.nuls.contract.base;

import io.nuls.contract.ownership.Ownable;
import io.nuls.contract.sdk.Msg;
import io.nuls.contract.sdk.annotation.Payable;
import io.nuls.contract.sdk.annotation.View;
import io.nuls.contract.util.GameUtil;

import java.math.BigInteger;

import static io.nuls.contract.sdk.Utils.require;

/**
 * @author: PierreLuo
 * @date: 2019/1/10
 */
public class Donation extends Ownable {

    /**
     * 可用捐赠
     */
    protected BigInteger donation;

    /**
     * 总捐赠
     */
    protected BigInteger totalDonation;

    public Donation() {
        this.donation = BigInteger.ZERO;
        this.totalDonation = BigInteger.ZERO;
    }

    @Payable
    public void donation() {
        BigInteger currentDonation = Msg.value();
        require(currentDonation.compareTo(BigInteger.ZERO) > 0, "The amount must be greater than 0.");
        this.addDonation(currentDonation);
    }

    protected void addDonation(BigInteger currentDonation) {
        this.donation = this.donation.add(currentDonation);
        this.totalDonation = this.totalDonation.add(currentDonation);
    }

    @View
    public String viewAvailableDonation() {
        return GameUtil.toNuls(this.donation).toPlainString();
    }

    @View
    public String viewTotalDonation() {
        return GameUtil.toNuls(this.totalDonation).toPlainString();
    }

    public String withdrawDonation() {
        onlyOwner();
        require(this.donation.compareTo(BigInteger.ZERO) > 0, "The withdraw amount must be greater than 0.");
        BigInteger amount = this.donation;
        this.donation = BigInteger.ZERO;
        this.owner.transfer(amount);
        return GameUtil.toNuls(amount).toPlainString();
    }
}
