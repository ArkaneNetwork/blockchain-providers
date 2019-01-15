package network.arkane.provider.sochain.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class BalanceResult extends SoChainResult<BalanceResult> {
    private String network;
    private String address;
    @JsonProperty("confirmed_balance")
    private BigDecimal confirmedBalance;
    @JsonProperty("unconfirmed_balance")
    private BigDecimal unconfirmedBalance;

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public BigDecimal getConfirmedBalance() {
        return confirmedBalance;
    }

    public void setConfirmedBalance(BigDecimal confirmedBalance) {
        this.confirmedBalance = confirmedBalance;
    }

    public BigDecimal getUnconfirmedBalance() {
        return unconfirmedBalance;
    }

    public void setUnconfirmedBalance(BigDecimal unconfirmedBalance) {
        this.unconfirmedBalance = unconfirmedBalance;
    }
}
