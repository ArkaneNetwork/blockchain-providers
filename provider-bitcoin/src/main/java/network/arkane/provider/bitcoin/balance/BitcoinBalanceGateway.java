package network.arkane.provider.bitcoin.balance;

import network.arkane.provider.PrecisionUtil;
import network.arkane.provider.balance.BalanceGateway;
import network.arkane.provider.balance.domain.Balance;
import network.arkane.provider.balance.domain.TokenBalance;
import network.arkane.provider.bitcoin.BitcoinEnv;
import network.arkane.provider.chain.SecretType;
import network.arkane.provider.sochain.SoChainGateway;
import network.arkane.provider.sochain.domain.BalanceResult;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class BitcoinBalanceGateway implements BalanceGateway {

    private SoChainGateway soChainGateway;
    private BitcoinEnv bitcoinEnv;

    public BitcoinBalanceGateway(SoChainGateway soChainGateway, BitcoinEnv bitcoinEnv) {
        this.soChainGateway = soChainGateway;
        this.bitcoinEnv = bitcoinEnv;
    }

    @Override
    public Balance getBalance(String address) {
        BalanceResult balance = soChainGateway.getBalance(bitcoinEnv.getNetwork(), address);
        BigDecimal confirmedBalance = balance.getConfirmedBalance();
        return Balance.builder()
                      .decimals(8)
                      .gasBalance(confirmedBalance == null ? 0 : confirmedBalance.doubleValue())
                      .balance(confirmedBalance == null ? 0 : confirmedBalance.doubleValue())
                      .rawGasBalance(confirmedBalance == null ? "0" : PrecisionUtil.toRaw(confirmedBalance, 8).toString())
                      .rawBalance(confirmedBalance == null ? "0" : PrecisionUtil.toRaw(confirmedBalance, 8).toString())
                      .secretType(SecretType.BITCOIN)
                      .gasSymbol("BTC")
                      .symbol("BTC")
                      .build();
    }

    @Override
    public TokenBalance getTokenBalance(String address, String tokenAddress) {
        throw new UnsupportedOperationException("Not possible for bitcoin");
    }

    @Override
    public List<TokenBalance> getTokenBalances(String address) {
        throw new UnsupportedOperationException("Not possible for bitcoin");
    }

    @Override
    public SecretType type() {
        return SecretType.BITCOIN;
    }
}
