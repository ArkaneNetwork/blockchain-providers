package network.arkane.provider.bitcoin.balance;

import network.arkane.provider.balance.domain.Balance;
import network.arkane.provider.bitcoin.BitcoinEnv;
import network.arkane.provider.chain.SecretType;
import network.arkane.provider.sochain.SoChainGateway;
import network.arkane.provider.sochain.domain.BalanceResult;
import network.arkane.provider.sochain.domain.Network;
import org.bitcoinj.params.TestNet3Params;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class BitcoinBalanceGatewayTest {

    private SoChainGateway soChainGateway;
    private BitcoinBalanceGateway bitcoinBalanceGateway;

    @BeforeEach
    void setUp() {
        soChainGateway = mock(SoChainGateway.class);
        bitcoinBalanceGateway = new BitcoinBalanceGateway(soChainGateway, new BitcoinEnv(Network.BTCTEST, TestNet3Params.get()));
    }

    @Test
    void emptyBalance() {
        String address = "mhxqpVGP4AiYNsmFwDGqBPaNimNZkqCX8o";
        BalanceResult balanceResult = new BalanceResult();
        balanceResult.setAddress(address);
        balanceResult.setConfirmedBalance(null);
        when(soChainGateway.getBalance(Network.BTCTEST, address)).thenReturn(balanceResult);

        Balance result = bitcoinBalanceGateway.getBalance(address);

        assertThat(result.getBalance()).isEqualTo(0);
        assertThat(result.getGasBalance()).isEqualTo(0);
        assertThat(result.getRawBalance()).isEqualTo("0");
        assertThat(result.getRawGasBalance()).isEqualTo("0");
        assertThat(result.getSecretType()).isEqualTo(SecretType.BITCOIN);
        assertThat(result.getDecimals()).isEqualTo(8);
        assertThat(result.getSymbol()).isEqualTo("BTC");
        assertThat(result.getGasSymbol()).isEqualTo("BTC");
    }

    @Test
    void withBalance() {
        String address = "mhxqpVGP4AiYNsmFwDGqBPaNimNZkqCX8o";
        BalanceResult balanceResult = new BalanceResult();
        balanceResult.setAddress(address);
        balanceResult.setConfirmedBalance(new BigDecimal("1.38478048"));
        when(soChainGateway.getBalance(Network.BTCTEST, address)).thenReturn(balanceResult);

        Balance result = bitcoinBalanceGateway.getBalance(address);

        assertThat(result.getBalance()).isEqualTo(1.38478048);
        assertThat(result.getGasBalance()).isEqualTo(1.38478048);
        assertThat(result.getRawBalance()).isEqualTo("138478048");
        assertThat(result.getRawGasBalance()).isEqualTo("138478048");
        assertThat(result.getSecretType()).isEqualTo(SecretType.BITCOIN);
        assertThat(result.getDecimals()).isEqualTo(8);
        assertThat(result.getSymbol()).isEqualTo("BTC");
        assertThat(result.getGasSymbol()).isEqualTo("BTC");
    }
}