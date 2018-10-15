package io.nuls.vote.contract;

import io.nuls.contract.sdk.Address;
import io.nuls.contract.sdk.Contract;
import io.nuls.contract.sdk.Utils;
import io.nuls.contract.sdk.annotation.Payable;
import io.nuls.contract.sdk.annotation.View;
import io.nuls.vote.contract.func.BaseVote;
import io.nuls.vote.contract.func.VoteInterface;
import io.nuls.vote.contract.model.VoteConfig;
import io.nuls.vote.contract.model.VoteEntity;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public class VoteContract implements Contract {

    private VoteInterface baseVote;

    public VoteContract(long minRecognizance) {
        baseVote = new BaseVote(BigInteger.valueOf(minRecognizance));
    }

    @Payable
    public VoteEntity create(String title, String desc, String[] items, long startTime, long endTime, boolean isMultipleSelect, int maxSelectCount, boolean voteCanModify) {
        VoteEntity voteEntity = baseVote.create(title, desc, items);

        VoteConfig config = new VoteConfig(startTime, endTime, isMultipleSelect, maxSelectCount, voteCanModify);
        boolean success = baseVote.init(voteEntity.getId(), config);

        Utils.require(success, "vote init fail");

        return voteEntity;
    }

//    public boolean init(long voteId, long startTime, long endTime, boolean isMultipleSelect, int maxSelectCount, boolean voteCanModify) {
//        VoteConfig config = new VoteConfig(startTime, endTime, isMultipleSelect, maxSelectCount, voteCanModify);
//        return baseVote.init(voteId, config);
//    }

    public boolean vote(long voteId, long[] itemIds) {
        return baseVote.vote(voteId, itemIds);
    }

    public boolean redemption(long voteId) {
        return baseVote.redemption(voteId);
    }

    @View
    public boolean canVote(long voteId) {
        return baseVote.canVote(voteId);
    }

    @View
    public VoteEntity queryVote(long voteId) {
        return baseVote.queryVote(voteId);
    }

    @View
    public Map<Address, List<Long>> queryVoteResult(long voteId) {
        return baseVote.queryVoteResult(voteId);
    }

    @View
    public boolean queryAddressHasVote(long voteId, Address address) {
        return baseVote.queryAddressHasVote(voteId, address);
    }
}
