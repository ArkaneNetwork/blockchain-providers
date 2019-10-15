package network.arkane.business.token.model;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Data
@NoArgsConstructor
public class TokenContract {

    private Long id;
    private String address;
    private boolean confirmed;
    private Date deployDate;
    private Date mineDate;
    private String description;
    private String applicationId;
    private String name;
    private String transactionHash;
    private String owner;

    @Builder
    public TokenContract(final Long id,
                         final String address,
                         final boolean confirmed,
                         final Date deployDate,
                         final Date mineDate,
                         final String description,
                         final String applicationId,
                         final String name,
                         final String transactionHash,
                         final String owner) {
        this.id = id;
        this.address = address;
        this.confirmed = confirmed;
        this.deployDate = deployDate;
        this.mineDate = mineDate;
        this.description = description;
        this.applicationId = applicationId;
        this.name = name;
        this.transactionHash = transactionHash;
        this.owner = owner;
    }
}
