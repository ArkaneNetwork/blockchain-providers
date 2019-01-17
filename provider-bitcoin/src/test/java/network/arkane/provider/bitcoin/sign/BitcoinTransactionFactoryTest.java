package network.arkane.provider.bitcoin.sign;

import network.arkane.provider.bitcoin.BitcoinEnv;
import network.arkane.provider.bitcoin.secret.generation.BitcoinSecretKey;
import network.arkane.provider.bitcoin.unspent.Unspent;
import network.arkane.provider.bitcoin.unspent.UnspentService;
import network.arkane.provider.sochain.domain.Network;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Base58;
import org.bitcoinj.core.DumpedPrivateKey;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.params.TestNet3Params;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BitcoinTransactionFactoryTest {

    private UnspentService unspentService;

    private BitcoinTransactionFactory bitcoinTransactionFactory;


    @BeforeEach
    void setUp() {
        unspentService = mock(UnspentService.class);
        bitcoinTransactionFactory = new BitcoinTransactionFactory(new BitcoinEnv(Network.BTCTEST, TestNet3Params.get()), unspentService);
    }

    @Test
    void createBitcoinTransaction() {
        final BitcoinTransactionSignable transactionSignable = BitcoinTransactionSignable.builder()
                                                                                         .satoshiValue(BigInteger.ONE)
                                                                                         .address("mhSwuar1U3Hf6phh2LMkFefkjCgFt8Xg5H")
                                                                                         .build();
        final BitcoinSecretKey bitcoinSecretKey = BitcoinSecretKey.builder()
                                                                  .key(DumpedPrivateKey.fromBase58(NetworkParameters.testNet(),
                                                                                                   "92JYtSuKyhrG1fVgtBXUQgT8yNGs6XFFCjz1XLCwg8jFM95GHB6")
                                                                                       .getKey())
                                                                  .build();

        when(unspentService.getUnspentForAddress(any(Address.class))).thenReturn(Arrays.asList(Unspent.builder()
                                                                                                      .amount(14482185)
                                                                                                      .scriptPubKey("76a91464d12bfa319ec47fe040bf6d2dbb3013d85f552588ac")
                                                                                                      .txId("0f496014bbb15adfd924a2b2ce8c16a4b6076240e4428d96b344d66adbb14c0b")
                                                                                                      .vOut(1).build()));

        final Transaction tx = bitcoinTransactionFactory.createBitcoinTransaction(transactionSignable, bitcoinSecretKey);

        assertThat(tx.getOutputs()).hasSize(2)
                                   .extracting((transactionOutput) -> transactionOutput.getValue().value)
                                   .contains(BigInteger.ONE.longValue());

        assertThat(tx.getInputs()).hasSize(1).extracting((transactionInput) -> transactionInput.getValue().value).containsOnly(14482185L);
    }

    @Test
    void cantSignWithoutUnspents() {
        assertThatThrownBy(() -> bitcoinTransactionFactory.createBitcoinTransaction(BitcoinTransactionSignable.builder()
                                                                                                              .satoshiValue(BigInteger.ONE)
                                                                                                              .address("mhSwuar1U3Hf6phh2LMkFefkjCgFt8Xg5H")
                                                                                                              .build(),
                                                                                    BitcoinSecretKey.builder()
                                                                                                    .key(ECKey.fromPrivate(
                                                                                                            Base58.decode("92JYtSuKyhrG1fVgtBXUQgT8yNGs6XFFCjz1XLCwg8jFM95GHB6"))
                                                                                                        )
                                                                                                    .build()))
                .hasMessageContaining("The account you're trying to use as origin in the transaction doesn't has valid inputs to send");
    }

    @Test
    void notEnoughFunds() {
        when(unspentService.getUnspentForAddress(any(Address.class))).thenReturn(Arrays.asList(
                Unspent
                        .builder()
                        .amount(14482185)
                        .scriptPubKey("76a91464d12bfa319ec47fe040bf6d2dbb3013d85f552588ac")
                        .txId("0f496014bbb15adfd924a2b2ce8c16a4b6076240e4428d96b344d66adbb14c0b")
                        .vOut(1).build()));
        assertThatThrownBy(() -> bitcoinTransactionFactory.createBitcoinTransaction(BitcoinTransactionSignable.builder()
                                                                                                              .satoshiValue(BigInteger.valueOf(14482195))
                                                                                                              .address("mhSwuar1U3Hf6phh2LMkFefkjCgFt8Xg5H")
                                                                                                              .build(),
                                                                                    BitcoinSecretKey.builder()
                                                                                                    .key(ECKey.fromPrivate(
                                                                                                            Base58.decode("92JYtSuKyhrG1fVgtBXUQgT8yNGs6XFFCjz1XLCwg8jFM95GHB6"))
                                                                                                        )
                                                                                                    .build()))
                .hasMessageContaining("Not enough funds to send the transaction");

    }
}
