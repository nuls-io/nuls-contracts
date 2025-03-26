package io.nuls.contract.extend;

import io.nuls.contract.extend.entity.OwnerFetch;
import io.nuls.contract.extend.event.ChangeExtend;
import io.nuls.contract.extend.event.ChangeKeyMaintain;
import io.nuls.contract.sdk.*;
import io.nuls.contract.sdk.annotation.View;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import static io.nuls.contract.sdk.Utils.emit;
import static io.nuls.contract.sdk.Utils.require;

public class DataExtend extends Ownable implements Contract{

    private Map<String, String> dataMap = new HashMap<String, String>();
    private Map<String, Address> keyMaintains = new HashMap<String, Address>();

    public void setMaintain(String key, Address maintain) {
        onlyOwner();
        _setMaintain(key, maintain);
    }

    private void _setMaintain(String key, Address maintain_) {
        require(key != null && maintain_ != null, "empty param");
        Address _maintain = keyMaintains.get(key);
        keyMaintains.put(key, maintain_);
        emit(new ChangeKeyMaintain(key, _maintain, maintain_));
    }

    public void batchSetMaintain(String[] keys, String[] maintains) {
        onlyOwner();
        require(keys != null && maintains != null && keys.length == maintains.length, "error param");
        for (int i = 0, length = keys.length; i < length; i++) {
            _setMaintain(keys[i], new Address(maintains[i]));
        }
    }

    public void setExtendUri(String key, String extend_) {
        Address address = keyMaintains.get(key);
        if (address == null) {
            String senderStr = Msg.sender().toString();
            int addrLen = senderStr.length();
            require (key.length() == addrLen, "Permission denied");
            OwnerFetch ownerFetch = new OwnerFetch(new Address(key));
            String viewOwner = ownerFetch.viewOwner();
            require(senderStr.equals(viewOwner), "Not owner");
            address = new Address(viewOwner);
            _setMaintain(key, address);
        } else {
            require(Msg.sender().equals(address), "Permission denied");
        }
        String _extend = dataMap.get(key);
        dataMap.put(key, extend_);
        emit(new ChangeExtend(key, _extend, extend_));
    }

    @View
    public String getExtend(String key) {
        String result = dataMap.get(key);
        return result == null ? "" : result;
    }

    @View
    public String getMaintain(String key) {
        Address result = keyMaintains.get(key);
        return result == null ? "" : result.toString();
    }
}
