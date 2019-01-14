package network.arkane.provider.bitcoin.secret.generation;

import network.arkane.provider.chain.SecretType;
import network.arkane.provider.secret.generation.SecretGenerator;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.springframework.stereotype.Component;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

@Component
public class BitcoinSecretGenerator implements SecretGenerator<BitcoinSecretKey> {

    @Override
    public BitcoinSecretKey generate() {
        try {
            final ECKeyPair ecKeyPair = Keys.createEcKeyPair();
            ECKey keyPair = ECKey.fromPrivate(ecKeyPair.getPrivateKey());
            return new BitcoinSecretKey(keyPair);
        } catch (InvalidAlgorithmParameterException | NoSuchAlgorithmException | NoSuchProviderException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public SecretType type() {
        return SecretType.BITCOIN;
    }
}
