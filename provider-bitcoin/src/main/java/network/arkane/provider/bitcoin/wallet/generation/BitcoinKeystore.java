package network.arkane.provider.bitcoin.wallet.generation;


import org.bitcoinj.crypto.EncryptedData;

public class BitcoinKeystore {
    private byte[] pubKey;
    private byte[] initialisationVector;
    private byte[] encryptedBytes;

    private EncryptedData encryptedData;

    public BitcoinKeystore() {
    }

    public BitcoinKeystore(byte[] pubKey, byte[] initialisationVector, byte[] encryptedBytes) {
        this.pubKey = pubKey;
        this.initialisationVector = initialisationVector;
        this.encryptedBytes = encryptedBytes;
    }

    public byte[] getPubKey() {
        return pubKey;
    }

    public void setPubKey(byte[] pubKey) {
        this.pubKey = pubKey;
    }

    public byte[] getInitialisationVector() {
        return initialisationVector;
    }

    public void setInitialisationVector(byte[] initialisationVector) {
        this.initialisationVector = initialisationVector;
    }

    public byte[] getEncryptedBytes() {
        return encryptedBytes;
    }

    public void setEncryptedBytes(byte[] encryptedBytes) {
        this.encryptedBytes = encryptedBytes;
    }

}
