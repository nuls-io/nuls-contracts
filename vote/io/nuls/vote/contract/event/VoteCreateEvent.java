package io.nuls.vote.contract.event;

import io.nuls.contract.sdk.Event;
import io.nuls.vote.contract.model.VoteItem;

import java.util.List;

public class VoteCreateEvent implements Event {

    private Long voteId;
    private String title;
    private String desc;
    private List<VoteItem> items;

    public VoteCreateEvent(Long voteId, String title, String desc, List<VoteItem> items) {
        this.voteId = voteId;
        this.title = title;
        this.desc = desc;
        this.items = items;
    }

    public Long getVoteId() {
        return voteId;
    }

    public void setVoteId(Long voteId) {
        this.voteId = voteId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public List<VoteItem> getItems() {
        return items;
    }

    public void setItems(List<VoteItem> items) {
        this.items = items;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VoteCreateEvent that = (VoteCreateEvent) o;

        if (voteId != null ? !voteId.equals(that.voteId) : that.voteId != null) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (desc != null ? !desc.equals(that.desc) : that.desc != null) return false;
        return items != null ? items.equals(that.items) : that.items == null;
    }

    @Override
    public int hashCode() {
        int result = voteId != null ? voteId.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (desc != null ? desc.hashCode() : 0);
        result = 31 * result + (items != null ? items.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "{" +
                "\"voteId\": " + voteId +
                ", \"title\": \"" + title + "\"" +
                ", \"desc\": \"" + desc + "\"" +
                ", \"items\": " + items +
                "}";
    }
}
