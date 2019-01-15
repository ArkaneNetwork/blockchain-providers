package network.arkane.provider.sochain.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class Transaction {
    @JsonProperty("txid")
    private String transactionid;

    @JsonProperty("output_no")
    private Long outputNumber;

    @JsonProperty("script_asm")
    private String asmScript;

    @JsonProperty("script_hex")
    private String hexScript;

    @JsonProperty("value")
    private BigDecimal value;

    @JsonProperty("confirmations")
    private Long confirmations;

    @JsonProperty("time")
    private Long time;

    public String getTransactionid() {
        return transactionid;
    }

    public void setTransactionid(String transactionid) {
        this.transactionid = transactionid;
    }

    public Long getOutputNumber() {
        return outputNumber;
    }

    public void setOutputNumber(Long outputNumber) {
        this.outputNumber = outputNumber;
    }

    public String getAsmScript() {
        return asmScript;
    }

    public void setAsmScript(String asmScript) {
        this.asmScript = asmScript;
    }

    public String getHexScript() {
        return hexScript;
    }

    public void setHexScript(String hexScript) {
        this.hexScript = hexScript;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public Long getConfirmations() {
        return confirmations;
    }

    public void setConfirmations(Long confirmations) {
        this.confirmations = confirmations;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}
