package network.arkane.provider.bitcoin.extraction;

import network.arkane.provider.bitcoin.secret.generation.BitcoinSecretKey;
import org.bitcoinj.core.AddressFormatException;
import org.bitcoinj.core.Base58;
import org.bitcoinj.params.TestNet3Params;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BitcoinPrivateKeyExtractorTest {

    private BitcoinPrivateKeyExtractor extractor;

    @BeforeEach
    void setUp() {
        extractor = new BitcoinPrivateKeyExtractor(TestNet3Params.get());
    }

    @Test
    void extract() {
        String privateKey = "92Pg46rUhgTT7romnV7iGW6W1gbGdeezqdbJCzShkCsYNzyyNcc";
        BitcoinSecretKey result = extractor.extract(new BitcoinPrivateKeyExtractionRequest(privateKey));

        assertThat(result.getKey().getPrivateKeyAsWiF(TestNet3Params.get())).isEqualTo(privateKey);
    }

}