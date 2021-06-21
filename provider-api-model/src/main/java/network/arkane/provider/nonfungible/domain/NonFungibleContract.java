package network.arkane.provider.nonfungible.domain;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true)
public class NonFungibleContract {

    private String name;
    private String description;
    private String address;
    private String symbol;
    @JsonAlias( {"externalUrl", "external_url"})
    private String url;
    @JsonAlias("image")
    private String imageUrl;
    private JsonNode media;
    private String banner;
    private String type;
    private boolean verified;
    private boolean premium;
    private List<String> categories;

}
