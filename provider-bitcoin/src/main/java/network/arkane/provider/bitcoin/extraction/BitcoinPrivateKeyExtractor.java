package network.arkane.provider.bitcoin.extraction;

import network.arkane.provider.bitcoin.secret.generation.BitcoinSecretKey;
import network.arkane.provider.wallet.extraction.SecretExtractor;
import org.bitcoinj.core.DumpedPrivateKey;
import org.bitcoinj.core.NetworkParameters;
import org.springframework.stereotype.Component;

@Component
public class BitcoinPrivateKeyExtractor implements SecretExtractor<BitcoinPrivateKeyExtractionRequest> {

    private NetworkParameters networkParameters;

    public BitcoinPrivateKeyExtractor(NetworkParameters networkParameters) {
        this.networkParameters = networkParameters;
    }

    @Override
    public BitcoinSecretKey extract(BitcoinPrivateKeyExtractionRequest extractionRequest) {
        DumpedPrivateKey dumpedPrivateKey = DumpedPrivateKey.fromBase58(networkParameters, extractionRequest.getPrivateKey());

        return BitcoinSecretKey.builder()
                               .key(dumpedPrivateKey.getKey())
                               .build();
    }

    @Override
    public Class<BitcoinPrivateKeyExtractionRequest> getImportRequestType() {
        return BitcoinPrivateKeyExtractionRequest.class;
    }
}
