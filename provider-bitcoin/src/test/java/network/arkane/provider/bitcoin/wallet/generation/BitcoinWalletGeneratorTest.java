package network.arkane.provider.bitcoin.wallet.generation;

import com.google.protobuf.ByteString;
import network.arkane.provider.JSONUtil;
import network.arkane.provider.bitcoin.secret.generation.BitcoinSecretGenerator;
import network.arkane.provider.bitcoin.secret.generation.BitcoinSecretKey;
import org.apache.commons.codec.binary.Base64;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.crypto.EncryptedData;
import org.bitcoinj.crypto.KeyCrypterScrypt;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.wallet.Protos;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.UnknownHostException;

import static org.assertj.core.api.Assertions.assertThat;

class BitcoinWalletGeneratorTest {

    private BitcoinWalletGenerator walletGenerator;

    @BeforeEach
    void setUp() {
        walletGenerator = new BitcoinWalletGenerator(TestNet3Params.get());
    }

    @Test
    void generatesWallet() throws UnknownHostException {

        GeneratedBitcoinWallet wallet = walletGenerator.generateWallet("flqksjfqklm",
                                                                       new BitcoinSecretGenerator().generate());

        assertThat(wallet.getAddress()).isNotBlank();
        assertThat(wallet.secretAsBase64()).isNotBlank();

    }

    @Test
    void walletCanBeDecrypted() {
        String password = "flqksjfqklm";

        BitcoinSecretKey secretKey = new BitcoinSecretGenerator().generate();
        System.out.println(secretKey.getKey().getPrivateKeyAsHex());

        GeneratedBitcoinWallet wallet = walletGenerator.generateWallet(password,
                                                                       secretKey);

        String secret = wallet.secretAsBase64();
        BitcoinKeystore ed = JSONUtil.fromJson(new String(Base64.decodeBase64(secret)), BitcoinKeystore.class);
        Protos.ScryptParameters params = Protos.ScryptParameters.newBuilder().setSalt(ByteString.copyFrom("".getBytes())).build();
        KeyCrypterScrypt crypter = new KeyCrypterScrypt(params);
        EncryptedData encryptedData = new EncryptedData(ed.getInitialisationVector(), ed.getEncryptedBytes());
        ECKey ecKey = ECKey.fromEncrypted(encryptedData, crypter, ed.getPubKey());
        ECKey unencrypted = ecKey.decrypt(crypter.deriveKey(password));


        System.out.println(unencrypted.getPrivateKeyAsHex());

        //        ECKey.fromEncrypted(privateKey)
    }


    @Test
    void blah() {
        //
        //        ECKey k1 = new ECKey(); // some random key
        //
        //        // encrypting a key
        //        KeyCrypter crypter1 = new KeyCrypterScrypt();
        //        KeyParameter aesKey1 = crypter1.deriveKey("some arbitrary passphrase");
        //        ECKey k2 = k1.encrypt(crypter1, aesKey1);
        //        ECKey.fromEncrypted());
        //        System.out.println(k2.isEncrypted()); // true
    }
}