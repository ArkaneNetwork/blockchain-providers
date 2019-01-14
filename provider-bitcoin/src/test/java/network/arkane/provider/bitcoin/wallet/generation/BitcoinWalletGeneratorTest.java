package network.arkane.provider.bitcoin.wallet.generation;

import network.arkane.provider.bitcoin.secret.generation.BitcoinSecretGenerator;
import org.bitcoinj.core.PeerAddress;
import org.bitcoinj.core.PeerGroup;
import org.bitcoinj.net.discovery.DnsDiscovery;
import org.bitcoinj.params.TestNet3Params;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

import static org.assertj.core.api.Assertions.assertThat;

class BitcoinWalletGeneratorTest {

    private BitcoinWalletGenerator walletGenerator;
    private TestNet3Params networkParams;

    @BeforeEach
    void setUp() {
        walletGenerator = new BitcoinWalletGenerator();
        networkParams = TestNet3Params.get();
    }

    @Test
    void generatesWallet() throws UnknownHostException {

        GeneratedBitcoinWallet wallet = walletGenerator.generateWallet("flqksjfqklm",
                                                                       new BitcoinSecretGenerator(networkParams).generate());

        assertThat(wallet.getAddress()).isNotBlank();
        assertThat(wallet.getWalletFile()).isNotNull();
        assertThat(wallet.secretAsBase64()).isNotBlank();

    }

    @Test
    void walletCanBeDecrypted() {
        String password = "flqksjfqklm";

        GeneratedBitcoinWallet wallet = walletGenerator.generateWallet(password,
                                                                       new BitcoinSecretGenerator(networkParams).generate());

        wallet.getWalletFile().decrypt(password);
    }

}