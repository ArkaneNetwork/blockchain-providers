package network.arkane.provider.bitcoin.extraction;

import lombok.Getter;
import network.arkane.provider.chain.SecretType;
import network.arkane.provider.wallet.extraction.request.ExtractionRequest;

@Getter
public class BitcoinPrivateKeyExtractionRequest extends ExtractionRequest {
    private String privateKey;

    public BitcoinPrivateKeyExtractionRequest(final String privateKey) {
        super(SecretType.BITCOIN);
        this.privateKey = privateKey;
    }
}
