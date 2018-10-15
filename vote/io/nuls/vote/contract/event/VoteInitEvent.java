package io.nuls.vote.contract.event;

import io.nuls.contract.sdk.Event;
import io.nuls.vote.contract.model.VoteConfig;

public class VoteInitEvent implements Event {

    private Long voteId;
    private VoteConfig voteConfig;

    public VoteInitEvent(Long voteId, VoteConfig voteConfig) {
        this.voteId = voteId;
        this.voteConfig = voteConfig;
    }

    public Long getVoteId() {
        return voteId;
    }

    public VoteConfig getVoteConfig() {
        return voteConfig;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VoteInitEvent that = (VoteInitEvent) o;

        if (voteId != null ? !voteId.equals(that.voteId) : that.voteId != null) return false;
        return voteConfig != null ? voteConfig.equals(that.voteConfig) : that.voteConfig == null;
    }

    @Override
    public int hashCode() {
        int result = voteId != null ? voteId.hashCode() : 0;
        result = 31 * result + (voteConfig != null ? voteConfig.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "{" +
                "\"voteId\": " + voteId +
                ", \"voteConfig\": " + voteConfig +
                "}";
    }
}
