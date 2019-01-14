package network.arkane.provider.bitcoin.unspent;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Unspent {
    private long amount;
    private long vOut;
    private String txId;
    private String scriptPubKey;
}
