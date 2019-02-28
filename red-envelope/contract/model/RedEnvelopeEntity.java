package contract.model;

import contract.event.SnatchRedEnvelopeEvent;
import contract.util.Nuls;
import io.nuls.contract.sdk.Address;

import java.util.Map;

/**
 * Red envelope instance
 *
 * @author captain
 * @version 1.0
 * @date 19-1-31 下午12:48
 */
public class RedEnvelopeEntity {
    private Long id;//primary key
    private Address sponsor;
    private Long initialHeight = 0L;
    private Nuls amount = Nuls.ZERO;
    private Nuls balance = Nuls.ZERO;
    private Byte parts = 0;
    private Boolean random = true;//Decide whether or not the red envelope is Shared equally
    private String remark = "Winer Winer Chicken Dinner!";
    private Boolean available = true;
    private Map<Address, SnatchRedEnvelopeEvent> map;

    public Long getInitialHeight() {
        return initialHeight;
    }

    public void setInitialHeight(Long initialHeight) {
        this.initialHeight = initialHeight;
    }

    public Nuls getBalance() {
        return balance;
    }

    public void setBalance(Nuls balance) {
        this.balance = balance;
    }

    public Byte getParts() {
        return parts;
    }

    public void setParts(Byte parts) {
        this.parts = parts;
    }

    public Map<Address, SnatchRedEnvelopeEvent> getMap() {
        return map;
    }

    public void setMap(Map<Address, SnatchRedEnvelopeEvent> map) {
        this.map = map;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Address getSponsor() {
        return sponsor;
    }

    public void setSponsor(Address sponsor) {
        this.sponsor = sponsor;
    }

    public Nuls getAmount() {
        return amount;
    }

    public void setAmount(Nuls amount) {
        this.amount = amount;
    }

    public Boolean getRandom() {
        return random;
    }

    public void setRandom(Boolean random) {
        this.random = random;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RedEnvelopeEntity entity = (RedEnvelopeEntity) o;

        if (id != null ? !id.equals(entity.id) : entity.id != null) return false;
        if (sponsor != null ? !sponsor.equals(entity.sponsor) : entity.sponsor != null) return false;
        if (initialHeight != null ? !initialHeight.equals(entity.initialHeight) : entity.initialHeight != null)
            return false;
        if (amount != null ? !amount.equals(entity.amount) : entity.amount != null) return false;
        if (balance != null ? !balance.equals(entity.balance) : entity.balance != null) return false;
        if (parts != null ? !parts.equals(entity.parts) : entity.parts != null) return false;
        if (random != null ? !random.equals(entity.random) : entity.random != null) return false;
        if (remark != null ? !remark.equals(entity.remark) : entity.remark != null) return false;
        if (available != null ? !available.equals(entity.available) : entity.available != null) return false;
        return map != null ? map.equals(entity.map) : entity.map == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (sponsor != null ? sponsor.hashCode() : 0);
        result = 31 * result + (initialHeight != null ? initialHeight.hashCode() : 0);
        result = 31 * result + (amount != null ? amount.hashCode() : 0);
        result = 31 * result + (balance != null ? balance.hashCode() : 0);
        result = 31 * result + (parts != null ? parts.hashCode() : 0);
        result = 31 * result + (random != null ? random.hashCode() : 0);
        result = 31 * result + (remark != null ? remark.hashCode() : 0);
        result = 31 * result + (available != null ? available.hashCode() : 0);
        result = 31 * result + (map != null ? map.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "{\"RedEnvelopeEntity\":{"
                + "\"id\":\"" + id + "\""
                + ", \"sponsor\":" + sponsor
                + ", \"initialHeight\":\"" + initialHeight + "\""
                + ", \"amount\":" + amount
                + ", \"balance\":" + balance
                + ", \"parts\":\"" + parts + "\""
                + ", \"random\":\"" + random + "\""
                + ", \"remark\":\"" + remark + "\""
                + ", \"available\":\"" + available + "\""
                + ", \"map\":" + map
                + "}}";
    }
}
