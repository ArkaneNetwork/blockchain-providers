package network.arkane.provider.bitcoin.unspent;

import network.arkane.provider.bitcoin.BitcoinEnv;
import network.arkane.provider.sochain.SoChainGateway;
import network.arkane.provider.sochain.domain.Transaction;
import org.bitcoinj.core.Address;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UnspentService {

    private SoChainGateway soChainGateway;
    private BitcoinEnv bitcoinEnv;

    public UnspentService(SoChainGateway soChainGateway, BitcoinEnv bitcoinEnv) {
        this.soChainGateway = soChainGateway;
        this.bitcoinEnv = bitcoinEnv;
    }

    public List<Unspent> getUnspentForAddress(final Address address) {
        return soChainGateway.getUnspentTransactions(bitcoinEnv.getNetwork(), address.toBase58())
                             .stream()
                             .map(this::toUnspent)
                             .collect(Collectors.toList());
    }

    private Unspent toUnspent(Transaction transaction) {
        return Unspent.builder()
                      .amount(transaction.getValue().multiply(BigDecimal.TEN.pow(8)).longValue())
                      .scriptPubKey(transaction.getHexScript())
                      .txId(transaction.getTransactionid())
                      .vOut(transaction.getOutputNumber())
                      .build();
    }
}
