package io.nuls.vote.contract.event;

import io.nuls.contract.sdk.Event;

import java.util.List;

public class VoteEvent implements Event {

    private Long voteId;
    private List<Long> itemIds;

    public VoteEvent(Long voteId, List<Long> itemIds) {
        this.voteId = voteId;
        this.itemIds = itemIds;
    }

    public Long getVoteId() {
        return voteId;
    }

    public List<Long> getItemIds() {
        return itemIds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VoteEvent voteEvent = (VoteEvent) o;

        if (voteId != null ? !voteId.equals(voteEvent.voteId) : voteEvent.voteId != null) return false;
        return itemIds != null ? itemIds.equals(voteEvent.itemIds) : voteEvent.itemIds == null;
    }

    @Override
    public int hashCode() {
        int result = voteId != null ? voteId.hashCode() : 0;
        result = 31 * result + (itemIds != null ? itemIds.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "{" +
                "\"voteId\": " + voteId +
                ", \"itemIds\": " + itemIds +
                "}";
    }
}
