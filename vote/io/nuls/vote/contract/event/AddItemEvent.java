package io.nuls.vote.contract.event;

import io.nuls.contract.sdk.Event;
import io.nuls.vote.contract.model.VoteItem;

import java.util.List;

public class AddItemEvent implements Event {

    private Long voteId;
    private Long itemId;
    private String itemContent;
    private List<VoteItem> items;

    public AddItemEvent(Long voteId, Long itemId, String itemContent, List<VoteItem> items) {
        this.voteId = voteId;
        this.itemId = itemId;
        this.itemContent = itemContent;
        this.items = items;
    }

    public Long getVoteId() {
        return voteId;
    }

    public Long getItemId() {
        return itemId;
    }

    public String getItemContent() {
        return itemContent;
    }

    public List<VoteItem> getItems() {
        return items;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AddItemEvent that = (AddItemEvent) o;

        if (voteId != null ? !voteId.equals(that.voteId) : that.voteId != null) return false;
        if (itemId != null ? !itemId.equals(that.itemId) : that.itemId != null) return false;
        if (itemContent != null ? !itemContent.equals(that.itemContent) : that.itemContent != null) return false;
        return items != null ? items.equals(that.items) : that.items == null;
    }

    @Override
    public int hashCode() {
        int result = voteId != null ? voteId.hashCode() : 0;
        result = 31 * result + (itemId != null ? itemId.hashCode() : 0);
        result = 31 * result + (itemContent != null ? itemContent.hashCode() : 0);
        result = 31 * result + (items != null ? items.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "{" +
                "\"voteId\": " + voteId +
                ", \"itemId\": " + itemId +
                ", \"itemContent\": \"" + itemContent + "\"" +
                ", \"items\": " + items +
                "}";
    }
}
