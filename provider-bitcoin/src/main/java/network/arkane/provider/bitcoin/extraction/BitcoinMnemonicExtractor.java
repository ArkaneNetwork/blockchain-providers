package network.arkane.provider.bitcoin.extraction;

import network.arkane.provider.bitcoin.secret.generation.BitcoinSecretKey;
import network.arkane.provider.wallet.extraction.SecretExtractor;
import org.apache.commons.lang3.StringUtils;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.wallet.DeterministicSeed;
import org.bitcoinj.wallet.UnreadableWalletException;
import org.bitcoinj.wallet.Wallet;
import org.springframework.stereotype.Component;

@Component
public class BitcoinMnemonicExtractor implements SecretExtractor<BitcoinMnemonicExtractionRequest> {

    private NetworkParameters networkParameters;

    public BitcoinMnemonicExtractor(NetworkParameters networkParameters) {
        this.networkParameters = networkParameters;
    }

    @Override
    public BitcoinSecretKey extract(BitcoinMnemonicExtractionRequest extractionRequest) {
        try {
            String passphrase = StringUtils.isBlank(extractionRequest.getPassphrase()) ? "" : extractionRequest.getPassphrase();
            DeterministicSeed seed = new DeterministicSeed(
                    extractionRequest.getMnemonic(),
                    null,
                    passphrase,
                    System.currentTimeMillis());
            return BitcoinSecretKey.builder()
                                   .wallet(Wallet.fromSeed(networkParameters, seed))
                                   .build();
        } catch (UnreadableWalletException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Class<BitcoinMnemonicExtractionRequest> getImportRequestType() {
        return BitcoinMnemonicExtractionRequest.class;
    }
}
