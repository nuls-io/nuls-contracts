package contract.util;

import contract.event.SnatchRedEnvelopeEvent;
import contract.model.RedEnvelopeEntity;
import io.nuls.contract.sdk.Address;
import io.nuls.contract.sdk.Block;

import java.math.BigInteger;

import static io.nuls.contract.sdk.Utils.*;

/**
 * Responsible for red envelope amount calculation
 *
 * @author captain
 * @version 1.0
 * @date 19-1-31 下午12:51
 */
public class RedEnvelopeManager {

    public static Nuls snatch(RedEnvelopeEntity entity, Address sender) {
        Nuls nuls;
        if (entity.getParts() - entity.getMap().size() == 1) {
            nuls = entity.getBalance();
        } else {
            if (entity.getRandom()) {
                int remain = entity.getParts() - entity.getMap().size();
                Nuls expect = entity.getBalance().divide(remain);
                int mod1 = (int) expect.multiply(2).divide(Nuls.MIN_TRANSFER).value();
                int mod2 = (int) entity.getBalance().minus(Nuls.MIN_TRANSFER.multiply(remain - 1)).divide(Nuls.MIN_TRANSFER).value();
                int mod = Math.min(mod1, mod2);
                BigInteger randomSeed = getRandomSeed(Block.number() - 1, 100);
                randomSeed = randomSeed.add(new BigInteger(String.valueOf(entity.getMap().size())));
                BigInteger integer = randomSeed.mod(new BigInteger(String.valueOf(mod)));
                nuls = Nuls.MIN_TRANSFER.multiply(integer.intValue());
            } else {
                nuls = entity.getAmount().divide(entity.getParts());
            }
        }
        SnatchRedEnvelopeEvent event = new SnatchRedEnvelopeEvent();
        event.setId(entity.getId());
        event.setNuls(nuls);
        event.setSnatcher(sender);
        entity.getMap().put(sender, event);

        entity.setBalance(entity.getBalance().minus(nuls));
        if (entity.getMap().size() == entity.getParts()) {
            entity.setAvailable(false);
        }
        emit(event);
        sender.transfer(BigInteger.valueOf(nuls.value()));
        return nuls;
    }
}
