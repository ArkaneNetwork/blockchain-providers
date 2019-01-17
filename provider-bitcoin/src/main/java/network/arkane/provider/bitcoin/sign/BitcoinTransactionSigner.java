package network.arkane.provider.bitcoin.sign;

import com.google.protobuf.ByteString;
import lombok.extern.slf4j.Slf4j;
import network.arkane.provider.JSONUtil;
import network.arkane.provider.bitcoin.secret.generation.BitcoinSecretKey;
import network.arkane.provider.bitcoin.wallet.generation.BitcoinKeystore;
import network.arkane.provider.exceptions.ArkaneException;
import network.arkane.provider.sign.Signer;
import network.arkane.provider.sign.domain.Signature;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.crypto.EncryptedData;
import org.bitcoinj.crypto.KeyCrypterScrypt;
import org.bitcoinj.wallet.Protos;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BitcoinTransactionSigner implements Signer<BitcoinTransactionSignable, BitcoinSecretKey> {

    private final BitcoinTransactionFactory transactionFactory;

    public BitcoinTransactionSigner(final BitcoinTransactionFactory transactionFactory) {
        this.transactionFactory = transactionFactory;
    }

    @Override
    public Signature createSignature(final BitcoinTransactionSignable signable, final BitcoinSecretKey secretKey) {
        try {
            final Transaction tx = transactionFactory.createBitcoinTransaction(signable, secretKey);
            return network.arkane.provider.sign.domain.TransactionSignature
                    .signTransactionBuilder()
                    .signedTransaction(Hex.encodeHexString(tx.bitcoinSerialize()))
                    .build();
        } catch (final ArkaneException ex) {
            log.error(ex.getMessage());
            throw ex;
        } catch (final Exception ex) {
            log.error(ex.getMessage());
            throw ArkaneException.arkaneException()
                                 .errorCode("bitcoin.signing-error")
                                 .message(String.format("An error occurred trying to sign the bitcoin transaction: %s", ex.getMessage()))
                                 .build();
        }
    }

    @Override
    public BitcoinSecretKey reconstructKey(final String secret, final String password) {
        BitcoinKeystore ed = JSONUtil.fromJson(new String(Base64.decodeBase64(secret)), BitcoinKeystore.class);
        Protos.ScryptParameters params = Protos.ScryptParameters.newBuilder().setSalt(ByteString.copyFrom("".getBytes())).build();
        KeyCrypterScrypt crypter = new KeyCrypterScrypt(params);
        EncryptedData encryptedData = new EncryptedData(ed.getInitialisationVector(), ed.getEncryptedBytes());
        return new BitcoinSecretKey(ECKey.fromEncrypted(encryptedData, crypter, ed.getPubKey()));
    }
}
