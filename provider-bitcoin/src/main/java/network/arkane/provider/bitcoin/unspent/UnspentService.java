package network.arkane.provider.bitcoin.unspent;

import org.apache.commons.lang3.NotImplementedException;
import org.bitcoinj.core.Address;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UnspentService {

    public List<Unspent> getUnspentForAddress(final Address address) {
        throw new NotImplementedException("");
    }
}
