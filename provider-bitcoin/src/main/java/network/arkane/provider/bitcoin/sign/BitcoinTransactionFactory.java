package network.arkane.provider.bitcoin.sign;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import network.arkane.provider.bitcoin.BitcoinEnv;
import network.arkane.provider.bitcoin.secret.generation.BitcoinSecretKey;
import network.arkane.provider.bitcoin.unspent.Unspent;
import network.arkane.provider.bitcoin.unspent.UnspentService;
import network.arkane.provider.exceptions.ArkaneException;
import org.apache.commons.codec.binary.Hex;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.ScriptException;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.TransactionInput;
import org.bitcoinj.core.TransactionOutPoint;
import org.bitcoinj.crypto.TransactionSignature;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.IntStream;

@Slf4j
@Component
public class BitcoinTransactionFactory {

    private final NetworkParameters networkParameters;
    private final UnspentService unspentService;

    public BitcoinTransactionFactory(final BitcoinEnv bitcoinEnv, final UnspentService unspentService) {
        this.networkParameters = bitcoinEnv.getNetworkParameters();
        this.unspentService = unspentService;
    }

    public Transaction createBitcoinTransaction(final BitcoinTransactionSignable signable, final BitcoinSecretKey secretKey) {
        final Address fromAddress = new Address(networkParameters, secretKey.getKey().getPubKeyHash());
        try {
            final Transaction tx = new Transaction(networkParameters);

            final Coin amount = Coin.valueOf(signable.getSatoshiValue().longValue());
            tx.addOutput(amount, fromAddress);
            addInputsToTransaction(Address.fromBase58(networkParameters, fromAddress.toBase58()), tx, fetchUnspents(fromAddress), amount.value);
            signInputsOfTransaction(Address.fromBase58(networkParameters, fromAddress.toBase58()), tx, secretKey.getKey());
            tx.verify();
            tx.setPurpose(Transaction.Purpose.USER_PAYMENT);
            return tx;
        } catch (final ArkaneException ex) {
            log.error(ex.getMessage());
            throw ex;
        } catch (final Exception ex) {
            log.error(ex.getMessage());
            throw ArkaneException.arkaneException()
                                 .errorCode("bitcoin.creation-error")
                                 .message(String.format("An error occurred trying to create the bitcoin transaction: %s", ex.getMessage()))
                                 .build();
        }
    }

    private List<Unspent> fetchUnspents(final Address address) {
        final List<Unspent> unspentForAddress = unspentService.getUnspentForAddress(address);
        if (unspentForAddress.isEmpty()) {
            throw ArkaneException.arkaneException()
                                 .errorCode("bitcoin.transaction-inputs")
                                 .message(String.format("The account you're trying to use as origin in the transaction doesn't has valid inputs to send"))
                                 .build();
        }
        return unspentForAddress;
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
        final long requiredAmount = amount;

        long gatheredAmount = 0L;
        for (final Unspent unspent : unspents) {
            gatheredAmount += unspent.getAmount();
            TransactionOutPoint outPoint = new TransactionOutPoint(networkParameters, unspent.getVOut(), Sha256Hash.wrap(unspent.getTxId()));
            TransactionInput transactionInput = new TransactionInput(networkParameters, tx, Hex.decodeHex(unspent.getScriptPubKey()),
                                                                     outPoint, Coin.valueOf(unspent.getAmount()));
            tx.addInput(transactionInput);

            if (gatheredAmount >= requiredAmount) {
                break;
            }
        }

        if (gatheredAmount < requiredAmount) {
            throw ArkaneException.arkaneException()
                                 .errorCode("bitcoin.not-enough-funds")
                                 .message(String.format("Not enough funds to send the transaction"))
                                 .build();
        }

        if (gatheredAmount > requiredAmount) {
            //return change to sender, in real life it should use different address
            tx.addOutput(Coin.valueOf((gatheredAmount - requiredAmount)), sourceAddress);
        }
    }
}
