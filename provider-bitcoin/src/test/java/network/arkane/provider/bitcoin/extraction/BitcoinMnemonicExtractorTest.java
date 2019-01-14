package network.arkane.provider.bitcoin.extraction;

import network.arkane.provider.bitcoin.secret.generation.BitcoinSecretKey;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.store.BlockStoreException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.UnknownHostException;

import static org.assertj.core.api.Assertions.assertThat;

class BitcoinMnemonicExtractorTest {

    private BitcoinMnemonicExtractor extractor;

    @BeforeEach
    void setUp() {
        extractor = new BitcoinMnemonicExtractor(TestNet3Params.get());
    }

//    @Test
//    void importMnemonic() throws UnknownHostException, BlockStoreException {
//        BitcoinSecretKey secretKey = extractor.extract(new BitcoinMnemonicExtractionRequest("picnic such web toilet weekend inspire clown shine tourist label stereo leg", ""));
//
//        assertThat(secretKey.getWallet()).isNotNull();
//    }
}