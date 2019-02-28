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


import java.math.BigDecimal;

/**
 * @author: PierreLuo
 * @date: 2019/1/7
 */
public class Game {

    private long id;

    private BigDecimal gamebling;

    private Creator creator;

    private Participant participant;

    //TODO 0-running, 1-drawing, 2-end
    private int status;

    private long createHeight;

    private long drawHeight;

    private int winnerSelection;

    public Game(long id, Creator creator, int status, long createHeight) {
        this.id = id;
        this.creator = creator;
        this.status = status;
        this.createHeight = createHeight;
        this.winnerSelection = -1;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public BigDecimal getGamebling() {
        return gamebling;
    }

    public void setGamebling(BigDecimal gamebling) {
        this.gamebling = gamebling;
    }

    public Creator getCreator() {
        return creator;
    }

    public void setCreator(Creator creator) {
        this.creator = creator;
    }

    public Participant getParticipant() {
        return participant;
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getCreateHeight() {
        return createHeight;
    }

    public void setCreateHeight(long createHeight) {
        this.createHeight = createHeight;
    }

    public long getDrawHeight() {
        return drawHeight;
    }

    public void setDrawHeight(long drawHeight) {
        this.drawHeight = drawHeight;
    }

    public int getWinnerSelection() {
        return winnerSelection;
    }

    public void setWinnerSelection(int winnerSelection) {
        this.winnerSelection = winnerSelection;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"id\":")
                .append(id);
        sb.append(",\"gamebling\":")
                .append(gamebling != null ? gamebling.toPlainString() : "\"-\"");
        sb.append(",\"creator\":")
                .append(creator != null ? creator.toString() : "\"-\"");
        sb.append(",\"participant\":")
                .append(participant != null ? participant.toString() : "\"-\"");
        sb.append(",\"status\":")
                .append(status == 0 ? "\"running\"" : (status == 1 ? "\"drawing\"" : "\"end\""));
        sb.append(",\"createHeight\":")
                .append(createHeight == 0 ? "\"-\"" : createHeight);
        sb.append(",\"drawHeight\":")
                .append(drawHeight == 0 ? "\"-\"" : drawHeight);
        sb.append(",\"winnerSelection\":")
                .append(winnerSelection == 0 ? "\"small\"" : (winnerSelection == 1 ? "\"big\"" : "\"-\""));
        sb.append('}');
        return sb.toString();
    }
}
