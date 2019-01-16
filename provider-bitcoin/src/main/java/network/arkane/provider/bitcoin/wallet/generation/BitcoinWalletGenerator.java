package network.arkane.provider.bitcoin.wallet.generation;

import com.google.protobuf.ByteString;
import network.arkane.provider.bitcoin.secret.generation.BitcoinSecretKey;
import network.arkane.provider.wallet.generation.WalletGenerator;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.crypto.KeyCrypterScrypt;
import org.bitcoinj.wallet.Protos;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class BitcoinWalletGenerator implements WalletGenerator<BitcoinSecretKey> {

    private NetworkParameters networkParams;

    public BitcoinWalletGenerator(NetworkParameters networkParams) {
        this.networkParams = networkParams;
    }

    @Override
    public GeneratedBitcoinWallet generateWallet(final String password, final BitcoinSecretKey secret) {
        if (StringUtils.isEmpty(password)) {
            throw new IllegalArgumentException("Password should not be empty");
        }

        byte[] salt = KeyCrypterScrypt.randomSalt();

        ECKey encryptedEcKey = encrypt(secret.getKey(), password, salt);
        SegwitAddress segwitAddress = new SegwitAddress(secret.getKey(), networkParams);
        return GeneratedBitcoinWallet.builder()
                                     .address(segwitAddress.getAddress().toBase58())
                                     .secret(new BitcoinKeystore(encryptedEcKey.getPubKey(),
                                                                 encryptedEcKey.getEncryptedData().initialisationVector,
                                                                 encryptedEcKey.getEncryptedData().encryptedBytes, salt))
                                     .build();
    }

    private ECKey encrypt(ECKey key, String password, byte[] salt) {
        Protos.ScryptParameters params = Protos.ScryptParameters.newBuilder().setSalt(
                ByteString.copyFrom(salt)).build();
        final KeyCrypterScrypt scrypt = new KeyCrypterScrypt(params);
        return key.encrypt(scrypt, scrypt.deriveKey(password));

    }

    @Override
    public Class<BitcoinSecretKey> type() {
        return BitcoinSecretKey.class;
    }
}
