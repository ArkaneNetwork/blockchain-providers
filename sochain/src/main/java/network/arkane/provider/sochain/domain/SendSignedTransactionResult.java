package network.arkane.provider.sochain.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SendSignedTransactionResult {

    private String network;

    @JsonProperty("tx_hex")
    private String transactionId;

    public SendSignedTransactionResult() {
    }

    public SendSignedTransactionResult(String network, String transactionId) {
        this.network = network;
        this.transactionId = transactionId;
    }

    public String getNetwork() {
        return network;
    }

    public String getTransactionId() {
        return transactionId;
    }
}
