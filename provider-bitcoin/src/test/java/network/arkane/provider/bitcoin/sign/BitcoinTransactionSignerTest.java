package network.arkane.provider.bitcoin.sign;

import network.arkane.provider.bitcoin.BitcoinEnv;
import network.arkane.provider.bitcoin.secret.generation.BitcoinSecretKey;
import network.arkane.provider.bitcoin.unspent.Unspent;
import network.arkane.provider.bitcoin.unspent.UnspentService;
import network.arkane.provider.sign.domain.Signature;
import network.arkane.provider.sign.domain.TransactionSignature;
import network.arkane.provider.sochain.domain.Network;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Base58;
import org.bitcoinj.core.DumpedPrivateKey;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
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

class BitcoinTransactionSignerTest {

    private BitcoinTransactionSigner bitcoinTransactionSigner;
    private UnspentService unspentService;


    @BeforeEach
    void setUp() {
        unspentService = mock(UnspentService.class);
        bitcoinTransactionSigner = new BitcoinTransactionSigner(new BitcoinEnv(Network.BTCTEST, TestNet3Params.get()), unspentService);
    }

    @Test
    void signsCorrectly() {
        when(unspentService.getUnspentForAddress(any(Address.class))).thenReturn(Arrays.asList(
                Unspent
                        .builder()
                        .amount(14382185)
                        .scriptPubKey("76a91464d12bfa319ec47fe040bf6d2dbb3013d85f552588ac")
                        .txId("9b1b92f4c3c774c24d5477b30fc1eaaae62f004445f8b6504c9e0b29d09f4f6c")
                        .vOut(0).build()));
        Signature signature = bitcoinTransactionSigner.createSignature(BitcoinTransactionSignable.builder()
                                                                                                 .satoshiValue(BigInteger.ONE)
                                                                                                 .address("mhSwuar1U3Hf6phh2LMkFefkjCgFt8Xg5H")
                                                                                                 .build(),
                                                                       BitcoinSecretKey.builder()
                                                                                       .key(DumpedPrivateKey.fromBase58(NetworkParameters.testNet(), "92JYtSuKyhrG1fVgtBXUQgT8yNGs6XFFCjz1XLCwg8jFM95GHB6").getKey())
                                                                                       .build());

        assertThat(signature).isInstanceOf(TransactionSignature.class);
        assertThat(((TransactionSignature) signature).getSignedTransaction()).isEqualTo(
                "01000000016c4f9fd0290b9e4c50b6f84544002fe6aaeac10fb377544dc274c7c3f4921b9b000000008a473044022039ff93a8af80cdc589a3c6dade2c57563cc14dcbfab1b7e95f71b01539c1032d022014aad0562835aa369c2766115708448b0487f9f0d7236342d3e7de0cc434114d81410496e59446aed552e60fb29b6a9c6c71d7c1b38b8396e67d9940dbb867a70e314c591b20a0e59e36259439e1d0b6ae16975ca7097103d8d1ac987a02d121b5cd16ffffffff0201000000000000001976a91464d12bfa319ec47fe040bf6d2dbb3013d85f552588acc8edd900000000001976a91464d12bfa319ec47fe040bf6d2dbb3013d85f552588ac00000000");
    }

    @Test
    void cantSignWithoutUnspents() {
        assertThatThrownBy(() -> bitcoinTransactionSigner.createSignature(BitcoinTransactionSignable.builder()
                                                                                                    .satoshiValue(BigInteger.ONE)
                                                                                                    .address("mhSwuar1U3Hf6phh2LMkFefkjCgFt8Xg5H")
                                                                                                    .build(),
                                                                          BitcoinSecretKey.builder()
                                                                                          .key(ECKey.fromPrivate(Base58.decode(
                                                                                                  "92JYtSuKyhrG1fVgtBXUQgT8yNGs6XFFCjz1XLCwg8jFM95GHB6")))
                                                                                          .build())).hasMessageContaining(
                "The account you're trying to sign with doesn't have valid inputs to send the transaction");

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
                                                                                          .key(ECKey.fromPrivate(Base58.decode(
                                                                                                  "92JYtSuKyhrG1fVgtBXUQgT8yNGs6XFFCjz1XLCwg8jFM95GHB6")))
                                                                                          .build())).hasMessageContaining("Not enough funds to send the transaction");

    }

}