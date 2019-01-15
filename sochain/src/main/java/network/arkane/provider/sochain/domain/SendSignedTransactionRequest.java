package network.arkane.provider.sochain.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SendSignedTransactionRequest {

    @JsonProperty("tx_hex")
    private String signedTransactionAsHex;

    public SendSignedTransactionRequest() {
    }

    public SendSignedTransactionRequest(String signedTransactionAsHex) {
        this.signedTransactionAsHex = signedTransactionAsHex;
    }

    public String getSignedTransactionAsHex() {
        return signedTransactionAsHex;
    }
}
