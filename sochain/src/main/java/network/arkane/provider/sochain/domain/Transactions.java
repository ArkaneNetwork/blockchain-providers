package network.arkane.provider.sochain.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Transactions {
    @JsonProperty("network")
    private String network;

    @JsonProperty("address")
    private String address;

    @JsonProperty("txs")
    private List<Transaction> transactions;

    public String getNetwork() {
        return network;
    }

    public String getAddress() {
        return address;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }
}
