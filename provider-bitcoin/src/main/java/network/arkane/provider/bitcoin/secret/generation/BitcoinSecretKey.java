package network.arkane.provider.bitcoin.secret.generation;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import network.arkane.provider.chain.SecretType;
import network.arkane.provider.wallet.domain.SecretKey;
import org.bitcoinj.core.ECKey;


@Data
@NoArgsConstructor
public class BitcoinSecretKey implements SecretKey {

    private ECKey key;

    @Override
    public SecretType type() {
        return SecretType.BITCOIN;
    }

    @Builder
    public BitcoinSecretKey(ECKey key) {
        this.key = key;
    }
}
