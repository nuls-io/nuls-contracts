package io.nuls.vote.contract.func;

public interface VoteStatus {
    int STATUS_WAIT_INIT = 0;
    int STATUS_WAIT_VOTE = 1;
    int STATUS_VOTEING = 2;
    int STATUS_PAUSE = 3;
    int STATUS_CLOSE = 4;
}
