package contract.func;

import contract.model.RedEnvelopeEntity;
import contract.util.Nuls;
import io.nuls.contract.sdk.annotation.Payable;
import io.nuls.contract.sdk.annotation.Required;
import io.nuls.contract.sdk.annotation.View;

import java.util.List;

/**
 * Red envelope contract abstract interface
 *
 * @author captain
 * @version 1.0
 * @date 19-1-31 下午12:47
 */
public interface RedEnvelopeInterface {

    @Payable
    Long newRedEnvelope(@Required Byte parts, @Required Boolean random, String remark);

    Nuls snatchRedEnvelope(@Required Long id);

    void retrieveRedEnvelope(@Required Long id);

    @View
    RedEnvelopeEntity detailInfo(@Required Long id);

    @View
    Boolean availableInfo(@Required Long id);

    @View
    List<String> allInfo();

}
