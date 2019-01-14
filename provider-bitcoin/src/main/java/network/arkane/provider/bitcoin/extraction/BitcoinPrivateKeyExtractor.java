package network.arkane.provider.bitcoin.extraction;

import network.arkane.provider.bitcoin.secret.generation.BitcoinSecretKey;
import network.arkane.provider.wallet.extraction.SecretExtractor;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.wallet.Wallet;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class BitcoinPrivateKeyExtractor implements SecretExtractor<BitcoinPrivateKeyExtractionRequest> {

    private NetworkParameters networkParameters;

    public BitcoinPrivateKeyExtractor(NetworkParameters networkParameters) {
        this.networkParameters = networkParameters;
    }

    @Override
    public BitcoinSecretKey extract(BitcoinPrivateKeyExtractionRequest extractionRequest) {
        Wallet wallet = Wallet.fromKeys(networkParameters,
                                        Collections.singletonList(ECKey.fromPrivate(extractionRequest.getPrivateKey().getBytes())));
        return BitcoinSecretKey.builder()
                               .wallet(wallet)
                               .build();
    }

    @Override
    public Class<BitcoinPrivateKeyExtractionRequest> getImportRequestType() {
        return BitcoinPrivateKeyExtractionRequest.class;
    }
}
