package io.nuls.vote.contract.func;

import io.nuls.contract.sdk.Address;
import io.nuls.contract.sdk.Block;
import io.nuls.contract.sdk.Msg;
import io.nuls.vote.contract.event.VoteCreateEvent;
import io.nuls.vote.contract.event.VoteEvent;
import io.nuls.vote.contract.event.VoteInitEvent;
import io.nuls.vote.contract.model.VoteConfig;
import io.nuls.vote.contract.model.VoteEntity;
import io.nuls.vote.contract.model.VoteItem;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.nuls.contract.sdk.Utils.emit;
import static io.nuls.contract.sdk.Utils.require;

public class BaseVote implements VoteInterface {

    private BigInteger minRecognizance;

    protected Map<Long, VoteEntity> votes = new HashMap<Long, VoteEntity>();
    protected Map<Long, Map<Address, List<Long>>> voteRecords = new HashMap<Long, Map<Address, List<Long>>>();

    public BaseVote(BigInteger minRecognizance) {
        require(minRecognizance.compareTo(BigInteger.valueOf(0)) > 0, "minRecognizance need greater than zero");
        this.minRecognizance = minRecognizance;
    }

    public VoteEntity create(String title, String desc, String[] items) {

        require(items != null && items.length > 0, "items can not empty");
        require(title != null, "title can not empty");
        require(desc != null, "desc can not empty");

        BigInteger value = Msg.value();
        require(value.compareTo(minRecognizance) >= 0, "value need greater than " + minRecognizance);

        Long voteId = Long.valueOf(votes.size() + 1);

        VoteEntity voteEntity = new VoteEntity();

        voteEntity.setId(voteId);
        voteEntity.setTitle(title);
        voteEntity.setDesc(desc);
        voteEntity.setRecognizance(value);

        List<VoteItem> itemList = new ArrayList<VoteItem>();

        for(int itemId = 0 ; itemId < items.length ; itemId++) {
            VoteItem item = new VoteItem();
            item.setId((long) itemId + 1);
            item.setContent(items[itemId]);
            itemList.add(item);
        }

        voteEntity.setItems(itemList);
        voteEntity.setStatus(VoteStatus.STATUS_WAIT_INIT);
        voteEntity.setOwner(Msg.sender());

        votes.put(voteId, voteEntity);

        emit(new VoteCreateEvent(voteId, title, desc, itemList));

        return voteEntity;
    }

    public boolean init(long voteId, VoteConfig config) {

        require(voteId > 0L, "voteId error");
        require(config != null, "config can not null");

        onlyOwner(voteId);

        require(config.check(), "the config is error");

        VoteEntity voteEntity = votes.get(voteId);

        require(voteEntity != null, "vote not find");

        require(voteEntity.getStatus() == VoteStatus.STATUS_WAIT_INIT, "vote status error");

        List<VoteItem> items = voteEntity.getItems();
        require(items != null && items.size() > 0, "vote item can not empty");

        if(config.isMultipleSelect()) {
            require(config.getMaxSelectCount() <= items.size(), "max select count can not greater then item size");
        }

        voteEntity.setConfig(config);
        voteEntity.setStatus(VoteStatus.STATUS_WAIT_VOTE);

        emit(new VoteInitEvent(voteId, config));

        return true;
    }

    public boolean vote(long voteId, long[] itemIds) {

        require(canVote(voteId), "can not vote, please check.");
        require(itemIds != null && itemIds.length > 0, "item id can not empty");

        VoteEntity voteEntity = votes.get(voteId);
        VoteConfig config = voteEntity.getConfig();

        if(config.isMultipleSelect()) {
            require(itemIds.length <= config.getMaxSelectCount(), "option cannot be greater than " + config.getMaxSelectCount());
        }
        if(!config.isMultipleSelect()) {
            require(itemIds.length == 1, "only support single selection");
        }

        List<VoteItem> items = voteEntity.getItems();
        List<Long> itemIdList = new ArrayList<Long>();

        for(int i = 0 ; i < itemIds.length ; i++) {
            Long itemId = itemIds[i];
            boolean hasExist = false;

            for(int m = 0 ; m < items.size() ; m++) {
                VoteItem voteItem = items.get(m);
                if(voteItem.getId().equals(itemId)) {
                    hasExist = true;
                    break;
                }
            }

            if(!hasExist) {
                require(false, "entered the wrong item id");
                return false;
            }
            itemIdList.add(itemId);
        }

        Map<Address, List<Long>> records = voteRecords.get(voteId);
        if(records == null) {
            records = new HashMap<Address, List<Long>>();
            voteRecords.put(voteId, records);
        }

        if(!voteEntity.getConfig().isVoteCanModify()) {
            require(records.get(Msg.sender()) == null, "already voted");
        }

        records.put(Msg.sender(), itemIdList);

        emit(new VoteEvent(voteId, itemIdList));

        return true;
    }

    public boolean redemption(long voteId) {

        require(voteId > 0L, "voteId error");

        //onlyOwner(voteId);

        VoteEntity voteEntity = votes.get(voteId);

        require(voteEntity != null, "vote is not find");

        if(voteEntity.getStatus() != VoteStatus.STATUS_CLOSE) {
            require(!canVote(voteId), "current vote is not over yet");
        }

        require(voteEntity.getStatus() == VoteStatus.STATUS_CLOSE, "vote has not closed");
        require(voteEntity.getRecognizance().compareTo(BigInteger.ZERO) > 0, "recognizance has been redeemed");

        BigInteger balance = Msg.address().balance();
        require(balance.compareTo(voteEntity.getRecognizance()) >= 0, "The contract balance is less than the recognizance amount");


        // return amount
        voteEntity.getOwner().transfer(voteEntity.getRecognizance());

        voteEntity.setRecognizance(BigInteger.ZERO);

        return true;
    }

    public boolean canVote(long voteId) {

        require(voteId > 0L, "voteId error");

        VoteEntity voteEntity = votes.get(voteId);

        if(voteEntity == null) {
            return false;
        }

        VoteConfig config = voteEntity.getConfig();

        if(config == null) {
            return false;
        }

        if(config.getStartTime() > Block.timestamp() || voteEntity.getStatus() == VoteStatus.STATUS_WAIT_INIT) {
            return false;
        } else if(voteEntity.getStatus() == VoteStatus.STATUS_WAIT_VOTE) {
            updateStatus(voteEntity, VoteStatus.STATUS_VOTEING);
        }

        if(voteEntity.getStatus() != VoteStatus.STATUS_VOTEING) {
            return false;
        }

        if(config.getEndTime() < Block.timestamp()) {
            if(voteEntity.getStatus() != VoteStatus.STATUS_CLOSE) {
                updateStatus(voteEntity, VoteStatus.STATUS_CLOSE);
            }
            return false;
        }

        return true;
    }

    public VoteEntity queryVote(long voteId) {

        require(voteId > 0L, "voteId error");

        VoteEntity voteEntity = votes.get(voteId);

        require(voteEntity != null, "vote is not find");

        List<VoteItem> items = voteEntity.getItems();
        voteEntity.setItems(items);

        return voteEntity;
    }

    public Map<Address, List<Long>> queryVoteResult(long voteId) {

        require(voteId > 0L, "voteId error");

        VoteEntity voteEntity = votes.get(voteId);

        require(voteEntity != null, "vote is not find");

        Map<Address, List<Long>> records = voteRecords.get(voteId);

        if(records == null) {
            return new HashMap<Address, List<Long>>();
        }

        return records;
    }

    public boolean queryAddressHasVote(long voteId, Address address) {
        require(voteId > 0L, "voteId error");
        require(address != null, "address is empty");

        VoteEntity voteEntity = votes.get(voteId);

        require(voteEntity != null, "vote is not find");

        Map<Address, List<Long>> records = voteRecords.get(voteId);

        require(records != null, "records is null");

        return records.get(address) != null;
    }

    private void updateStatus(VoteEntity voteEntity, int statusVoteing) {
        voteEntity.setStatus(statusVoteing);
    }

    private void onlyOwner(long voteId) {
        require(voteId > 0L, "voteId error");

        VoteEntity voteEntity = votes.get(voteId);

        require(voteEntity != null, "vote is not find");

        require(voteEntity.getOwner().equals(Msg.sender()), "is not owner");
    }
}
