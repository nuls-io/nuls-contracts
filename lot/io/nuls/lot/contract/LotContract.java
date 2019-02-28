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
package io.nuls.lot.contract;

import io.nuls.contract.sdk.Block;
import io.nuls.contract.sdk.Contract;
import io.nuls.contract.sdk.Utils;
import io.nuls.contract.sdk.annotation.View;
import io.nuls.lot.contract.model.LotPool;
import io.nuls.lot.contract.util.LotUtil;

import java.util.ArrayList;
import java.util.List;

import static io.nuls.contract.sdk.Utils.require;

/**
 * @author: PierreLuo
 * @date: 2019-02-18
 */
public class LotContract implements Contract {

    private List<LotPool> lotPoolList;

    public LotContract() {
        this.lotPoolList = new ArrayList<LotPool>();
    }

    public long createLotPool(String[] users, int winnersNumber) {
        Utils.require(users != null && users.length > 0, "empty users!");
        int usersCount = users.length;
        Utils.require(winnersNumber > 0, "The number of winners must be greater than 0!");
        Utils.require(usersCount > winnersNumber, "The number of users must be greater than the nubmer of winners!");
        long id = lotPoolList.size() + 1;
        int status = 1;
        long createHeight = Block.number();
        LotPool lotPool = new LotPool(id, users, winnersNumber, status, createHeight);
        lotPool.setDrawHeight(createHeight + 10);
        lotPoolList.add(lotPool);
        return id;
    }

    private LotPool getLotPool(long poolId) {
        checkLotPoolExist(poolId);
        return lotPoolList.get((int) (poolId - 1));
    }

    private void checkLotPoolExist(long poolId) {
        int size = lotPoolList.size();
        require(poolId > 0 && size >= poolId, "The lot pool does not exist.");
    }

    public String draw(long poolId) {

        LotPool lotPool = this.getLotPool(poolId);
        int status = lotPool.getStatus();
        require(status == 1, "The lot pool is over.");
        long drawHeight = lotPool.getDrawHeight();
        require(Block.number() > drawHeight, "Not yet at the draw time. Please draw after " + drawHeight + " block height.");

        // 计算得奖结果
        String[] users = lotPool.getUsers();

        int[] winners = LotUtil.extractWinners(drawHeight, 10, users.length, lotPool.getWinnersNumber());

        lotPool.setWinners(winners);
        lotPool.setStatus(2);

        return getWinners(users, winners);
    }

    private String getWinners(String[] users, int[] winners) {
        StringBuilder sb = new StringBuilder();
        for (int winner : winners) {
            sb.append(users[winner].toString()).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    private String getLosers(String[] users, int[] winners) {
        int usersLength = users.length;
        int winnersLength = winners.length;
        int[] losers = new int[usersLength - winnersLength];
        int winnerIndex = winners[0];
        for (int i = 0, w = 1, l = 0; i < usersLength; i++) {
            if (winnerIndex == i) {
                if(w < winnersLength) {
                    winnerIndex = winners[w++];
                }
            } else {
                losers[l++] = i;
            }
        }
        StringBuilder sb = new StringBuilder();
        for (int loser : losers) {
            sb.append(users[loser].toString()).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    //public static void main(String[] args) {
    //    int[] users = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19};
    //    int[] winners = new int[]{1, 3, 4, 6, 9, 10, 12, 13, 14, 16, 17};
    //    int usersLength = users.length;
    //    int winnersLength = winners.length;
    //    int[] losers = new int[usersLength - winnersLength];
    //    int winnerIndex = winners[0];
    //    for (int i = 0, w = 1, l = 0; i < usersLength; i++) {
    //        if (winnerIndex == i) {
    //            if(w < winnersLength) {
    //                winnerIndex = winners[w++];
    //            }
    //        } else {
    //            losers[l++] = i;
    //        }
    //    }
    //    System.out.println(Arrays.toString(winners));
    //    System.out.println(Arrays.toString(losers));
    //
    //    String[] strUsers = new String[]{"1a","2b","3c"};
    //    LotPool lotPool = new LotPool(1L, strUsers, 2, 1, 892341L);
    //    System.out.println(lotPool);
    //    lotPool.setWinners(winners);
    //    System.out.println(lotPool);
    //}

    @View
    public String viewWinners(long poolId) {
        LotPool lotPool = this.getLotPool(poolId);
        int status = lotPool.getStatus();
        require(status == 2, "The lot pool is not over yet.");
        return getWinners(lotPool.getUsers(), lotPool.getWinners());
    }

    @View
    public String viewLoser(long poolId) {
        LotPool lotPool = this.getLotPool(poolId);
        int status = lotPool.getStatus();
        require(status == 2, "The lot pool is not over yet.");
        return getLosers(lotPool.getUsers(), lotPool.getWinners());
    }


    @View
    public LotPool viewLotPoolDetails(long poolId) {
        return this.getLotPool(poolId);
    }

    @View
    public List<LotPool> viewAllLotPoolList() {
        return lotPoolList;
    }

    @View
    public List<LotPool> viewDrawingLotPoolList() {
        List<LotPool> result = new ArrayList<LotPool>();
        for (LotPool lotPool : lotPoolList) {
            if (lotPool.getStatus() == 1) {
                result.add(lotPool);
            }
        }
        return result;
    }
}
