package network.arkane.provider.bitcoin.sign;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import network.arkane.provider.bitcoin.BitcoinEnv;
import network.arkane.provider.bitcoin.secret.generation.BitcoinSecretKey;
import network.arkane.provider.bitcoin.unspent.Unspent;
import network.arkane.provider.bitcoin.unspent.UnspentService;
import network.arkane.provider.exceptions.ArkaneException;
import org.apache.commons.codec.DecoderException;
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
import org.bitcoinj.core.TransactionOutput;
import org.bitcoinj.crypto.TransactionSignature;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
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
            addInputsAndOutputsToTransaction(fromAddress, tx, amount.value, signable.getFeePerByte());
            signInputsOfTransaction(fromAddress, tx, secretKey.getKey());
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
                                 .message(String.format("An error occurred trying to create the Bitcoin transaction: %s", ex.getMessage()))
                                 .build();
        }
    }

    private List<Unspent> fetchUnspents(final Address address) {
        final List<Unspent> unspentForAddress = unspentService.getUnspentForAddress(address);
        if (unspentForAddress.isEmpty()) {
            throw ArkaneException.arkaneException()
                                 .errorCode("bitcoin.transaction-inputs")
                                 .message("The account you're trying to use as origin in the transaction doesn't has valid inputs to send")
                                 .build();
        }
        return unspentForAddress;
    }

    private void signInputsOfTransaction(final Address sourceAddress, final Transaction tx, final ECKey key) {
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
    private void addInputsAndOutputsToTransaction(final Address sourceAddress, final Transaction tx, final Long requiredAmount, int feePerByte) {

        final Iterator<Unspent> unspentIterator = fetchUnspents(sourceAddress).iterator();
        boolean isRequiredAmountCovered = false;
        long gatheredAmount = 0L;

        while (unspentIterator.hasNext() && !isRequiredAmountCovered) {
            final Unspent unspent = unspentIterator.next();

            gatheredAmount += unspent.getAmount();
            tx.addInput(createTransactionInput(tx, unspent));

            if (gatheredAmount >= requiredAmount) {
                isRequiredAmountCovered = addChangeAndTxFee(sourceAddress, tx, requiredAmount, gatheredAmount, feePerByte);
            }
        }

        if (gatheredAmount < requiredAmount) {
            throw ArkaneException.arkaneException()
                                 .errorCode("bitcoin.not-enough-funds")
                                 .message("Not enough funds to create the transaction")
                                 .build();
        }
    }

    private boolean addChangeAndTxFee(final Address sourceAddress, final Transaction tx, final long requiredAmount, final long gatheredAmount, final int feePerByte) {
        final List<TransactionOutput> initialOutputs = new ArrayList<>(tx.getOutputs());

        // Temporary add change-output (no fee applied yet)
        tx.addOutput(Coin.valueOf((gatheredAmount - requiredAmount)), sourceAddress);

        // Calculate the fee
        final long fee = calculateTxFee(tx, feePerByte);

        // Remove the change-output
        resetOutputs(tx, initialOutputs);

        if (gatheredAmount >= (requiredAmount + fee)) {

            final Coin change = Coin.valueOf((gatheredAmount - requiredAmount - fee));
            // Add change-output - fee
            if (change.value > 0) {
                tx.addOutput(change, sourceAddress);
            }
            return true;
        }
        return false;
    }

    private long calculateTxFee(final Transaction tx, final int feePerByte) {
        return new BigInteger(String.valueOf(tx.getMessageSize())).multiply(new BigInteger(String.valueOf(feePerByte))).longValue();
    }

    private void resetOutputs(final Transaction tx, final List<TransactionOutput> initialOutputs) {
        tx.clearOutputs();
        initialOutputs.forEach(tx::addOutput);
    }

    private TransactionInput createTransactionInput(final Transaction tx, final Unspent unspent) throws DecoderException {
        final TransactionOutPoint outPoint = new TransactionOutPoint(networkParameters, unspent.getVOut(), Sha256Hash.wrap(unspent.getTxId()));
        return new TransactionInput(networkParameters, tx, Hex.decodeHex(unspent.getScriptPubKey()), outPoint, Coin.valueOf(unspent.getAmount()));
    }
}
