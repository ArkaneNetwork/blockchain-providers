package network.arkane.provider.bitcoin.bridge;

import network.arkane.provider.bitcoin.BitcoinEnv;
import network.arkane.provider.bridge.TransactionGateway;
import network.arkane.provider.chain.SecretType;
import network.arkane.provider.sign.domain.Signature;
import network.arkane.provider.sign.domain.SubmittedAndSignedTransactionSignature;
import network.arkane.provider.sign.domain.TransactionSignature;
import network.arkane.provider.sochain.SoChainGateway;
import network.arkane.provider.sochain.domain.SendSignedTransactionResult;
import org.springframework.stereotype.Component;

@Component
public class BitcoinTransactionGateway implements TransactionGateway {

    private SoChainGateway soChainGateway;
    private BitcoinEnv bitcoinEnv;

    public BitcoinTransactionGateway(SoChainGateway soChainGateway, BitcoinEnv bitcoinEnv) {
        this.soChainGateway = soChainGateway;
        this.bitcoinEnv = bitcoinEnv;
    }

    @Override
    public Signature submit(TransactionSignature transactionSignature) {
        SendSignedTransactionResult submission = soChainGateway.sendSignedTransaction(bitcoinEnv.getNetwork(), transactionSignature.getSignedTransaction());
        return new SubmittedAndSignedTransactionSignature(submission.getTransactionId(), transactionSignature.getSignedTransaction());
    }

    @Override
    public SecretType getType() {
        return SecretType.BITCOIN;
    }
}
