package network.arkane.provider.bitcoin.sign;

import network.arkane.provider.bitcoin.secret.generation.BitcoinSecretKey;
import network.arkane.provider.exceptions.ArkaneException;
import network.arkane.provider.sign.domain.Signature;
import network.arkane.provider.sign.domain.TransactionSignature;
import org.apache.commons.codec.binary.Hex;
import org.bitcoinj.core.Base58;
import org.bitcoinj.core.DumpedPrivateKey;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BitcoinTransactionSignerTest {

    private BitcoinTransactionSigner bitcoinTransactionSigner;
    private BitcoinTransactionFactory transactionFactory;


    @BeforeEach
    void setUp() {
        transactionFactory = mock(BitcoinTransactionFactory.class);
        bitcoinTransactionSigner = new BitcoinTransactionSigner(transactionFactory);
    }

    @Test
    void signsCorrectly() {
        final BitcoinTransactionSignable transactionSignable = BitcoinTransactionSignable.builder()
                                                                                         .satoshiValue(BigInteger.ONE)
                                                                                         .address("mhSwuar1U3Hf6phh2LMkFefkjCgFt8Xg5H")
                                                                                         .build();
        final BitcoinSecretKey bitcoinSecretKey = BitcoinSecretKey.builder()
                                                                  .key(DumpedPrivateKey.fromBase58(NetworkParameters.testNet(), "92JYtSuKyhrG1fVgtBXUQgT8yNGs6XFFCjz1XLCwg8jFM95GHB6").getKey())
                                                                  .build();
        final Transaction transaction = mock(Transaction.class);
        final byte[] bitcoinSerialize = new byte[0];

        when(transaction.bitcoinSerialize()).thenReturn(bitcoinSerialize);
        when(transactionFactory.createBitcoinTransaction(transactionSignable, bitcoinSecretKey)).thenReturn(transaction);

        final Signature signature = bitcoinTransactionSigner.createSignature(transactionSignable, bitcoinSecretKey);

        assertThat(signature).isInstanceOf(TransactionSignature.class);
        assertThat(((TransactionSignature) signature).getSignedTransaction()).isEqualTo(Hex.encodeHexString(bitcoinSerialize));
    }


    @Test
    void cantSignWhenTxFactoryThrowsArkaneException() {
        final BitcoinTransactionSignable transactionSignable = BitcoinTransactionSignable.builder()
                                                                                         .satoshiValue(BigInteger.ONE)
                                                                                         .address("mhSwuar1U3Hf6phh2LMkFefkjCgFt8Xg5H")
                                                                                         .build();
        final BitcoinSecretKey bitcoinSecretKey = BitcoinSecretKey.builder()
                                                                  .key(ECKey.fromPrivate(Base58.decode("92JYtSuKyhrG1fVgtBXUQgT8yNGs6XFFCjz1XLCwg8jFM95GHB6")))
                                                                  .build();

        doThrow(new ArkaneException("Test", "test.code")).when(transactionFactory).createBitcoinTransaction(transactionSignable, bitcoinSecretKey);

        assertThatThrownBy(() -> bitcoinTransactionSigner.createSignature(transactionSignable, bitcoinSecretKey)).hasMessageContaining("Test");
    }

}