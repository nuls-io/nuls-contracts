package io.nuls.vote.contract.model;

import io.nuls.contract.sdk.Block;

public class VoteConfig {

    private long startTime;
    private long endTime;
    private boolean isMultipleSelect;
    private int maxSelectCount;
    private boolean voteCanModify;

    public VoteConfig(long startTime, long endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public VoteConfig(long startTime, long endTime, boolean isMultipleSelect, int maxSelectCount) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.isMultipleSelect = isMultipleSelect;
        this.maxSelectCount = maxSelectCount;
    }

    public VoteConfig(long startTime, long endTime, boolean isMultipleSelect, int maxSelectCount, boolean voteCanModify) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.isMultipleSelect = isMultipleSelect;
        this.maxSelectCount = maxSelectCount;
        this.voteCanModify = voteCanModify;
    }

    public boolean check() {

        if(isMultipleSelect && maxSelectCount < 1) {
            return false;
        }

        if(startTime >= endTime) {
            return false;
        }

        if(endTime <= Block.timestamp()) {
            return false;
        }

        return true;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public boolean isMultipleSelect() {
        return isMultipleSelect;
    }

    public void setMultipleSelect(boolean multipleSelect) {
        isMultipleSelect = multipleSelect;
    }

    public int getMaxSelectCount() {
        return maxSelectCount;
    }

    public void setMaxSelectCount(int maxSelectCount) {
        this.maxSelectCount = maxSelectCount;
    }

    public boolean isVoteCanModify() {
        return voteCanModify;
    }

    public void setVoteCanModify(boolean voteCanModify) {
        this.voteCanModify = voteCanModify;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VoteConfig that = (VoteConfig) o;

        if (startTime != that.startTime) return false;
        if (endTime != that.endTime) return false;
        if (isMultipleSelect != that.isMultipleSelect) return false;
        if (maxSelectCount != that.maxSelectCount) return false;
        return voteCanModify == that.voteCanModify;
    }

    @Override
    public int hashCode() {
        int result = (int) (startTime ^ (startTime >>> 32));
        result = 31 * result + (int) (endTime ^ (endTime >>> 32));
        result = 31 * result + (isMultipleSelect ? 1 : 0);
        result = 31 * result + maxSelectCount;
        result = 31 * result + (voteCanModify ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "{" +
                "\"startTime\": " + startTime +
                ", \"endTime\": " + endTime +
                ", \"isMultipleSelect\": " + isMultipleSelect +
                ", \"maxSelectCount\": " + maxSelectCount +
                ", \"voteCanModify\": " + voteCanModify +
                "}";
    }
}
