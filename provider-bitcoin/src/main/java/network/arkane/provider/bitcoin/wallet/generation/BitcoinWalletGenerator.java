package network.arkane.provider.bitcoin.wallet.generation;

import network.arkane.provider.bitcoin.secret.generation.BitcoinSecretKey;
import network.arkane.provider.wallet.generation.WalletGenerator;
import org.apache.commons.codec.binary.Base64OutputStream;
import org.bitcoinj.core.Address;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.wallet.Wallet;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Component
public class BitcoinWalletGenerator implements WalletGenerator<BitcoinSecretKey> {

    @Override
    public GeneratedBitcoinWallet generateWallet(final String password, final BitcoinSecretKey secret) {
        if (StringUtils.isEmpty(password)) {
            throw new IllegalArgumentException("Password should not be empty");
        }
        Wallet wallet = secret.getWallet();
        Address address = wallet.currentReceiveAddress();
        wallet.encrypt(password);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (Base64OutputStream os = new Base64OutputStream(baos, true)) {
            wallet.saveToFileStream(os);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return GeneratedBitcoinWallet.builder()
                                     .address(address.toString())
                                     .secret(baos.toString())
                                     .build();
    }

    @Override
    public Class<BitcoinSecretKey> type() {
        return BitcoinSecretKey.class;
    }
}
