package network.arkane.provider.bitcoin.sign;

import lombok.Builder;
import lombok.Data;
import network.arkane.provider.sign.domain.Signable;

import java.math.BigInteger;

@Data
@Builder
public class BitcoinTransactionSignable implements Signable {
    private String address;
    private BigInteger satoshiValue;
}
