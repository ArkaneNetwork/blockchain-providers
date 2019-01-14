package network.arkane.provider.bitcoin.extraction;

import lombok.Getter;
import network.arkane.provider.chain.SecretType;
import network.arkane.provider.wallet.extraction.request.ExtractionRequest;

@Getter
public class BitcoinMnemonicExtractionRequest extends ExtractionRequest {
    private String mnemonic;
    private String passphrase;

    public BitcoinMnemonicExtractionRequest(final String mnemonic, String passphrase) {
        super(SecretType.BITCOIN);
        this.mnemonic = mnemonic;
        this.passphrase = passphrase;
    }
}
