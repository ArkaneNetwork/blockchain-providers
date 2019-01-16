package network.arkane.provider.bitcoin;

import network.arkane.provider.sochain.domain.Network;
import org.bitcoinj.core.NetworkParameters;

public class BitcoinEnv {
    private Network network;
    private NetworkParameters networkParameters;

    public BitcoinEnv(Network network, NetworkParameters networkParameters) {
        this.network = network;
        this.networkParameters = networkParameters;
    }

    public Network getNetwork() {
        return network;
    }

    public NetworkParameters getNetworkParameters() {
        return networkParameters;
    }
}
