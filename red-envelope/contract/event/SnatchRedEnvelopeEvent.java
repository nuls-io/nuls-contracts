package contract.event;

import contract.util.Nuls;
import io.nuls.contract.sdk.Address;
import io.nuls.contract.sdk.Event;

public class SnatchRedEnvelopeEvent implements Event {
    private Long id;
    private Address snatcher;
    private Nuls nuls;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Address getSnatcher() {
        return snatcher;
    }

    public void setSnatcher(Address snatcher) {
        this.snatcher = snatcher;
    }

    public Nuls getNuls() {
        return nuls;
    }

    public void setNuls(Nuls nuls) {
        this.nuls = nuls;
    }

    public SnatchRedEnvelopeEvent() {
    }

    public SnatchRedEnvelopeEvent(Long id, Address snatcher, Nuls nuls) {
        this.id = id;
        this.snatcher = snatcher;
        this.nuls = nuls;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SnatchRedEnvelopeEvent that = (SnatchRedEnvelopeEvent) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (snatcher != null ? !snatcher.equals(that.snatcher) : that.snatcher != null) return false;
        return nuls != null ? nuls.equals(that.nuls) : that.nuls == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (snatcher != null ? snatcher.hashCode() : 0);
        result = 31 * result + (nuls != null ? nuls.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SnatchRedEnvelopeEvent{" +
                "id=" + id +
                ", snatcher=" + snatcher +
                ", nuls=" + nuls +
                '}';
    }
}
