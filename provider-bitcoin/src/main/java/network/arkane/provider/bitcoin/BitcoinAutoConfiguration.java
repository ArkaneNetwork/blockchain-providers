package network.arkane.provider.bitcoin;

import network.arkane.provider.sochain.domain.Network;
import org.apache.commons.lang3.StringUtils;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BitcoinAutoConfiguration {

    private NetworkParameters networkParameters;
    private Network network;

    public BitcoinAutoConfiguration(final @Value("${bitcoin.network}") String bitcoinNetwork) {
        if (StringUtils.isBlank(bitcoinNetwork)) {
            throw new IllegalArgumentException("Providing a bitcoin network is necessary (bitcoin.network)");
        }
        if (bitcoinNetwork.equals("testnet")) {
            this.networkParameters = TestNet3Params.get();
            this.network = Network.BTCTEST;
        } else {
            this.networkParameters = MainNetParams.get();
            this.network = Network.BTC;
        }
    }

    @Bean
    public BitcoinEnv bitcoinEnv() {
        return new BitcoinEnv(this.network, this.networkParameters);
    }

}
