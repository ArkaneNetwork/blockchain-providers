package network.arkane.provider.bitcoin.sign;

import com.google.protobuf.ByteString;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import network.arkane.provider.JSONUtil;
import network.arkane.provider.bitcoin.secret.generation.BitcoinSecretKey;
import network.arkane.provider.bitcoin.unspent.Unspent;
import network.arkane.provider.bitcoin.unspent.UnspentService;
import network.arkane.provider.bitcoin.wallet.generation.BitcoinKeystore;
import network.arkane.provider.exceptions.ArkaneException;
import network.arkane.provider.sign.Signer;
import network.arkane.provider.sign.domain.Signature;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.DumpedPrivateKey;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.ScriptException;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionInput;
import org.bitcoinj.core.TransactionOutPoint;
import org.bitcoinj.crypto.EncryptedData;
import org.bitcoinj.crypto.KeyCrypterScrypt;
import org.bitcoinj.crypto.TransactionSignature;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.bitcoinj.wallet.Protos;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.IntStream;

@Component
@Slf4j
public class BitcoinTransactionSigner implements Signer<BitcoinTransactionSignable, BitcoinSecretKey> {

    private static final TestNet3Params NETWORK_PARAMS = TestNet3Params.get();
    private UnspentService unspentService;

    public BitcoinTransactionSigner(final UnspentService unspentService) {
        this.unspentService = unspentService;
    }

    @Override
    public Signature createSignature(BitcoinTransactionSignable signable, BitcoinSecretKey key) {
        final DumpedPrivateKey privateKey = DumpedPrivateKey.fromBase58(NETWORK_PARAMS, "");
        final Address fromAddress = new Address(NETWORK_PARAMS, privateKey.getKey().getPubKeyHash());

        try {
            final Transaction tx = new Transaction(NETWORK_PARAMS);
            final Coin amount = Coin.valueOf(signable.getSatoshiValue().longValue());

            tx.addOutput(amount, Address.fromBase58(NETWORK_PARAMS, privateKey.getKey().getPublicKeyAsHex()));
            addInputsToTransaction(Address.fromBase58(NETWORK_PARAMS, fromAddress.toBase58()), tx, fetchUnspents(fromAddress), amount.value);
            signInputsOfTransaction(Address.fromBase58(NETWORK_PARAMS, fromAddress.toBase58()), tx, privateKey.getKey());
            tx.verify();
            tx.setPurpose(Transaction.Purpose.USER_PAYMENT);
            return network.arkane.provider.sign.domain.TransactionSignature
                    .signTransactionBuilder()
                    .signedTransaction(Hex.encodeHexString(tx.bitcoinSerialize()))
                    .build();
        } catch (final Exception ex) {
            log.error(ex.getMessage());
            throw ArkaneException.arkaneException()
                                 .errorCode("bitcoin.signing-error")
                                 .message(String.format("An error occurred trying to sign the bitcoin transaction %s", ex.getMessage()))
                                 .build();
        }
    }

    private List<Unspent> fetchUnspents(final Address address) {
        return unspentService.getUnspentForAddress(address);
    }

    private void signInputsOfTransaction(Address sourceAddress, Transaction tx, ECKey key) {
        IntStream.range(0, tx.getInputs().size()).forEach(i -> {
            final Script scriptPubKey = ScriptBuilder.createOutputScript(sourceAddress);
            final Sha256Hash hash = tx.hashForSignature(i, scriptPubKey, Transaction.SigHash.ALL, true);
            final ECKey.ECDSASignature ecdsaSignature = key.sign(hash);
            final TransactionSignature txSignature = new TransactionSignature(ecdsaSignature, Transaction.SigHash.ALL, true);
            if (scriptPubKey.isSentToRawPubKey()) {
                tx.getInput(i).setScriptSig(ScriptBuilder.createInputScript(txSignature));
            } else {
                if (!scriptPubKey.isSentToAddress()) {
                    throw new ScriptException("Unable to sign this scriptPubKey: " + scriptPubKey);
                }
                tx.getInput(i).setScriptSig(ScriptBuilder.createInputScript(txSignature, key));
            }
        });
    }

    @SneakyThrows
    private void addInputsToTransaction(Address sourceAddress, Transaction tx, List<Unspent> unspents, Long amount) {
        long gatheredAmount = 0L;
        long requiredAmount = amount + Transaction.DEFAULT_TX_FEE.value;
        for (Unspent unspent : unspents) {
            gatheredAmount += unspent.getAmount();
            TransactionOutPoint outPoint = new TransactionOutPoint(NETWORK_PARAMS, unspent.getVOut(), Sha256Hash.wrap(unspent.getTxId()));
            TransactionInput transactionInput = new TransactionInput(NETWORK_PARAMS, tx, Hex.decodeHex(unspent.getScriptPubKey()),
                                                                     outPoint, Coin.valueOf(unspent.getAmount()));
            tx.addInput(transactionInput);

            if (gatheredAmount >= requiredAmount) {
                break;
            }
        }
        if (gatheredAmount > requiredAmount) {
            //return change to sender, in real life it should use different address
            tx.addOutput(Coin.valueOf((gatheredAmount - requiredAmount)), sourceAddress);
        }
    }

    @Override
    public BitcoinSecretKey reconstructKey(String secret, String password) {
        BitcoinKeystore ed = JSONUtil.fromJson(new String(Base64.decodeBase64(secret)), BitcoinKeystore.class);
        Protos.ScryptParameters params = Protos.ScryptParameters.newBuilder().setSalt(ByteString.copyFrom("".getBytes())).build();
        KeyCrypterScrypt crypter = new KeyCrypterScrypt(params);
        EncryptedData encryptedData = new EncryptedData(ed.getInitialisationVector(), ed.getEncryptedBytes());
        return new BitcoinSecretKey(ECKey.fromEncrypted(encryptedData, crypter, ed.getPubKey()));
    }
}
