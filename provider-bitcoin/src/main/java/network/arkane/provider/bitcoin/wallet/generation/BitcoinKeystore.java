package network.arkane.provider.bitcoin.wallet.generation;


public class BitcoinKeystore {
    private byte[] pubKey;
    private byte[] initialisationVector;
    private byte[] encryptedBytes;
    private byte[] salt;

    public BitcoinKeystore() {
    }

    public BitcoinKeystore(byte[] pubKey, byte[] initialisationVector, byte[] encryptedBytes, byte[] salt) {
        this.pubKey = pubKey;
        this.initialisationVector = initialisationVector;
        this.encryptedBytes = encryptedBytes;
        this.salt = salt;
    }

    public byte[] getPubKey() {
        return pubKey;
    }

    public byte[] getInitialisationVector() {
        return initialisationVector;
    }

    public byte[] getEncryptedBytes() {
        return encryptedBytes;
    }

    public byte[] getSalt() {
        return salt;
    }
}
