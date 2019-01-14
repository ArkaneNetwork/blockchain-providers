package network.arkane.provider.bitcoin.wallet.generation;

import lombok.Builder;
import network.arkane.provider.JSONUtil;
import network.arkane.provider.wallet.generation.GeneratedWallet;
import org.apache.commons.codec.binary.Base64;
import org.bitcoinj.crypto.EncryptedData;

@Builder
public class GeneratedBitcoinWallet implements GeneratedWallet {

    private String address;

    private BitcoinKeystore secret;

    public String getAddress() {
        return address;
    }

    public String secretAsBase64() {
        return Base64.encodeBase64String(JSONUtil.toJson(secret).getBytes());
    }

}
