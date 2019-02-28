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
package io.nuls.contract.tiger;

import io.nuls.contract.base.Donation;
import io.nuls.contract.model.Creator;
import io.nuls.contract.model.Game;
import io.nuls.contract.model.Participant;
import io.nuls.contract.sdk.Address;
import io.nuls.contract.sdk.Block;
import io.nuls.contract.sdk.Contract;
import io.nuls.contract.sdk.Msg;
import io.nuls.contract.sdk.annotation.Payable;
import io.nuls.contract.sdk.annotation.View;
import io.nuls.contract.util.GameUtil;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.nuls.contract.sdk.Utils.require;

/**
 * @author: PierreLuo
 * @date: 2019/1/7
 */
public class Tiger extends Donation implements Contract {
    /**
     * 游戏列表
     */
    private List<Game> gameList;
    private Map<String, List<Long>> userGameListMap;

    public Tiger() {
        this.gameList = new ArrayList<Game>();
        this.userGameListMap = new HashMap<String, List<Long>>();
    }

    @Override
    @Payable
    public void _payable() {
        this.donation();
    }

    @Payable
    public long createGame(int selection) {
        require(selection == 0 || selection == 1, "Please enter 0 or 1 in selection.(0 - small, 1 - big)");
        Address sender = Msg.sender();
        BigDecimal nuls = GameUtil.toNuls(Msg.value());

        require(nuls.compareTo(BigDecimal.ONE) >= 0, "The amount must be greater than or equal to 1.00000000 NULS.");
        long id = gameList.size() + 1;
        int status = 0;

        Creator creator = new Creator(sender, selection);
        creator.setBet(nuls);
        gameList.add(new Game(id, creator, status, Block.number()));
        addUserGameListMap(sender.toString(), id);

        return id;
    }

    private void addUserGameListMap(String address, long id) {
        List<Long> userGameList = null;
        if((userGameList = userGameListMap.get(address)) != null) {
            userGameList.add(id);
        } else {
            userGameList = new ArrayList<Long>();
            userGameList.add(id);
            userGameListMap.put(address, userGameList);
        }
    }

    @Payable
    public long joinGame(long gameId) {

        Game game = this.getGame(gameId);
        int status = game.getStatus();
        if(status == 2) {
            require(status == 0, "The game is over.");
        } else {
            require(status == 0, "The game already has a participant.");
        }
        BigDecimal nuls = GameUtil.toNuls(Msg.value());
        Creator creator = game.getCreator();
        BigDecimal creatorBet = creator.getBet();
        require(nuls.compareTo(BigDecimal.ONE) >= 0 && creatorBet.compareTo(nuls) >= 0, "You need to pay at least 1.00000000 NULS, up to " + creatorBet.toPlainString() + " NULS.");

        Address sender = Msg.sender();
        int selection = creator.getSelection() ^ 1;
        Participant participant = new Participant(sender, selection);
        participant.setBet(nuls);

        game.setParticipant(participant);
        game.setStatus(1);
        game.setGamebling(nuls.add(nuls));
        game.setDrawHeight(Block.number() + 10);
        addUserGameListMap(sender.toString(), gameId);

        return gameId;
    }

    public String draw(long gameId) {

        Game game = this.getGame(gameId);
        int status = game.getStatus();
        if(status == 2) {
            require(status == 1, "The game is over.");
        } else {
            require(status == 1, "The game is waiting for the participant.");
        }
        long drawHeight = game.getDrawHeight();
        require(Block.number() > drawHeight, "Not yet at the draw time. Please draw after " + drawHeight + " block height.");

        // 计算得奖结果
        //String blockhash = Block.blockhash(drawHeight);
        //require(blockhash != null && blockhash.length() > 0, "The block hash is null, the block height is " + drawHeight + ".");
        Creator creator = game.getCreator();
        Participant participant = game.getParticipant();
        Address creatorAddress = creator.getAddress();
        Address participantAddress = participant.getAddress();
        String creatorAddressStr = creatorAddress.toString();
        String participantAddressStr = participantAddress.toString();
        //int result = creatorAddressStr.hashCode();
        //result = 31 * result + participantAddressStr.hashCode();
        //result = 31 * result + game.getGamebling().hashCode();
        //result = 31 * result + blockhash.hashCode();
        //int number = (int) (GameUtil.pseudoRandom(result) * 16) + 1;
        int number = GameUtil.random(drawHeight, 10, 16);
        require(number > 0, "Failed to get random number.");
        int winner = number > 8 ? 1 : 0;
        game.setWinnerSelection(winner);
        boolean isCreatorWin = creator.getSelection() == winner;

        // 游戏结束
        game.setStatus(2);

        // 发奖
        String winnerAddress;
        BigInteger prize = calPrize(game);
        if(isCreatorWin) {
            winnerAddress = creatorAddressStr;
            creator.setWinner(true);
            creatorAddress.transfer(prize);
        } else {
            winnerAddress = participantAddressStr;
            participant.setWinner(true);
            participantAddress.transfer(prize);
            // 退回多余的NULS
            BigDecimal refund = creator.getBet().subtract(participant.getBet());
            if(refund.compareTo(BigDecimal.ZERO) > 0) {
                creatorAddress.transfer(GameUtil.toNa(refund));
            }
        }

        return winnerAddress;
    }

    public void interrupt(long gameId) {
        Game game = this.getGame(gameId);
        int status = game.getStatus();
        if(status == 2) {
            require(status == 0, "The game is over.");
        } else {
            require(status == 0, "The game already has a participant.");
        }

        Creator creator = game.getCreator();
        Address sender = Msg.sender();
        Address creatorAddress = creator.getAddress();
        require(sender.equals(creatorAddress), "Only the creator of the game can execute it.");

        long createHeight = game.getCreateHeight();
        long currentHeight = Block.number();
        long interval = currentHeight - createHeight;
        require(interval > 100, "You can cancel the game after " + (createHeight + 100) + " block height.");
        BigInteger bet = GameUtil.toNa(creator.getBet());

        // 结束游戏
        game.setStatus(2);

        // 退还NULS
        creatorAddress.transfer(bet);
    }

    @View
    public Game viewGameDetail(long gameId) {
        return this.getGame(gameId);
    }

    @View
    public int viewGamesTotalCount() {
        return this.gameList.size();
    }

    @View
    public List<Game> viewRunningGameList() {
        List<Game> result = new ArrayList<Game>();
        for(Game game : gameList) {
            if(game.getStatus() == 0) {
                result.add(game);
            }
        }
        return result;
    }

    @View
    public List<Game> viewDrawingGameList() {
        List<Game> result = new ArrayList<Game>();
        for(Game game : gameList) {
            if(game.getStatus() == 1) {
                result.add(game);
            }
        }
        return result;
    }

    @View
    public List<Game> viewUserGameList(Address user) {
        List<Game> result = new ArrayList<Game>();
        String userStr = user.toString();
        List<Long> idList = userGameListMap.get(userStr);
        if(idList != null && !idList.isEmpty()) {
            for(Long id : idList) {
                result.add(gameList.get((int) (id - 1)));
            }
        }
        return result;
    }

    private BigInteger calPrize(Game game) {
        return GameUtil.toNa(game.getGamebling());
    }

    private Game getGame(long gameId) {
        checkGameExist(gameId);
        return gameList.get((int) (gameId - 1));
    }

    private void checkGameExist(long gameId) {
        int size = gameList.size();
        require(gameId > 0 && size >= gameId, "The game does not exist.");
    }
}
