package network.arkane.provider.tx;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigInteger;

@Getter
@AllArgsConstructor
@Builder
public class TxInfo {
    private String hash;
    private TxStatus status;
    private String from;
    private String to;
    private BigInteger confirmations;
    private String blockHash;
    private BigInteger blockNumber;

    protected TxInfo() {
    }
}