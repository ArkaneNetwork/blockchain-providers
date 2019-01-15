package network.arkane.provider.bitcoin.sign;

import network.arkane.provider.bitcoin.secret.generation.BitcoinSecretKey;
import network.arkane.provider.bitcoin.unspent.Unspent;
import network.arkane.provider.bitcoin.unspent.UnspentService;
import network.arkane.provider.sign.domain.Signature;
import network.arkane.provider.sign.domain.TransactionSignature;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Base58;
import org.bitcoinj.core.ECKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BitcoinTransactionSignerTest {

    private BitcoinTransactionSigner bitcoinTransactionSigner;
    private UnspentService unspentService;


    @BeforeEach
    void setUp() {
        unspentService = mock(UnspentService.class);
        bitcoinTransactionSigner = new BitcoinTransactionSigner(unspentService);
    }

    @Test
    void signsCorrectly() {
        when(unspentService.getUnspentForAddress(any(Address.class))).thenReturn(Arrays.asList(
                Unspent
                        .builder()
                        .amount(14482185)
                        .scriptPubKey("76a91464d12bfa319ec47fe040bf6d2dbb3013d85f552588ac")
                        .txId("0f496014bbb15adfd924a2b2ce8c16a4b6076240e4428d96b344d66adbb14c0b")
                        .vOut(1).build()));
        Signature signature = bitcoinTransactionSigner.createSignature(BitcoinTransactionSignable.builder()
                                                                                                 .satoshiValue(BigInteger.ONE)
                                                                                                 .address("mhSwuar1U3Hf6phh2LMkFefkjCgFt8Xg5H")
                                                                                                 .build(),
                                                                       BitcoinSecretKey.builder()
                                                                                       .key(ECKey.fromPrivate(Base58.decode("92JYtSuKyhrG1fVgtBXUQgT8yNGs6XFFCjz1XLCwg8jFM95GHB6")))
                                                                                       .build());

        assertThat(signature).isInstanceOf(TransactionSignature.class);
        assertThat(((TransactionSignature) signature).getSignedTransaction()).isEqualTo("01000000010b4cb1db6ad644b3968d42e4406207b6a4168cceb2a224d9df5ab1bb1460490f010000008a4730440220711a81110ec055d072c5769a2cde6aefc9024234670b4c46fe872b20e457daee0220448d49888fa817f73cd05e418a7bf42ed2b4c8b364c48cca75bde8ce9a509d9781410496e59446aed552e60fb29b6a9c6c71d7c1b38b8396e67d9940dbb867a70e314c591b20a0e59e36259439e1d0b6ae16975ca7097103d8d1ac987a02d121b5cd16ffffffff0201000000000000001976a91464d12bfa319ec47fe040bf6d2dbb3013d85f552588ac6874db00000000001976a91464d12bfa319ec47fe040bf6d2dbb3013d85f552588ac00000000");
    }

    @Test
    void cantSignWithoutUnspents() {
      assertThatThrownBy(() -> bitcoinTransactionSigner.createSignature(BitcoinTransactionSignable.builder()
                                                                                                 .satoshiValue(BigInteger.ONE)
                                                                                                 .address("mhSwuar1U3Hf6phh2LMkFefkjCgFt8Xg5H")
                                                                                                 .build(),
                                                                       BitcoinSecretKey.builder()
                                                                                       .key(ECKey.fromPrivate(Base58.decode("92JYtSuKyhrG1fVgtBXUQgT8yNGs6XFFCjz1XLCwg8jFM95GHB6")))
                                                                                       .build())).hasMessageContaining("The account you're trying to sign with doesn't have valid inputs to send the transaction");

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
        assertThatThrownBy(() -> bitcoinTransactionSigner.createSignature(BitcoinTransactionSignable.builder()
                                                                                              .satoshiValue(BigInteger.valueOf(14482195))
                                                                                              .address("mhSwuar1U3Hf6phh2LMkFefkjCgFt8Xg5H")
                                                                                              .build(),
                                                                    BitcoinSecretKey.builder()
                                                                                    .key(ECKey.fromPrivate(Base58.decode("92JYtSuKyhrG1fVgtBXUQgT8yNGs6XFFCjz1XLCwg8jFM95GHB6")))
                                                                                    .build())).hasMessageContaining("Not enough funds to send the transaction");

    }
}