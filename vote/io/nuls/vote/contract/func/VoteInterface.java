package io.nuls.vote.contract.func;

import io.nuls.contract.sdk.Address;
import io.nuls.vote.contract.model.VoteConfig;
import io.nuls.vote.contract.model.VoteEntity;

import java.util.List;
import java.util.Map;

public interface VoteInterface {

    VoteEntity create(String title, String desc, String[] items);

    boolean init(long voteId, VoteConfig config);

    boolean vote(long voteId, long[] itemIds);

    boolean canVote(long voteId);

    boolean redemption(long voteId);

    VoteEntity queryVote(long voteId);

    Map<Address, List<Long>> queryVoteResult(long voteId);

    boolean queryAddressHasVote(long voteId, Address address);
}
