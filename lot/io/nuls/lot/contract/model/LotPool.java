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
package io.nuls.lot.contract.model;


/**
 * @author: PierreLuo
 * @date: 2019-02-18
 */
public class LotPool {

    private long id;

    private String[] users;

    private int winnersNumber;

    // 1-drawing, 2-end
    private int status;

    private long createHeight;

    private long drawHeight;

    private int[] winners;

    public LotPool(long id, String[] users, int winnersNumber, int status, long createHeight) {
        this.id = id;
        this.users = users;
        this.winnersNumber = winnersNumber;
        this.status = status;
        this.createHeight = createHeight;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"id\":")
                .append(id);
        sb.append(",\"users\":")
                .append(arrayToString(users));
        sb.append(",\"winnersNumber\":")
                .append(winnersNumber);
        sb.append(",\"status\":")
                .append(status == 1 ? "\"drawing\"" : "\"end\"");
        sb.append(",\"createHeight\":")
                .append(createHeight == 0 ? "\"-\"" : createHeight);
        sb.append(",\"drawHeight\":")
                .append(drawHeight == 0 ? "\"-\"" : drawHeight);
        sb.append(",\"winnersIndex\":")
                .append(arrayToString(winners));
        sb.append('}');
        return sb.toString();
    }

    private String arrayToString(String[] a) {
        if (a == null)
            return "[]";
        int iMax = a.length - 1;
        if (iMax == -1)
            return "[]";

        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = 0; ; i++) {
            b.append("\"").append(a[i]).append("\"");
            if (i == iMax)
                return b.append(']').toString();
            b.append(", ");
        }
    }

    private String arrayToString(int[] a) {
        if (a == null)
            return "[]";
        int iMax = a.length - 1;
        if (iMax == -1)
            return "[]";

        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = 0; ; i++) {
            b.append(a[i]);
            if (i == iMax)
                return b.append(']').toString();
            b.append(",");
        }
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String[] getUsers() {
        return users;
    }

    public void setUsers(String[] users) {
        this.users = users;
    }

    public int getWinnersNumber() {
        return winnersNumber;
    }

    public void setWinnersNumber(int winnersNumber) {
        this.winnersNumber = winnersNumber;
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

    public int[] getWinners() {
        return winners;
    }

    public void setWinners(int[] winners) {
        this.winners = winners;
    }

    //public static void main(String[] args) {
    //    int times = 20;
    //    char let = 'a';
    //    String result = "";
    //    for(int i=0;i<times;i++,let++) {
    //        result += "\"";
    //        result += let;
    //        result += let;
    //        result += let;
    //        result += i;
    //        result += "\"";
    //        result += ",";
    //    }
    //    System.out.println(result);
    //}

}
