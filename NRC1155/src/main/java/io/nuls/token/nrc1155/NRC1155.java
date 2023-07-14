package io.nuls.token.nrc1155;

import io.nuls.contract.sdk.Address;
import io.nuls.contract.sdk.Contract;
import io.nuls.contract.sdk.Msg;
import io.nuls.contract.sdk.annotation.View;
import io.nuls.token.nrc1155.base.NRC1155Base;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static io.nuls.contract.sdk.Utils.require;

public class NRC1155 extends NRC1155Base implements Contract {

    private String name;
    private String symbol;
    private Map<BigInteger, String> _tokenURIs = new HashMap<BigInteger, String>();
    private Map<BigInteger, BigInteger> _totalSupply = new HashMap<BigInteger, BigInteger>();
    private Set<Address> _minters = new HashSet<Address>();

    void onlyMinter() {
        require(isMinter(Msg.sender()), "MinterRole: caller does not have the Minter role");
    }

    public NRC1155(String uri_, String name_, String symbol_){
        super(uri_);
        this.name = name_;
        this.symbol = symbol_;
    }

    public void addMinter(Address address) {
        onlyOwner();
        _minters.add(address);
    }

    public void setURL(BigInteger _tokenId, String _tokenUrl) {
        onlyOwner();
        _tokenURIs.put(_tokenId, _tokenUrl);
    }

    public BigInteger mint(Address to, BigInteger id, BigInteger amount) {
        onlyMinter();
        require(amount.compareTo(BigInteger.ZERO) > 0, "The amount must be have");
        _mint(to, id, amount, "");
        return id;
    }

    public void burn(BigInteger _id, BigInteger _amount) {
        _burn(_msgSender(), _id, _amount);
    }

    @View
    public boolean isMinter(Address address) {
        return _minters.contains(address);
    }

    @View
    public String uri(BigInteger _id) {
        return _tokenURIs.get(_id);
    }

    @View
    public String[] uriBatch(String[] _tokenIds) {
        require(_tokenIds.length > 0,"The _tokenId must be have");

        String[] returnTokenUrl = new String[_tokenIds.length];

        for(int i=0;i<_tokenIds.length;i++){
            String uri = _tokenURIs.get(new BigInteger(_tokenIds[i]));
            returnTokenUrl[i] = uri == null ? "" : uri;
        }
        return returnTokenUrl;
    }

    @View
    public String name() {
        return name;
    }

    @View
    public String symbol() {
        return symbol;
    }

    @View
    public BigInteger totalSupply(BigInteger id) {
        BigInteger ts = _totalSupply.get(id);
        return ts == null ? BigInteger.ZERO : ts;
    }

    @View
    public boolean exists(BigInteger id) {
        BigInteger ts = totalSupply(id);
        return ts.compareTo(BigInteger.ZERO) > 0;
    }

    protected void _beforeTokenTransfer(Address operator, Address from, Address to, BigInteger[] ids, BigInteger[] amounts, String data) {
        super._beforeTokenTransfer(operator, from, to, ids, amounts, data);

        if (from == null) {
            for (int i = 0; i < ids.length; ++i) {
                BigInteger id = ids[i];
                _totalSupply.put(id, totalSupply(id).add(amounts[i]));
            }
        }

        if (to == null) {
            for (int i = 0; i < ids.length; ++i) {
                BigInteger id = ids[i];
                BigInteger amount = amounts[i];
                BigInteger supply = totalSupply(id);
                require(supply.compareTo(amount) >= 0, "ERC1155: burn amount exceeds totalSupply");
                _totalSupply.put(id, supply.subtract(amount));
            }
        }
    }

}