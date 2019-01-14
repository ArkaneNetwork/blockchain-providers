package network.arkane.provider.bitcoin.sign;

import network.arkane.provider.bitcoin.secret.generation.BitcoinSecretKey;
import network.arkane.provider.sign.Signer;
import network.arkane.provider.sign.domain.Signature;
import org.apache.commons.codec.binary.Base64InputStream;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.Coin;
import org.bitcoinj.core.InsufficientMoneyException;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.wallet.SendRequest;
import org.bitcoinj.wallet.UnreadableWalletException;
import org.bitcoinj.wallet.Wallet;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;

//https://medium.com/programmers-blockchain/creating-your-first-blockchain-with-java-part-2-transactions-2cdac335e0ce
//https://github.com/ValleZ/Paper-Wallet
@Component
public class BitcoinTransactionSigner implements Signer<BitcoinTransactionSignable, BitcoinSecretKey> {

    private NetworkParameters networkParameters;

    public BitcoinTransactionSigner(NetworkParameters networkParameters) {
        this.networkParameters = networkParameters;
    }

    @Override
    public Signature createSignature(BitcoinTransactionSignable signable, BitcoinSecretKey key) {
        Wallet wallet = key.getWallet();
        wallet.reset();
        Address address = Address.fromBase58(networkParameters, signable.getAddress());
        Coin value = Coin.valueOf(signable.getSatoshiValue().longValue());
        SendRequest sendRequest = SendRequest.to(address, value);
        try {
            wallet.completeTx(sendRequest);
        } catch (InsufficientMoneyException e) {
            e.printStackTrace();
        }
        wallet.signTransaction(sendRequest);

        return null;
    }

    public void blah() {
//        Address address2 = Address.fromBase58(networkParameters, address);
//
//        Transaction tx = new Transaction(params);
//        //value is a sum of all inputs, fee is 4013
//        tx.addOutput(Coin.valueOf(amount-4013), address2);
//
//        //utxos is an array of inputs from my wallet
//        for(UTXO utxo : utxos)
//        {
//            TransactionOutPoint outPoint = new TransactionOutPoint(networkParameters, utxo.getIndex(), utxo.getHash());
//            //YOU HAVE TO CHANGE THIS
//            tx.addSignedInput(outPoint, utxo.getScript(), key, Transaction.SigHash.ALL, true);
//        }
//
//        tx.getConfidence().setSource(TransactionConfidence.Source.SELF);
//        tx.setPurpose(Transaction.Purpose.USER_PAYMENT);
    }

    @Override
    public BitcoinSecretKey reconstructKey(String secret, String password) {
        try {
            Wallet wallet = Wallet.loadFromFileStream(new Base64InputStream(new ByteArrayInputStream(secret.getBytes())));
            wallet.decrypt(password);
            return BitcoinSecretKey.builder()
                                   .wallet(wallet)
                                   .build();
        } catch (UnreadableWalletException e) {
            throw new RuntimeException(e);
        }
    }

//    public void yan() {
//        final SimpleSpendTransactionBuilder builder = new SimpleSpendTransactionBuilder();
//        builder.sendToRecipient(Value.fromString("0.1BTC"), Address.fromAddressString("12rsrcBjfx3wKxb4VNK5Ajt1JMVVYz3SYr"));
//        builder.sendToRecipient(Value.fromString("5mBTC"), Address.fromAddressString( "15hKjJscmhquUvoHT997Mehq6sYpvjNHNb"));
//        builder.
//        final Transaction trans = builder.build();
//    }
    //    private static final String DEFAULT_DATA = "0x";
    //
    //    private EthereumWalletDecryptor ethereumWalletDecryptor;
    //
    //    public BitcoinTransactionSigner(final EthereumWalletDecryptor ethereumWalletDecryptor) {
    //        this.ethereumWalletDecryptor = ethereumWalletDecryptor;
    //    }
    //
    //    @Override
    //    public Signature createSignature(EthereumTransactionSignable signable, EthereumSecretKey key) {
    //        final org.web3j.crypto.RawTransaction rawTransaction = constructTransaction(signable);
    //        byte[] encodedMessage = TransactionEncoder.signMessage(rawTransaction, Credentials.create(key.getKeyPair()));
    //        final String prettify = BytesUtils.withHexPrefix(Hex.toHexString(encodedMessage), Prefix.ZeroLowerX);
    //        return TransactionSignature
    //                .signTransactionBuilder()
    //                .signedTransaction(prettify)
    //                .build();
    //    }
    //
    //    @Override
    //    public EthereumSecretKey reconstructKey(String secret, String password) {
    //        return ethereumWalletDecryptor.generateKey(GeneratedEthereumWallet.builder()
    //                                                                          .walletFile(JSONUtil.fromJson(secret, WalletFile.class))
    //                                                                          .build(), password);
    //    }
    //
    //    private org.web3j.crypto.RawTransaction constructTransaction(final EthereumTransactionSignable signTransactionRequest) {
    //        return org.web3j.crypto.RawTransaction.createTransaction(
    //                signTransactionRequest.getNonce(),
    //                signTransactionRequest.getGasPrice(),
    //                signTransactionRequest.getGasLimit(),
    //                signTransactionRequest.getTo(),
    //                signTransactionRequest.getValue(),
    //                StringUtils.isBlank(signTransactionRequest.getData()) ? DEFAULT_DATA : signTransactionRequest.getData());
    //    }
}
