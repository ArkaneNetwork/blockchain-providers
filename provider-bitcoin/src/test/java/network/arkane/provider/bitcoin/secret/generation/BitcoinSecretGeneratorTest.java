package network.arkane.provider.bitcoin.secret.generation;

import org.bitcoinj.params.TestNet3Params;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BitcoinSecretGeneratorTest {

    private BitcoinSecretGenerator generator;

    @BeforeEach
    void setUp() {
        generator = new BitcoinSecretGenerator(TestNet3Params.get());
    }

    @Test
    void generate() {
        BitcoinSecretKey result = generator.generate();

        assertThat(result.getWallet()).isNotNull();
    }
}