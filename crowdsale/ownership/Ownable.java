package ownership;

import io.nuls.contract.sdk.Address;
import io.nuls.contract.sdk.Event;

public interface Ownable {

    Address getOwner();

    void onlyOwner();

    void transferOwnership(Address newOwner);

    void renounceOwnership();
     //放弃拥有者
    class OwnershipRenouncedEvent implements Event {

        private Address previousOwner;//先前拥有者

        public OwnershipRenouncedEvent(Address previousOwner) {
            this.previousOwner = previousOwner;
        }

        public Address getPreviousOwner() {
            return previousOwner;
        }

        public void setPreviousOwner(Address previousOwner) {
            this.previousOwner = previousOwner;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            OwnershipRenouncedEvent that = (OwnershipRenouncedEvent) o;

            return previousOwner != null ? previousOwner.equals(that.previousOwner) : that.previousOwner == null;
        }

        @Override
        public int hashCode() {
            return previousOwner != null ? previousOwner.hashCode() : 0;
        }

        @Override
        public String toString() {
            return "OwnershipRenouncedEvent{" +
                    "previousOwner=" + previousOwner +
                    '}';
        }

    }

    /**
     * 转移owner
     */
    class OwnershipTransferredEvent implements Event {

        private Address previousOwner;//先前拥有者

        private Address newOwner;//新的拥有者

        public OwnershipTransferredEvent(Address previousOwner, Address newOwner) {
            this.previousOwner = previousOwner;
            this.newOwner = newOwner;
        }

        public Address getPreviousOwner() {
            return previousOwner;
        }

        public void setPreviousOwner(Address previousOwner) {
            this.previousOwner = previousOwner;
        }

        public Address getNewOwner() {
            return newOwner;
        }

        public void setNewOwner(Address newOwner) {
            this.newOwner = newOwner;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            OwnershipTransferredEvent that = (OwnershipTransferredEvent) o;

            if (previousOwner != null ? !previousOwner.equals(that.previousOwner) : that.previousOwner != null)
                return false;
            return newOwner != null ? newOwner.equals(that.newOwner) : that.newOwner == null;
        }

        @Override
        public int hashCode() {
            int result = previousOwner != null ? previousOwner.hashCode() : 0;
            result = 31 * result + (newOwner != null ? newOwner.hashCode() : 0);
            return result;
        }

        @Override
        public String toString() {
            return "OwnershipTransferredEvent{" +
                    "previousOwner=" + previousOwner +
                    ", newOwner=" + newOwner +
                    '}';
        }

    }

}
