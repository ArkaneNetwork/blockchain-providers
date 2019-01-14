package network.arkane.provider.bitcoin.wallet.generation;

import lombok.Builder;
import network.arkane.provider.wallet.generation.GeneratedWallet;
import org.apache.commons.codec.binary.Base64InputStream;
import org.bitcoinj.wallet.UnreadableWalletException;
import org.bitcoinj.wallet.Wallet;

import java.io.ByteArrayInputStream;

@Builder
public class GeneratedBitcoinWallet implements GeneratedWallet {

    private String address;

    private Wallet walletFile;

    private String secret;

    public String getAddress() {
        return address;
    }

    public String secretAsBase64() {
        return secret;
    }

    public Wallet getWalletFile() {
        try {
            return Wallet.loadFromFileStream(new Base64InputStream(new ByteArrayInputStream(secret.getBytes())));
        } catch (UnreadableWalletException e) {
            throw new RuntimeException(e);
        }
    }
}
