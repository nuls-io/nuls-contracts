package contract.func;

import contract.util.Nuls;

public interface RedEnvelopeConstant {
    Integer UNAVAILABLE_HEIGHT = 3;//24 hours
    Nuls MAX = Nuls.ONE.multiply(20L);
    Nuls MIN = Nuls.MIN_TRANSFER;
}
