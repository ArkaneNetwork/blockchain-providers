package network.arkane.provider.bitcoin.wallet.exporting;

import network.arkane.provider.JSONUtil;
import network.arkane.provider.bitcoin.BitcoinEnv;
import network.arkane.provider.bitcoin.bip38.BIP38EncryptionService;
import network.arkane.provider.bitcoin.secret.generation.BitcoinSecretGenerator;
import network.arkane.provider.bitcoin.secret.generation.BitcoinSecretKey;
import network.arkane.provider.bitcoin.wallet.generation.BitcoinWalletGenerator;
import network.arkane.provider.bitcoin.wallet.generation.GeneratedBitcoinWallet;
import network.arkane.provider.blockcypher.Network;
import org.apache.commons.codec.binary.Base64;
import org.bitcoinj.core.DumpedPrivateKey;
import org.bitcoinj.params.TestNet3Params;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BitcoinKeyExporterTest {

    private BitcoinKeyExporter bitcoinKeyExporter;
    private BIP38EncryptionService bip38EncryptionService;

    @BeforeEach
    void setUp() {
        this.bip38EncryptionService = mock(BIP38EncryptionService.class);
        this.bitcoinKeyExporter = new BitcoinKeyExporter(bip38EncryptionService);
    }

    @Test
    void export() {
        final String password = "password";
        final String key = "92JYtSuKyhrG1fVgtBXUQgT8yNGs6XFFCjz1XLCwg8jFM95GHB6";

        DumpedPrivateKey pk = DumpedPrivateKey.fromBase58(TestNet3Params.get(), key);

        final String expected = "exported";
        when(bip38EncryptionService.encrypt(any(BitcoinSecretKey.class), eq(password)))
                .thenReturn(expected);

        final String export = bitcoinKeyExporter.export(BitcoinSecretKey.builder().key(pk.getKey()).build(), password);

        final ExportedBitcoinKey exportedBitcoinKey = JSONUtil.fromJson(export, ExportedBitcoinKey.class);
        assertThat(exportedBitcoinKey.getValue()).isEqualTo(expected);
        assertThat(exportedBitcoinKey.getType()).isEqualTo("BIP38");
    }

    @Test
    void exportButError() {
        final String password = "password";
        final String key = "92JYtSuKyhrG1fVgtBXUQgT8yNGs6XFFCjz1XLCwg8jFM95GHB6";
        DumpedPrivateKey pk = DumpedPrivateKey.fromBase58(TestNet3Params.get(), key);

        doThrow(IllegalArgumentException.class).when(bip38EncryptionService).encrypt(any(BitcoinSecretKey.class), eq(password));
        assertThatThrownBy(() -> bitcoinKeyExporter.export(
                BitcoinSecretKey.builder().key(pk.getKey()).build(), password)).hasMessageContaining("An error occurred while trying to export the key");
    }

    @Test
    void reconstructsKey() {
        final BitcoinWalletGenerator bitcoinWalletGenerator = new BitcoinWalletGenerator(new BitcoinEnv(Network.BTC, TestNet3Params.get()));
        final String pwd = "test";
        final BitcoinSecretKey originalSecretKey = new BitcoinSecretGenerator().generate();
        final GeneratedBitcoinWallet test = bitcoinWalletGenerator.generateWallet(pwd, originalSecretKey);

        final BitcoinSecretKey reconstructedPrivateKey = bitcoinKeyExporter.reconstructKey(new String(Base64.decodeBase64(JSONUtil.toJson(test.secretAsBase64()))), pwd);
        assertThat(reconstructedPrivateKey.getKey().getPrivateKeyAsHex()).isEqualTo(reconstructedPrivateKey.getKey().getPrivateKeyAsHex());
    }
}